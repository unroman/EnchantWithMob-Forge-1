package baguchan.enchantwithmob.registry;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.capability.ItemMobEnchantCapability;
import baguchan.enchantwithmob.capability.MobEnchantCapability;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModCapability {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnchantWithMob.MODID);

    public static final Supplier<AttachmentType<MobEnchantCapability>> MOB_ENCHANT = ATTACHMENT_TYPES.register(
            "mob_enchant", () -> AttachmentType.serializable(MobEnchantCapability::new).build());
    public static final Supplier<AttachmentType<ItemMobEnchantCapability>> ITEM_MOB_ENCHANT = ATTACHMENT_TYPES.register(
            "item_mob_enchant", () -> AttachmentType.serializable(ItemMobEnchantCapability::new).build());
}
