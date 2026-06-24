package net.rodrko.mod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.rodrko.mod.recipe.ModRecipes;
import net.rodrko.mod.recipe.TallBlockRecipe;
import net.rodrko.mod.recipe.TallBlockRecipeInput;
import net.rodrko.mod.screen.custom.TallBlockMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TallBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
        @Override
        public int getSlotLimit(int slot) {
            // only 1 in bubble slots
            if (slot == ITEM1_SLOT || slot == ITEM2_SLOT || slot == ITEM3_SLOT) {
                return 1;
            }
            // Allow normal stacks for other slots
            return super.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == ITEM1_SLOT || slot == ITEM2_SLOT || slot == ITEM3_SLOT) {
            }
            return super.isItemValid(slot, stack);
        }
    };



    private static final int FUEL_SLOT = 0; // blaze powder
    private static final int BOTTLE_SLOT = 1; // bottle of enchanting
    private static final int COMPONENT_SLOT = 2; // level comp (1 -> 2)
    private static final int ITEM1_SLOT = 3; // item 1 to upgrade
    private static final int ITEM2_SLOT = 4; // item 2 to upgrade
    private static final int ITEM3_SLOT = 5; // item 3 to upgrade

    private final int[] dataArray = new int[3];
    private boolean hasCrafted = false;
    private long lastTick = -1;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            if (index >= 0 && index < dataArray.length) {
                return dataArray[index];
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index >= 0 && index < dataArray.length) {
                dataArray[index] = value;
            }
        }

        @Override
        public int getCount() {
            return dataArray.length;
        }
    };
    private int progress = 0;
    private int maxProgress = 144;
    private int fuelTime = 0;
    private final int MAX_FUEL_TIME = 3 * 144;

    private int lastFuelTime = -1;

    public TallBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TALL_BLOCK_BE.get(), pos, blockState);
        // initialize the data array with default values
        dataArray[0] = 0;
        dataArray[1] = 144;
        dataArray[2] = 0;
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("block.johnminecraftmodsixseven.tall_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TallBlockMenu(containerId, playerInventory,this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("tall_block.progress", progress);
        pTag.putInt("tall_block.max_progress", maxProgress);
        pTag.putInt("tall_block.fuel_time", fuelTime);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("tall_block.progress");
        maxProgress = pTag.getInt("tall_block.max_progress");
        fuelTime = pTag.getInt("tall_block.fuel_time");

        if (maxProgress <= 0) maxProgress = 144;

        dataArray[0] = progress;
        dataArray[1] = maxProgress;
        dataArray[2] = fuelTime;
    }



    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void debugFuelChange(String location) {
        if (lastFuelTime != fuelTime) {
            //System.out.println("[DEBUG] " + location + " | fuelTime changed from " + lastFuelTime + " to " + fuelTime);
            lastFuelTime = fuelTime;
        }
    }

    public void tick(Level level1, BlockPos blockPos, BlockState blockState) {
        if (level1.isClientSide()) return;
        //System.out.println("DEBUG: maxProgress = " + maxProgress);

        long currentTick = level1.getGameTime();
        if (currentTick == lastTick) {
            return;
        }
        lastTick = currentTick;

        dataArray[0] = progress;
        dataArray[1] = maxProgress;
        dataArray[2] = fuelTime;

        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT);
        boolean hasFuel = !fuelStack.isEmpty();
        Optional<RecipeHolder<TallBlockRecipe>> recipe = getCurrentRecipe();
        boolean hasRecipe = hasRecipe();

        debugFuelChange("START OF TICK");

        //System.out.println("Tick - FuelTime: " + fuelTime + ", HasRecipe: " + hasRecipe + ", Progress: " + progress);

        // if oval is empty (no fuel), and blaze powder is in slot 0, valid recipe, extract the powder
        if (fuelTime <= 0 && hasFuel && hasRecipe) {
            //System.out.println(">>> FUEL CONSUMED! Setting fuelTime to " + MAX_FUEL_TIME);
            itemHandler.extractItem(FUEL_SLOT, 1, false);
            fuelTime = MAX_FUEL_TIME;
            debugFuelChange("FUEL CONSUMED BLOCK");
            dataArray[2] = fuelTime;
        }

        if (fuelTime > 0 && hasRecipe) {
            //System.out.println(">>> ENTERED CRAFT LOOP! progress before: " + progress);
            progress++;
            //System.out.println(">>> progress after increment: " + progress);
            setChanged(level1, blockPos, blockState);

            if (progress >= maxProgress) {
                if (recipe.isPresent()) {
                    TallBlockRecipe recipeValue = recipe.get().value();

                    // consume non bubbles
                    itemHandler.extractItem(BOTTLE_SLOT, recipeValue.slot1Count(), false);
                    itemHandler.extractItem(COMPONENT_SLOT, recipeValue.slot2Count(), false);

                    // replace bubbles with upgrades
                    craftItem(recipeValue);
                }
                fuelTime -= (MAX_FUEL_TIME/3);
                dataArray[2] = fuelTime;
                debugFuelChange("FUEL DECREASED AFTER COMPLETE");
                resetProgress();
                dataArray[0] = progress;
                debugFuelChange("RESET PROGRESS (fuelTime unchanged");
            }
        } else if (!hasRecipe){
            if (progress > 0) {
                resetProgress();
                dataArray[0] = progress;

            }
        }

        debugFuelChange("END OF TICK BEFORE SYNC");

        dataArray[0] = progress;
        dataArray[1] = maxProgress;
        dataArray[2] = fuelTime;
    }

    private void resetProgress() {
        progress = 0;
    }

    private Optional<RecipeHolder<TallBlockRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(
                        ModRecipes.TALL_BLOCK_TYPE.get(),
                        new TallBlockRecipeInput(
                                ItemStack.EMPTY, // ignores fuel slot
                                itemHandler.getStackInSlot(BOTTLE_SLOT),
                                itemHandler.getStackInSlot(COMPONENT_SLOT),
                                itemHandler.getStackInSlot(ITEM1_SLOT),
                                itemHandler.getStackInSlot(ITEM2_SLOT),
                                itemHandler.getStackInSlot(ITEM3_SLOT)
                        ),
                        level
                );
    }
    private void craftItem(TallBlockRecipe recipeValue) {
//        System.out.println("Upgrading items:");
        itemHandler.extractItem(ITEM1_SLOT, 1, false);
        itemHandler.setStackInSlot(ITEM1_SLOT, recipeValue.outputSlot3().copy());

        itemHandler.extractItem(ITEM2_SLOT, 1, false);
        itemHandler.setStackInSlot(ITEM2_SLOT, recipeValue.outputSlot4().copy());

        itemHandler.extractItem(ITEM3_SLOT, 1, false);
        itemHandler.setStackInSlot(ITEM3_SLOT, recipeValue.outputSlot5().copy());
    }
//
//    private boolean hasCraftingFinished() {
//        return this.progress >= this.maxProgress;
//    }
//
//    private void increaseCraftingProgress() {
//        progress++;
//    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<TallBlockRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
//            System.out.println("No recipe found!");
            return false;
        }

        TallBlockRecipe recipeValue = recipe.get().value();

//        System.out.println("Recipe found!");
//        System.out.println("Recipe Output: " + recipeValue.outputSlot3().getItem());
        if (itemHandler.getStackInSlot(BOTTLE_SLOT).getCount() < recipeValue.slot1Count()) return false;
        if (itemHandler.getStackInSlot(COMPONENT_SLOT).getCount() < recipeValue.slot2Count()) return false;
        if (itemHandler.getStackInSlot(ITEM1_SLOT).getCount() < recipeValue.slot3Count()) return false;
        if (itemHandler.getStackInSlot(ITEM2_SLOT).getCount() < recipeValue.slot4Count()) return false;
        if (itemHandler.getStackInSlot(ITEM3_SLOT).getCount() < recipeValue.slot5Count()) return false;

        return true;
    }

    public ContainerData getData() {
        return this.data;
    }
}
