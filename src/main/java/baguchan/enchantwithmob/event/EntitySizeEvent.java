package baguchan.enchantwithmob.event;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class EntitySizeEvent {

    public static boolean settingSize;

    @SubscribeEvent
    public static void onSetSize(EntityEvent.Size event) {
        Entity entity = event.getEntity();

        if (entity instanceof IEnchantCap cap) {
            if (cap.getEnchantCap() != null) {
                if (cap.getEnchantCap().hasEnchant()) {
                    if (MobEnchantUtils.findMobEnchantFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.SMALL.get())) {
                        int level = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.SMALL.get());
                        float cappedScale = Mth.clamp(0.15F * level, 0.0F, 0.9F);
                        float totalWidth = event.getNewSize().width * (1.0F - cappedScale);
                        float totalHeight = event.getNewSize().height * (1.0F - cappedScale);

                        event.setNewSize(EntityDimensions.fixed(totalWidth, totalHeight), true);
                    } else if (MobEnchantUtils.findMobEnchantFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.HUGE.get())) {
                        int level = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.HUGE.get());

                        float totalWidth = event.getNewSize().width * (1.0F + level * 0.15F);
                        float totalHeight = event.getNewSize().height * (1.0F + level * 0.15F);
                        event.setNewSize(EntityDimensions.fixed(totalWidth, totalHeight), true);
                    } else if (EnchantConfig.COMMON.changeSizeWhenEnchant.get()) {
                        float totalWidth = event.getNewSize().width * 1.025F;
                        float totalHeight = event.getNewSize().height * 1.025F;
                        event.setNewSize(EntityDimensions.fixed(totalWidth, totalHeight), true);

                    }
                }
            }
        }
    }
}