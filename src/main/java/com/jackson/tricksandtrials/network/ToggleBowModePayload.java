package com.jackson.tricksandtrials.network;

import com.jackson.tricksandtrials.TricksandTrials;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleBowModePayload(boolean isMainHand) implements CustomPacketPayload {

    public static final Type<ToggleBowModePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TricksandTrials.MODID, "toggle_bow_mode"));

    public static final StreamCodec<FriendlyByteBuf, ToggleBowModePayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeBoolean(payload.isMainHand()),
                    buf -> new ToggleBowModePayload(buf.readBoolean())
            );

    @Override
    public Type<ToggleBowModePayload> type() {
        return TYPE;
    }
}
