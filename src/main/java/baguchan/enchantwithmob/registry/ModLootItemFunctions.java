package baguchan.enchantwithmob.registry;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.loot.MobEnchantRandomlyFunction;
import baguchan.enchantwithmob.loot.MobEnchantWithLevelsFunction;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class ModLootItemFunctions {
	public static final LootItemFunctionType MOB_ENCHANT_WITH_LEVELS = register("mob_enchant_with_levels", MobEnchantWithLevelsFunction.CODEC);
	public static final LootItemFunctionType MOB_ENCHANT_RANDOMLY_FUNCTION = register("mob_enchant_randomly_function", MobEnchantRandomlyFunction.CODEC);


	private static LootItemFunctionType register(String p_80763_, Codec<? extends LootItemFunction> p_300110_) {
		return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(EnchantWithMob.MODID, p_80763_), new LootItemFunctionType(p_300110_));
	}

	public static void init() {

	}
}
