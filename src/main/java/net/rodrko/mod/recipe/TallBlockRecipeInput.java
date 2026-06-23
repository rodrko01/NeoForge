package net.rodrko.mod.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record TallBlockRecipeInput(ItemStack slot0,
                                   ItemStack slot1,
                                   ItemStack slot2,
                                   ItemStack slot3,
                                   ItemStack slot4,
                                   ItemStack slot5) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.slot0;
            case 1 -> this.slot1;
            case 2 -> this.slot2;
            case 3 -> this.slot3;
            case 4 -> this.slot4;
            case 5 -> this.slot5;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 6;
    }
}