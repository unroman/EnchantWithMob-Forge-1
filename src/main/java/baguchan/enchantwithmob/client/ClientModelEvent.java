package baguchan.enchantwithmob.client;

import bagu_chan.bagus_lib.api.client.IRootModel;
import bagu_chan.bagus_lib.client.event.BagusModelEvent;
import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.client.animation.FloatAnimation;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, value = Dist.CLIENT)
public class ClientModelEvent {
    @SubscribeEvent
    public static void modelEventInit(BagusModelEvent.Init event) {
        if (event.getModel() instanceof IRootModel rootModel) {
            rootModel.getBagusRoot().resetPose();
        }
    }

    @SubscribeEvent
    public static void modelEvent(BagusModelEvent.PostAnimate event) {
        if (event.getEntityIn() instanceof IEnchantCap enchantCap && MobEnchantUtils.hasWindEnchant(enchantCap.getEnchantCap().getMobEnchants())) {
            if (event.getModel() instanceof IRootModel rootModel) {
                rootModel.animateWalkBagu(FloatAnimation.FLOAT, event.getAgeInTick(), 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
