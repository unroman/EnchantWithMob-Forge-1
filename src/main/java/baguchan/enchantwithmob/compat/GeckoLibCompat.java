package baguchan.enchantwithmob.compat;

import baguchan.enchantwithmob.EnchantWithMob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeckoLibCompat {
	public static boolean isLoaded = false;
	public static final String GECKO_LIB_MOD_ID = "geckolib3";

	@SubscribeEvent
	public static void onInterMod(InterModProcessEvent event) {
		if (ModList.get().isLoaded(GECKO_LIB_MOD_ID)) {
			isLoaded = true;
		}
	}
}
