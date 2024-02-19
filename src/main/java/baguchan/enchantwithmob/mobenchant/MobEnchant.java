package baguchan.enchantwithmob.mobenchant;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.utils.MobEnchantConfigUtils;
import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.AttributeModifierTemplate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class MobEnchant implements FeatureElement {
    private final Map<Attribute, MobEnchantAttributeModifierTemplate> attributeModifierMap = Maps.newHashMap();
	protected final Rarity enchantType;
	private final int level;
	private int minlevel = 1;
    private final FeatureFlagSet requiredFeatures;
    @Nullable
    private String descriptionId;

    public MobEnchant(Properties properties) {
		this.enchantType = properties.enchantType;
		this.level = properties.level;
        this.requiredFeatures = properties.requiredFeatures;
	}

    public Rarity getRarity() {
        return enchantType;
    }

    public MobEnchant setMinLevel(int level) {
        this.minlevel = level;

        return this;
    }


    /**
     * Returns the minimum level that the enchantment can have.
     */
    public int getMinLevel() {
        return minlevel;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return level;
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + enchantmentLevel * 10;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 5;
    }


	public void tick(LivingEntity entity, int level) {

	}

	public final boolean isCompatibleWith(MobEnchant enchantmentIn) {
		return this.canApplyTogether(enchantmentIn) && enchantmentIn.canApplyTogether(this);
	}

	public boolean isTresureEnchant() {
		return false;
	}

	public boolean isOnlyChest() {
		return false;
	}

	public boolean isCompatibleMob(LivingEntity livingEntity) {
        return !(livingEntity instanceof Player) || MobEnchantConfigUtils.isPlayerEnchantable(this);
	}

	/**
	 * Determines if the enchantment passed can be applyied together with this enchantment.
	 */
	protected boolean canApplyTogether(MobEnchant ench) {
		return this != ench;
    }

    public MobEnchant addAttributesModifier(Attribute attributeIn, String uuid, double amount, AttributeModifier.Operation operation) {
        this.attributeModifierMap.put(attributeIn, new MobEnchantAttributeModifierTemplate(UUID.fromString(uuid), amount, operation));
        return this;
    }

    public Map<Attribute, MobEnchantAttributeModifierTemplate> getAttributeModifierMap() {
        return this.attributeModifierMap;
    }

	public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn) {
        for (Map.Entry<Attribute, MobEnchantAttributeModifierTemplate> entry : this.attributeModifierMap.entrySet()) {
			AttributeInstance modifiableattributeinstance = attributeMapIn.getInstance(entry.getKey());
			if (modifiableattributeinstance != null) {
                modifiableattributeinstance.removeModifier(entry.getValue().getAttributeModifierId());
			}
		}

	}

	public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
        for (Map.Entry<Attribute, MobEnchantAttributeModifierTemplate> entry : this.attributeModifierMap.entrySet()) {
			AttributeInstance modifiableattributeinstance = attributeMapIn.getInstance(entry.getKey());
			if (modifiableattributeinstance != null) {
                MobEnchantAttributeModifierTemplate attributemodifier = entry.getValue();
                modifiableattributeinstance.removeModifier(attributemodifier.getAttributeModifierId());
                modifiableattributeinstance.addPermanentModifier(new AttributeModifier(attributemodifier.getAttributeModifierId(), MobEnchants.getRegistry().getKey(this).toString() + " " + amplifier, this.getAttributeModifierAmount(amplifier, attributemodifier), attributemodifier.operation));
			}
		}
    }

    public double getAttributeModifierAmount(int amplifier, MobEnchantAttributeModifierTemplate modifier) {
        return modifier.amount * (double) (amplifier);
    }

    public boolean isDisabled() {
        return EnchantConfig.COMMON.DISABLE_ENCHANTS.get().contains(MobEnchants.getRegistry().getKey(this).toString());
    }

    public boolean isCursedEnchant() {
        return false;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet p_249172_) {
        return !this.isDisabled() && this.requiredFeatures().isSubsetOf(p_249172_);
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("mob_enchant", MobEnchants.getRegistry().getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getFullname(int p_44701_) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        if (this.isCursedEnchant()) {
            mutablecomponent.withStyle(ChatFormatting.RED);
        } else {
            mutablecomponent.withStyle(ChatFormatting.AQUA);
        }

        if (p_44701_ != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + p_44701_));
        }

        return mutablecomponent;
    }


    public static class Properties {
        private final Rarity enchantType;
        private final int level;

        FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

        public Properties(Rarity enchantType, int level) {
            this.enchantType = enchantType;
            this.level = level;
        }

        public FeatureFlagSet getRequiredFeatures() {
            return requiredFeatures;
        }

        public Properties requiredFeatures(FeatureFlag... p_250948_) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset(p_250948_);
            return this;
        }
    }

    public static enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        private Rarity(int rarityWeight) {
            this.weight = rarityWeight;
        }

        /**
         * Retrieves the weight of Rarity.
         */
        public int getWeight() {
            return this.weight;
        }


    }

    public class MobEnchantAttributeModifierTemplate implements AttributeModifierTemplate {
        private final UUID id;
        private final double amount;
        private final AttributeModifier.Operation operation;

        public MobEnchantAttributeModifierTemplate(UUID p_295616_, double p_294530_, AttributeModifier.Operation p_294958_) {
            this.id = p_295616_;
            this.amount = p_294530_;
            this.operation = p_294958_;
        }

        public AttributeModifier.Operation getOperation() {
            return operation;
        }

        public double getAmount() {
            return amount;
        }

        public UUID getId() {
            return id;
        }

        @Override
        public UUID getAttributeModifierId() {
            return this.id;
        }

        @Override
        public AttributeModifier create(int p_294228_) {
            return new AttributeModifier(this.id, MobEnchant.this.getDescriptionId() + " " + p_294228_, this.amount * (double) (p_294228_ + 1), this.operation);
        }
    }
}