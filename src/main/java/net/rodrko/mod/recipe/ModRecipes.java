package net.rodrko.mod.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rodrko.mod.StinkyMod;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, StinkyMod.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, StinkyMod.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TallBlockRecipe>> TALL_BLOCK_SERIALIZER =
            SERIALIZERS.register("tall_block_crafting", TallBlockRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TallBlockRecipe>> TALL_BLOCK_TYPE =
            TYPES.register("tall_block_crafting", () -> new RecipeType<TallBlockRecipe>() {
                @Override
                public String toString() {
                    return "tall_block_crafting";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
