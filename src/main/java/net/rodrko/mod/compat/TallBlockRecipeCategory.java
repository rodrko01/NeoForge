package net.rodrko.mod.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.rodrko.mod.StinkyMod;
import net.rodrko.mod.block.ModBlocks;
import net.rodrko.mod.recipe.TallBlockRecipe;
import org.jetbrains.annotations.Nullable;

public class TallBlockRecipeCategory  implements IRecipeCategory<TallBlockRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "tall_block");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "textures/gui/tall_block/tall_block_gui.png");


    public static final RecipeType<TallBlockRecipe> TALL_BLOCK_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, TallBlockRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public TallBlockRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0,0, 176, 112);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.TALL_BLOCK));
    }

    @Override
    public RecipeType<TallBlockRecipe> getRecipeType() {
        return TALL_BLOCK_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("tall block");
        //TODO
       // return Component.translatable("block.{modname}.{blockname}"); // block.{modname}.{blockname} replace {} with ur directory names
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TallBlockRecipe recipe, IFocusGroup focuses) { // TallBlockRecipe -> {blockname}Recipe
        // for each ingredient
        builder.addSlot(RecipeIngredientRole.INPUT, 33, 81).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 79, 74).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 79, 16).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 59, 37).addIngredients(recipe.getIngredients().get(3));
        builder.addSlot(RecipeIngredientRole.INPUT, 79, 45).addIngredients(recipe.getIngredients().get(4));
        builder.addSlot(RecipeIngredientRole.INPUT, 99, 37).addIngredients(recipe.getIngredients().get(5));

        // each output but they go over input... where go
        builder.addSlot(RecipeIngredientRole.OUTPUT, 20, 56).addItemStack(recipe.getResultItem(null));
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 79, 45).addItemStack(recipe.getResultItem(null));
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 37).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }
}
