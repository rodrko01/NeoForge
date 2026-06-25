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
import net.minecraft.client.Minecraft;
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
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "textures/gui/tall_block/tall_block_gui_jei.png");


    public static final RecipeType<TallBlockRecipe> TALL_BLOCK_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, TallBlockRecipe.class);
    private static final ResourceLocation OVAL_FILLED =
            ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "textures/gui/oval.png");
    private static final ResourceLocation WHITE_BARS =
            ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "textures/gui/whitebar.png");

    // added oval and bars
    private static final int OVAL_X = 78;
    private static final int OVAL_Y = 93;
    private static final int OVAL_HEIGHT = 6;
    private static final int OVAL_WIDTH = 18;

    private static final int RIGHT_BAR_X = 120;
    private static final int RIGHT_BAR_Y = 71; // bottom of bar

    private static final int RIGHT_BAR2_X = 120;
    private static final int RIGHT_BAR2_Y = 18; // top of bar

    private static final int LEFT_BAR_X = 46;
    private static final int LEFT_BAR_Y = 71; // bottom of bar


    private static final int LEFT_BAR2_X = 46;
    private static final int LEFT_BAR2_Y = 18; // top of bar

    private static final int BAR_WIDTH = 8;
    private static final int BAR_HEIGHT = 24;

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
    public int getWidth() {
        return IRecipeCategory.super.getWidth();
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


        builder.addSlot(RecipeIngredientRole.OUTPUT, 144, 74).addItemStack(recipe.getResultItem(null));
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 79, 45).addItemStack(recipe.getResultItem(null));
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 37).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public IDrawable getBackground() { // draws static gui
        return background;
    }

    // we need draw for the animation

    @Override
    public void draw(TallBlockRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // redraw oval
        guiGraphics.blit(OVAL_FILLED, OVAL_X, OVAL_Y, 0, 0, OVAL_WIDTH, OVAL_HEIGHT, OVAL_WIDTH, OVAL_HEIGHT);

        Minecraft mc = Minecraft.getInstance();
        long time = mc.level != null ? mc.level.getGameTime() : 0;


        int barHeight = (int) ((time % 144) / 144.0F * 24);
        int delayedBarHeight = (int) (((time + 2) % 144) / 144.0F * BAR_HEIGHT);

        if (barHeight > 0) {
            // down to top bars
            guiGraphics.blit(WHITE_BARS,
                    LEFT_BAR_X, LEFT_BAR_Y - barHeight,
                    0, BAR_HEIGHT - barHeight,
                    BAR_WIDTH, barHeight,
                    BAR_WIDTH, BAR_HEIGHT);

            guiGraphics.blit(WHITE_BARS,
                    RIGHT_BAR_X, RIGHT_BAR_Y - barHeight,
                    0, BAR_HEIGHT - barHeight,
                    BAR_WIDTH, barHeight,
                    BAR_WIDTH, BAR_HEIGHT);
        }

        if (delayedBarHeight > 0) {
            // top down bars
            guiGraphics.blit(WHITE_BARS,
                    LEFT_BAR2_X, LEFT_BAR2_Y,
                    0, 0,
                    BAR_WIDTH, delayedBarHeight,
                    BAR_WIDTH, BAR_HEIGHT);

            guiGraphics.blit(WHITE_BARS,
                    RIGHT_BAR2_X, RIGHT_BAR2_Y,
                    0, 0,
                    BAR_WIDTH, delayedBarHeight,
                    BAR_WIDTH, BAR_HEIGHT);
        }
        
        
    }
}
