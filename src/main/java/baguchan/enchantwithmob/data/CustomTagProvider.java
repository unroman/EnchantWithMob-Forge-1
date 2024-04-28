package baguchan.enchantwithmob.data;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class CustomTagProvider {

    public static class MobEnchantTagGenerator extends TagsProvider<MobEnchant> {

        public MobEnchantTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, MobEnchants.MOB_ENCHANT_REGISTRY, provider, EnchantWithMob.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider p_256380_) {
            for (DeferredHolder<MobEnchant, ? extends MobEnchant> mobEnchant : MobEnchants.MOB_ENCHANT.getEntries()) {
                tag(ModTags.MobEnchantTags.TOOLTIP_ORDER).add(mobEnchant.getKey());
            }
        }
    }
}
