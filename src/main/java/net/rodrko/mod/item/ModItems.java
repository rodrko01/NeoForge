package net.rodrko.mod.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rodrko.mod.StinkyMod;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StinkyMod.MOD_ID);

    public static final DeferredItem<Item> STINK = ITEMS.register("stink",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUBBLE1 = ITEMS.register("bubble1",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUBBLE2 = ITEMS.register("bubble2",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUBBLE3 = ITEMS.register("bubble3",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUBBLE4 = ITEMS.register("bubble4",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUBBLE5 = ITEMS.register("bubble5",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUBBLE6 = ITEMS.register("bubble6",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT1 = ITEMS.register("component1",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT2 = ITEMS.register("component2",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT3 = ITEMS.register("component3",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT4 = ITEMS.register("component4",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT5 = ITEMS.register("component5",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
