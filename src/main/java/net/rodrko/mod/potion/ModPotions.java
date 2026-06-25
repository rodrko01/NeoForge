package net.rodrko.mod.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rodrko.mod.StinkyMod;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, StinkyMod.MOD_ID);

    public static final Holder<Potion> GAY_POTION = POTIONS.register("gay_potion",
            () -> new Potion(
                    new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0), // each instance is an effect
                    new MobEffectInstance(MobEffects.JUMP, 1200, 0))); // 0 is amplifier

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
