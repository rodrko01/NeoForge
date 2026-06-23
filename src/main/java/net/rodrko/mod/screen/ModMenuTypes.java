package net.rodrko.mod.screen;

import net.minecraft.core.BlockPos;
import net.rodrko.mod.StinkyMod;
import net.rodrko.mod.block.custom.TallBlock;
import net.rodrko.mod.block.entity.TallBlockEntity;
import net.rodrko.mod.screen.custom.GrowthChamberMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rodrko.mod.screen.custom.TallBlockMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, StinkyMod.MOD_ID);


    public static final DeferredHolder<MenuType<?>, MenuType<GrowthChamberMenu>> GROWTH_CHAMBER_MENU =
            registerMenuType("growth_chamber_menu", GrowthChamberMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<TallBlockMenu>> TALL_BLOCK_MENU =
            MENUS.register("tall_block_menu", () -> IMenuTypeExtension.create((windowId, inv, data)
                    -> {
                BlockPos pos = data.readBlockPos();
                TallBlockEntity entity = (TallBlockEntity) inv.player.level().getBlockEntity(pos);
                return new TallBlockMenu(windowId, inv, entity, entity.getData());
            } ));

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }


    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
