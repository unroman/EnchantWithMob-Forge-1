package baguchan.enchantwithmob.loot;

import baguchan.enchantwithmob.mobenchant.MobEnchant;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.registry.ModItems;
import baguchan.enchantwithmob.registry.ModLootItemFunctions;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MobEnchantRandomlyFunction extends LootItemConditionalFunction {
	private static final Codec<MobEnchant> ENCHANTMENT_SET_CODEC = Codec.lazyInitialized(() -> MobEnchants.getRegistry().byNameCodec());
	public static final MapCodec<MobEnchantRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec((p_297085_) -> {
		return commonFields(p_297085_).and(Codec.list(ENCHANTMENT_SET_CODEC).fieldOf("mob_enchant").forGetter((p_297084_) -> {
			return p_297084_.enchantments;
		})).apply(p_297085_, MobEnchantRandomlyFunction::new);
	});
	private static final Logger LOGGER = LogUtils.getLogger();
	final List<MobEnchant> enchantments;

	public MobEnchantRandomlyFunction(List<LootItemCondition> p_80418_, Collection<MobEnchant> p_80419_) {
		super(p_80418_);
		this.enchantments = ImmutableList.copyOf(p_80419_);
	}

	public LootItemFunctionType getType() {
		return ModLootItemFunctions.MOB_ENCHANT_RANDOMLY_FUNCTION.get();
	}

	public ItemStack run(ItemStack p_80429_, LootContext p_80430_) {
		RandomSource randomsource = p_80430_.getRandom();
		MobEnchant enchantment;
		if (this.enchantments.isEmpty()) {
			boolean flag = p_80429_.is(Items.BOOK) || p_80429_.is(ModItems.MOB_ENCHANT_BOOK.get());
            List<MobEnchant> list = MobEnchants.getRegistry().stream().filter(MobEnchant::isDiscoverable).filter((p_80436_) -> {
				return flag;
			}).toList();
			if (list.isEmpty()) {
				LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object) p_80429_);
				return p_80429_;
			}

			enchantment = list.get(randomsource.nextInt(list.size()));
		} else {
			enchantment = this.enchantments.get(randomsource.nextInt(this.enchantments.size()));
		}

		return enchantItem(p_80429_, enchantment, randomsource);
	}

	private static ItemStack enchantItem(ItemStack p_230980_, MobEnchant p_230981_, RandomSource p_230982_) {
		int i = Mth.nextInt(p_230982_, p_230981_.getMinLevel(), p_230981_.getMaxLevel());
		if (p_230980_.is(Items.BOOK)) {
			p_230980_ = new ItemStack(ModItems.MOB_ENCHANT_BOOK.get());
            MobEnchantUtils.enchant(p_230981_, p_230980_, i);
		} else {
            MobEnchantUtils.enchant(p_230981_, p_230980_, i);
		}

		return p_230980_;
	}

	public static MobEnchantRandomlyFunction.Builder randomMobEnchant() {
		return new MobEnchantRandomlyFunction.Builder();
	}

	public static LootItemConditionalFunction.Builder<?> randomApplicableMobEnchant() {
		return simpleBuilder((p_80438_) -> {
			return new MobEnchantRandomlyFunction(p_80438_, ImmutableList.of());
		});
	}

	public static class Builder extends LootItemConditionalFunction.Builder<MobEnchantRandomlyFunction.Builder> {
		private final Set<MobEnchant> enchantments = Sets.newHashSet();

		protected MobEnchantRandomlyFunction.Builder getThis() {
			return this;
		}

		public MobEnchantRandomlyFunction.Builder withMobEnchant(MobEnchant p_80445_) {
			this.enchantments.add(p_80445_);
			return this;
		}

		public LootItemFunction build() {
			return new MobEnchantRandomlyFunction(this.getConditions(), this.enchantments);
		}
	}
}