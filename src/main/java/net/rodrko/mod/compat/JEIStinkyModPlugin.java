package net.rodrko.mod.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.rodrko.mod.StinkyMod;
import net.rodrko.mod.block.ModBlocks;
import net.rodrko.mod.recipe.ModRecipes;
import net.rodrko.mod.recipe.TallBlockRecipe;
import net.rodrko.mod.screen.custom.TallBlockScreen;

import java.util.List;

@JeiPlugin
public class JEIStinkyModPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(StinkyMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TallBlockRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<TallBlockRecipe> tallBlockRecipes = recipeManager.
                getAllRecipesFor(ModRecipes.TALL_BLOCK_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(TallBlockRecipeCategory.TALL_BLOCK_RECIPE_RECIPE_TYPE, tallBlockRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TallBlockScreen.class, 74, 30, 22, 20, //change for click area of gui
                TallBlockRecipeCategory.TALL_BLOCK_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TALL_BLOCK.asItem()),
                TallBlockRecipeCategory.TALL_BLOCK_RECIPE_RECIPE_TYPE);
    }
}
