package baguchan.enchantwithmob.event;

import baguchan.enchantwithmob.EnchantWithMob;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class EntitySizeEvent {

    public static boolean settingSize;

    /*@SubscribeEvent
    public static void onSetSize(EntityEvent.Size event) {
        Entity entity = event.getEntity();


        }
    }*/
}