package net.rodrko.mod.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.rodrko.mod.StinkyMod;

@EventBusSubscriber(modid = StinkyMod.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        // remove when adding actual client events (keybinds)
    }
}
