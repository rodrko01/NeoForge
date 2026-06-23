package net.rodrko.mod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record TallBlockRecipe(
        Ingredient slot0, int slot0Count,
        Ingredient slot1, int slot1Count,
        Ingredient slot2, int slot2Count,
        Ingredient slot3, int slot3Count,
        Ingredient slot4, int slot4Count,
        Ingredient slot5, int slot5Count,
        ItemStack outputSlot3, ItemStack outputSlot4, ItemStack outputSlot5) implements Recipe<TallBlockRecipeInput> // constructor has slot counts as parameters to keep track
{ // each slot is in the record parameter
    // inputItem & output ==> Read From JSON File!
    // GrowthChamberRecipeInput --> INVENTORY of the Block Entity

    @Override
    public NonNullList<Ingredient> getIngredients() { // returns list of ingredients
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(slot0); // input 1
        list.add(slot1); // input 2
        list.add(slot2); // input 3
        list.add(slot3); // input 4
        list.add(slot4);
        list.add(slot5);

        return list;
    }

//    @Override
//    public boolean matches(TallBlockRecipeInput tallBlockRecipeInput, Level level) {
//        if (level.isClientSide()) {
//            return false;
//        }
//        // check fuel, bottles, and component
//        if (!matchesWithCount(tallBlockRecipeInput.getItem(0), slot0, slot0Count)) return false;
//        if (!matchesWithCount(tallBlockRecipeInput.getItem(1), slot1, slot1Count)) return false;
//        if (!matchesWithCount(tallBlockRecipeInput.getItem(2), slot2, slot2Count)) return false;
//
//        // check items to upgrade
//        if (!matchesWithCount(tallBlockRecipeInput.getItem(3), slot3, slot3Count)) return false;
//        if (!matchesWithCount(tallBlockRecipeInput.getItem(4), slot4, slot4Count)) return false;
//        if (!matchesWithCount(tallBlockRecipeInput.getItem(5), slot5, slot5Count)) return false;
//
//        return true;
//    }
public boolean matches(TallBlockRecipeInput input, Level level) {
    if (level.isClientSide()) {
        return false;
    }
// for debugging
//    System.out.println("=== MATCHING RECIPE ===");
//    System.out.println("Slot 0: expected " + slot0().getItems()[0].getItem() + " x" + slot0Count() + " got " + input.getItem(0));
//    System.out.println("Slot 1: expected " + slot1().getItems()[0].getItem() + " x" + slot1Count() + " got " + input.getItem(1));
//    System.out.println("Slot 2: expected " + slot2().getItems()[0].getItem() + " x" + slot2Count() + " got " + input.getItem(2));
//    System.out.println("Slot 3: expected " + slot3().getItems()[0].getItem() + " x" + slot3Count() + " got " + input.getItem(3));
//    System.out.println("Slot 4: expected " + slot4().getItems()[0].getItem() + " x" + slot4Count() + " got " + input.getItem(4));
//    System.out.println("Slot 5: expected " + slot5().getItems()[0].getItem() + " x" + slot5Count() + " got " + input.getItem(5));

    // Check each slot (IGNORE FUEL (SLOT 0))
    if (!matchesWithCount(input.getItem(1), slot1, slot1Count)) {
//        System.out.println("Slot 1 failed");
        return false;
    }
    if (!matchesWithCount(input.getItem(2), slot2, slot2Count)) {
//        System.out.println("Slot 2 failed");
        return false;
    }
    if (!matchesWithCount(input.getItem(3), slot3, slot3Count)) {
//        System.out.println("Slot 3 failed");
        return false;
    }
    if (!matchesWithCount(input.getItem(4), slot4, slot4Count)) {
//        System.out.println("Slot 4 failed");
        return false;
    }
    if (!matchesWithCount(input.getItem(5), slot5, slot5Count)) {
//        System.out.println("Slot 5 failed");
        return false;
    }
    return true;
}
    // helper method for matches
    private boolean matchesWithCount(ItemStack inputStack, Ingredient ingredient, int requiredCount) {
        return ingredient.test(inputStack) && inputStack.getCount() >= requiredCount;
    }

    @Override
    public @NotNull ItemStack assemble(TallBlockRecipeInput tallBlockRecipeInput, HolderLookup.Provider provider) {
        return outputSlot3.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return outputSlot3.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TALL_BLOCK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TALL_BLOCK_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TallBlockRecipe> {
        public static final MapCodec<TallBlockRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group( // change codec to read multiple ingredients and each ingredients counts
                Ingredient.CODEC_NONEMPTY.fieldOf("slot0").forGetter(TallBlockRecipe::slot0),
                Codec.INT.fieldOf("slot0_count").orElse(1).forGetter(TallBlockRecipe::slot0Count),
                Ingredient.CODEC_NONEMPTY.fieldOf("slot1").forGetter(TallBlockRecipe::slot1),
                Codec.INT.fieldOf("slot1_count").orElse(1).forGetter(TallBlockRecipe::slot1Count),
                Ingredient.CODEC_NONEMPTY.fieldOf("slot2").forGetter(TallBlockRecipe::slot2),
                Codec.INT.fieldOf("slot2_count").orElse(1).forGetter(TallBlockRecipe::slot2Count),
                Ingredient.CODEC_NONEMPTY.fieldOf("slot3").forGetter(TallBlockRecipe::slot3),
                Codec.INT.fieldOf("slot3_count").orElse(1).forGetter(TallBlockRecipe::slot3Count),
                Ingredient.CODEC_NONEMPTY.fieldOf("slot4").forGetter(TallBlockRecipe::slot4),
                Codec.INT.fieldOf("slot4_count").orElse(1).forGetter(TallBlockRecipe::slot4Count),
                Ingredient.CODEC_NONEMPTY.fieldOf("slot5").forGetter(TallBlockRecipe::slot5),
                Codec.INT.fieldOf("slot5_count").orElse(1).forGetter(TallBlockRecipe::slot5Count),
                // upgraded item outputs
                ItemStack.CODEC.fieldOf("output_slot3").forGetter(TallBlockRecipe::outputSlot3),
                ItemStack.CODEC.fieldOf("output_slot4").forGetter(TallBlockRecipe::outputSlot4),
                ItemStack.CODEC.fieldOf("output_slot5").forGetter(TallBlockRecipe::outputSlot5)
        ).apply(inst, TallBlockRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TallBlockRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.slot0());
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.slot1());
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.slot2());
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.slot3());
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.slot4());
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.slot5());

                            buf.writeInt(recipe.slot0Count());
                            buf.writeInt(recipe.slot1Count());
                            buf.writeInt(recipe.slot2Count());
                            buf.writeInt(recipe.slot3Count());
                            buf.writeInt(recipe.slot4Count());
                            buf.writeInt(recipe.slot5Count());

                            ItemStack.STREAM_CODEC.encode(buf, recipe.outputSlot3());
                            ItemStack.STREAM_CODEC.encode(buf, recipe.outputSlot4());
                            ItemStack.STREAM_CODEC.encode(buf, recipe.outputSlot5());
                        },
                        (buf) -> {
                            Ingredient slot0 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                            Ingredient slot1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                            Ingredient slot2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                            Ingredient slot3 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                            Ingredient slot4 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                            Ingredient slot5 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);

                            int slot0Count = buf.readInt();
                            int slot1Count = buf.readInt();
                            int slot2Count = buf.readInt();
                            int slot3Count = buf.readInt();
                            int slot4Count = buf.readInt();
                            int slot5Count = buf.readInt();

                            ItemStack outputSlot3 = ItemStack.STREAM_CODEC.decode(buf);
                            ItemStack outputSlot4 = ItemStack.STREAM_CODEC.decode(buf);
                            ItemStack outputSlot5 = ItemStack.STREAM_CODEC.decode(buf);
                            return new TallBlockRecipe(slot0, slot0Count, slot1, slot1Count,
                                    slot2, slot2Count, slot3, slot3Count, slot4, slot4Count,
                                    slot5, slot5Count, outputSlot3, outputSlot4, outputSlot5);
                        }
                );

        @Override
        public MapCodec<TallBlockRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TallBlockRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
