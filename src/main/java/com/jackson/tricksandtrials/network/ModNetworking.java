package com.jackson.tricksandtrials.network;

import com.jackson.tricksandtrials.item.custom.WarriorsBow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import com.jackson.tricksandtrials.TricksandTrials;

@EventBusSubscriber(modid = TricksandTrials.MODID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                ToggleBowModePayload.TYPE,
                ToggleBowModePayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (payload, context) -> {}, // client -> ignore
                        (payload, context) -> handleOnServer(payload, context.player())
                )
        );
    }

    private static void handleOnServer(ToggleBowModePayload payload, net.minecraft.world.entity.player.Player player) {
        if (!(player instanceof ServerPlayer)) return;

        ItemStack stack = payload.isMainHand()
                ? player.getMainHandItem()
                : player.getOffhandItem();

        if (stack.getItem() instanceof WarriorsBow wb) {
            wb.toggleMode(stack);
        }
    }
}
