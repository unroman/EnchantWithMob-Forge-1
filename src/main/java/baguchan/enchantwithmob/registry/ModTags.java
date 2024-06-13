package baguchan.enchantwithmob.registry;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ModTags {
    public static class MobEnchantTags {
        public static final TagKey<MobEnchant> TOOLTIP_ORDER = create("tooltip_order");

        private static TagKey<MobEnchant> create(String p_341202_) {
            return TagKey.create(MobEnchants.MOB_ENCHANT_REGISTRY, ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, p_341202_));
        }
    }
}
