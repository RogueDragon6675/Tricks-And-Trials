package com.jackson.tricksandtrials.client.keybinds;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.item.custom.WarriorsBow;
import com.jackson.tricksandtrials.network.ToggleBowModePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = TricksandTrials.MODID, value = Dist.CLIENT)
public class KeyBindHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null && KeyBindRegistry.BEGINSHREKING.consumeClick()) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            if (mainHand.getItem() instanceof WarriorsBow) {
                PacketDistributor.sendToServer(new ToggleBowModePayload(true));
            } else if (offHand.getItem() instanceof WarriorsBow) {
                PacketDistributor.sendToServer(new ToggleBowModePayload(false));
            }
        }
    }
}