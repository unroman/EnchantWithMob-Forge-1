package baguchi.enchantwithmob.utils;

import baguchi.enchantwithmob.EnchantConfig;
import baguchi.enchantwithmob.mobenchant.MobEnchant;
import baguchi.enchantwithmob.registry.MobEnchants;

public class MobEnchantConfigUtils {

    public static boolean isPlayerEnchantable(MobEnchant mobEnchant) {
        return !EnchantConfig.COMMON.BLACKLIST_PLAYER_ENCHANT.get().contains(MobEnchants.getRegistry().getKey(mobEnchant).toString());
    }
}
