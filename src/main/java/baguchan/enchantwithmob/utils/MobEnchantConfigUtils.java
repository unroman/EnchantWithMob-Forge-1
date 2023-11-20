package baguchan.enchantwithmob.utils;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import baguchan.enchantwithmob.registry.MobEnchants;

public class MobEnchantConfigUtils {

    public static boolean isPlayerEnchantable(MobEnchant mobEnchant) {
        return !EnchantConfig.COMMON.BLACKLIST_PLAYER_ENCHANT.get().contains(MobEnchants.getRegistry().getKey(mobEnchant).toString());
    }
}
