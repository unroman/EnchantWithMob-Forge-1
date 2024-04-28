package baguchan.enchantwithmob.loot;

import baguchan.enchantwithmob.registry.ModLootItemFunctions;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.Set;

public class MobEnchantWithLevelsFunction extends LootItemConditionalFunction {
	public static final MapCodec<MobEnchantWithLevelsFunction> CODEC = RecordCodecBuilder.mapCodec((p_298285_) -> {
        return commonFields(p_298285_).and(p_298285_.group(NumberProviders.CODEC.fieldOf("levels").forGetter((p_298991_) -> {
            return p_298991_.levels;
        }), Codec.BOOL.fieldOf("treasure").orElse(false).forGetter((p_298792_) -> {
            return p_298792_.treasure;
        }), Codec.BOOL.fieldOf("curse").orElse(false).forGetter((p_298792_) -> {
            return p_298792_.treasure;
        }))).apply(p_298285_, MobEnchantWithLevelsFunction::new);
    });
	final NumberProvider levels;
	final boolean treasure;
	final boolean curse;

    MobEnchantWithLevelsFunction(List<LootItemCondition> p_165193_, NumberProvider p_165194_, boolean p_165195_, boolean curse) {
		super(p_165193_);
		this.levels = p_165194_;
		this.treasure = p_165195_;
		this.curse = curse;
	}

	public LootItemFunctionType getType() {
		return ModLootItemFunctions.MOB_ENCHANT_WITH_LEVELS.get();
	}

	public Set<LootContextParam<?>> getReferencedContextParams() {
		return this.levels.getReferencedContextParams();
	}

	public ItemStack run(ItemStack p_80483_, LootContext p_80484_) {
		RandomSource random = p_80484_.getRandom();
		return MobEnchantUtils.addRandomEnchantmentToItemStack(random, p_80483_, this.levels.getInt(p_80484_), this.treasure, this.curse);
	}

	public static Builder enchantWithLevels(NumberProvider p_165197_) {
		return new Builder(p_165197_);
	}

	public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
		private final NumberProvider levels;
		private boolean treasure;
		private boolean curse;

		public Builder(NumberProvider p_165200_) {
			this.levels = p_165200_;
		}

		protected Builder getThis() {
			return this;
		}

		public Builder allowTreasure() {
			this.treasure = true;
			return this;
		}

		public Builder allowCurse() {
			this.curse = true;
			return this;
		}

		public LootItemFunction build() {
			return new MobEnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure, this.curse);
		}
	}
}