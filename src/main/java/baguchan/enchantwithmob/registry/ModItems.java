package baguchan.enchantwithmob.registry;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.item.EnchanterBottleItem;
import baguchan.enchantwithmob.item.EnchanterExperienceBottleItem;
import baguchan.enchantwithmob.item.EnchantersBookItem;
import baguchan.enchantwithmob.item.MobEnchantBookItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEM_REGISTRY = DeferredRegister.createItems(EnchantWithMob.MODID);

    public static final DeferredItem<Item> ENCHANTERS_BOOK = ITEM_REGISTRY.register("enchanters_book", () -> new EnchantersBookItem((new Item.Properties()).setId(prefix("enchanters_book")).stacksTo(1).durability(64)));
    public static final DeferredItem<Item> MOB_ENCHANT_BOOK = ITEM_REGISTRY.register("mob_enchant_book", () -> new MobEnchantBookItem((new Item.Properties()).setId(prefix("mob_enchant_book")).stacksTo(1).durability(5)));
    public static final DeferredItem<Item> ENCHANATERS_BOTTLE = ITEM_REGISTRY.register("enchanters_bottle", () -> new EnchanterBottleItem((new Item.Properties()).setId(prefix("enchanters_bottle")).stacksTo(16)));
    public static final DeferredItem<Item> ENCHANATERS_EXPERIENCE_BOTTLE = ITEM_REGISTRY.register("enchanters_experience_bottle", () -> new EnchanterExperienceBottleItem((new Item.Properties()).setId(prefix("enchanters_experience_bottle")).craftRemainder(ENCHANATERS_BOTTLE.get()).stacksTo(1)));
    public static final DeferredItem<Item> ENCHANTER_SPAWNEGG = ITEM_REGISTRY.register("enchanter_spawn_egg", () -> new DeferredSpawnEggItem(ModEntities.ENCHANTER, 9804699, 0x81052d, (new Item.Properties().setId(prefix("enchanter_spawn_egg")))));

    private static ResourceKey<Item> prefix(String path) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, path));
    }
}
