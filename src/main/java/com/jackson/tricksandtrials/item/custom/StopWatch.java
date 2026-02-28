package com.jackson.tricksandtrials.item.custom;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StopWatch extends Item {
    public StopWatch(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        if (level.isClientSide)
            return super.use(level, player, usedHand);
        MinecraftServer server = level.getServer();
        CommandSourceStack Freeze = server.createCommandSourceStack();
        if (level.tickRateManager().isFrozen()){
        server.getCommands().performPrefixedCommand(Freeze,"tick unfreeze");
        } else {
            server.getCommands().performPrefixedCommand(Freeze,"tick freeze");
        }
        return super.use(level, player, usedHand);
    }
}
