package com.jackson.tricksandtrials.client.keybinds;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.item.ModItems;
import com.jackson.tricksandtrials.item.custom.WarriorsBow;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = TricksandTrials.MODID, value = Dist.CLIENT)
public class KeyBindHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent shrek)
    {
     Minecraft MC = Minecraft.getInstance();
     Player Shreker = MC.player;
     if (Shreker != null && KeyBindRegistry.BEGINSHREKING.consumeClick())
     {
         ItemStack Donkey = Shreker.getMainHandItem();
         ItemStack Dragon = Shreker.getOffhandItem();
         if (Donkey.getItem()instanceof WarriorsBow WB){
            WB.toggleMode();
         }
         if (Dragon.getItem()instanceof WarriorsBow WB){
             WB.toggleMode();
         }
     }
    }
}
