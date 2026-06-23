package net.rodrko.mod.screen.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.rodrko.mod.block.ModBlocks;
import net.rodrko.mod.block.entity.TallBlockEntity;
import net.rodrko.mod.screen.ModMenuTypes;
import org.jetbrains.annotations.NotNull;

public class TallBlockMenu extends AbstractContainerMenu {
    public final TallBlockEntity blockEntity;
    private final ContainerData data;
    private final Level level;

    private static final int FUEL_SLOT_X = 17;
    private static final int FUEL_SLOT_Y = 74;
    private static final int BOTTLE_SLOT_X = 79;
    private static final int BOTTLE_SLOT_Y = 67;
    private static final int COMPONENT_SLOT_X = 79;
    private static final int COMPONENT_SLOT_Y = 9;

    private static final int ITEM1_SLOT_X = 59;
    private static final int ITEM1_SLOT_Y = 30;
    private static final int ITEM2_SLOT_X = 79;
    private static final int ITEM2_SLOT_Y = 38;
    private static final int ITEM3_SLOT_X = 99;
    private static final int ITEM3_SLOT_Y = 30;

    private static final int PLAYER_INVENTORY_X = 8;
    private static final int PLAYER_INVENTORY_Y = 101;
    private static final int HOTBAR_X = 8;
    private static final int HOTBAR_Y = 159;

    public TallBlockMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                (TallBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()),
                ((TallBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos())).getData()
        );
    }
    public TallBlockMenu(int containerId, Inventory inv, TallBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TALL_BLOCK_MENU.get(), containerId);
        this.blockEntity = entity;
        this.level = inv.player.level();
        this.data = data;


        // slot 0: fuel
        this.addSlot(new SlotItemHandler(entity.itemHandler, 0, FUEL_SLOT_X, FUEL_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.BLAZE_POWDER;
            }
        });

        // slot 1: bottle of enchanting
        this.addSlot(new SlotItemHandler(entity.itemHandler, 1, BOTTLE_SLOT_X, BOTTLE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.EXPERIENCE_BOTTLE;
            }
        });

        // slot 2: level component
        this.addSlot(new SlotItemHandler(entity.itemHandler, 2, COMPONENT_SLOT_X, COMPONENT_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true; // Any item can be placed as component
            }
        });

        // slot 3: bubble to upgrade
        this.addSlot(new SlotItemHandler(entity.itemHandler, 3, ITEM1_SLOT_X, ITEM1_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true; // Any item can be upgraded
            }

            @Override
            public int getMaxStackSize() {
                return 1; // only allow 1 item
            }
        });

        // slot 4: bubble 2 to upgrade
        this.addSlot(new SlotItemHandler(entity.itemHandler, 4, ITEM2_SLOT_X, ITEM2_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1; // only allow 1 item
            }
        });

        // slot 5: bubble 3 to upgrade
        this.addSlot(new SlotItemHandler(entity.itemHandler, 5, ITEM3_SLOT_X, ITEM3_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1; // only allow 1 item
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        PLAYER_INVENTORY_X + col * 18,
                        PLAYER_INVENTORY_Y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col,
                    HOTBAR_X + col * 18,
                    HOTBAR_Y));
        }

        this.addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 36; // 27 inv + 9 hotbar
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 0;

    private static final int TE_INVENTORY_SLOT_COUNT = 6;  // must be the number of slots you have!
    private static final int PLAYER_INVENTORY_FIRST_SLOT_INDEX = TE_INVENTORY_SLOT_COUNT;
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 6) {
                if (!this.moveItemStackTo(itemstack1, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 6, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.TALL_BLOCK.get());
    }

    public int getFuelTime() {
        return this.data.get(2);
    }
}
