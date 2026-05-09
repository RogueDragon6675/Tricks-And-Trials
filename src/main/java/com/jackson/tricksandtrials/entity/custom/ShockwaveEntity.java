package com.jackson.tricksandtrials.entity.custom;

import com.jackson.tricksandtrials.entity.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShockwaveEntity extends Entity {

    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(ShockwaveEntity.class, EntityDataSerializers.FLOAT);

    // How far the entity can drop from its spawn Y before it despawns (cliff edge)
    private static final float MAX_DROP = 4.0f;

    private int   lifetime;
    private float damage;
    private float speed;
    private float growthRate;
    private Vec3  direction;
    private boolean isCircleMode;
    private float spawnY;
    private int age = 0;

    private float forceY;
    private float forceXZ;

    public float getLifetimePercent() {
        return ((float)age / lifetime);
    }

    private final Set<UUID> hitEntities = new HashSet<>();

    public ShockwaveEntity(EntityType<? extends ShockwaveEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static ShockwaveEntity create(Level level, LivingEntity owner,
                                          Vec3 spawnPos, Vec3 direction,
                                          float damage, float speed, float growthRate,
                                          int lifetime, float forceXZ, float forceY,  boolean isCircleMode) {
        ShockwaveEntity e = new ShockwaveEntity(ModEntities.SHOCKWAVE.get(), level);
        e.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        e.direction    = direction.normalize();
        e.damage       = damage;
        e.speed        = speed;
        e.growthRate   = growthRate;
        e.lifetime     = lifetime;
        e.isCircleMode = isCircleMode;
        e.spawnY       = (float) spawnPos.y;
        e.forceXZ = forceXZ;
        e.forceY = forceY;
        e.setRadius(0.3f);
        return e;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_RADIUS, 0.3f);
    }

    @Override
    public void tick() {
        super.tick();
        age++;

        if (age >= lifetime) {
            discard();
            return;
        }

        // ── grow ──
        setRadius(getRadius() + growthRate);

        // ── move forward + ground hug (line mode) or just ground hug (circle mode) ──
        if (direction != null) {
            Vec3 next = isCircleMode
                    ? position()                              // circle stays in place, just re-snaps each tick
                    : position().add(direction.scale(speed)); // line travels forward

            int groundY = level().getHeight(
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (int) next.x, (int) next.z);

            // Despawn if it fell off a cliff
            if (spawnY - groundY > MAX_DROP) {
                discard();
                return;
            }

            setPos(next.x, groundY, next.z);
        }

        // ── particles (server → broadcasts to all nearby clients) ──
        if (level() instanceof ServerLevel server) {
            spawnParticlesServer(server);
        }

        // ── damage ──
        if (!level().isClientSide()) {
            float r = getRadius();
            AABB box = new AABB(getX() - r, getY() - 1, getZ() - r,
                                getX() + r, getY() + 2, getZ() + r);
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box)) {
                if (hitEntities.contains(target.getUUID())) continue;
                double dx = target.getX() - getX();
                double dz = target.getZ() - getZ();
                if (dx * dx + dz * dz > (double)(r * r)) continue;

                float fallOffPerc = 1 - getLifetimePercent();
                float damageCalc =damage * fallOffPerc;
                float pushBack = forceXZ * fallOffPerc;
                float pushUp = forceY  * fallOffPerc;

                target.hurt(level().damageSources().magic(), damageCalc);
                Vec3 push = new Vec3(dx, 0, dz).normalize().scale(pushBack);
                push = push.add(0,pushUp, 0);

                target.setDeltaMovement(target.getDeltaMovement().add(push));
                hitEntities.add(target.getUUID());
            }
        }
    }

    private void spawnParticlesServer(ServerLevel server) {
        float r   = getRadius();
        double cx = getX();
        double cy = getY() + 0.15;
        double cz = getZ();

        // ── outer ring ──
        // Circle mode uses fewer points — 12 entities already form the ring shape
        int ringCount = isCircleMode
                ? Math.max(4, (int)(r * 5))
                : Math.max(12, (int)(r * 18));
        for (int i = 0; i < ringCount; i++) {
            double angle = (2.0 * Math.PI * i) / ringCount;
            double cos   = Math.cos(angle);
            double sin   = Math.sin(angle);

            double px = cx + r * cos;
            double pz = cz + r * sin;

            // Outward radial velocity so particles spray away from the edge
            double vx = cos * 0.06;
            double vz = sin * 0.06;

            server.sendParticles(
                   ParticleTypes.SOUL_FIRE_FLAME,
                    px, cy, pz,
                    1,          // count (1 so we control exact position per call)
                    0, 0, 0,    // no random spread — position is already exact
                    0.0         // speed passed to particle (0 = use our vx/vy/vz... but sendParticles doesn't take vx separately)
            );

            // sendParticles doesn't support per-particle velocity, so spawn a second
            // pass using addParticle via the level for the velocity-driven spray
            server.sendParticles(
                    ParticleTypes.SMOKE,
                    px, cy, pz,
                    1, vx, 0.05, vz, 0.5
            );
        }

        // ── dense fill: mass of particles inside the disc ──
        // Circle mode is much sparser — 12 entities × fill = too many particles otherwise
        int fillCount = isCircleMode
                ? Math.min((int)(r * r * 1), 6)
                : Math.min((int)(r * r * 4), 40);
        for (int i = 0; i < fillCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double dist  = random.nextDouble() * r;
            double px    = cx + dist * Math.cos(angle);
            double pz    = cz + dist * Math.sin(angle);

            server.sendParticles(
                     ParticleTypes.SOUL_FIRE_FLAME,
                    px, cy - 0.05, pz,
                    1, 0, 0.02, 0, 0.3
            );
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        lifetime     = tag.getInt("Lifetime");
        damage       = tag.getFloat("Damage");
        speed        = tag.getFloat("Speed");
        growthRate   = tag.getFloat("GrowthRate");
        isCircleMode = tag.getBoolean("CircleMode");
        age          = tag.getInt("Age");
        spawnY       = tag.getFloat("SpawnY");
        direction    = new Vec3(tag.getDouble("DirX"), 0, tag.getDouble("DirZ")).normalize();
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        tag.putInt("Lifetime",       lifetime);
        tag.putFloat("Damage",       damage);
        tag.putFloat("Speed",        speed);
        tag.putFloat("GrowthRate",   growthRate);
        tag.putBoolean("CircleMode", isCircleMode);
        tag.putInt("Age",            age);
        tag.putFloat("SpawnY",       spawnY);
        if (direction != null) {
            tag.putDouble("DirX", direction.x);
            tag.putDouble("DirZ", direction.z);
        }
    }

    public float   getRadius()     { return entityData.get(DATA_RADIUS); }
    public int     getAge()        { return age; }
    public int     getLifetime()   { return lifetime; }
    public boolean isCircleMode()  { return isCircleMode; }

    public void setRadius(float r) {
        entityData.set(DATA_RADIUS, r);
        setBoundingBox(new AABB(getX() - r, getY() - 0.5, getZ() - r,
                                getX() + r, getY() + 0.5, getZ() + r));
    }

    @Override public boolean isPickable()    { return false; }
    @Override public boolean shouldBeSaved() { return false; }
}
