package baguchan.enchantwithmob.item;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EnchantersBookItem extends Item {
	private final TargetingConditions enchantTargeting = TargetingConditions.forNonCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting();
	private final TargetingConditions alreadyEnchantTargeting = TargetingConditions.forNonCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting().selector((entity) -> {
		return entity instanceof IEnchantCap enchantCap && enchantCap.getEnchantCap().hasEnchant();
	});

	public EnchantersBookItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isEnabled(FeatureFlagSet p_249172_) {
		return super.isEnabled(p_249172_) && !EnchantConfig.COMMON.disableMobEnchantStuffItems.get();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
			if (MobEnchantUtils.hasMobEnchant(stack)) {
				List<LivingEntity> list = level.getNearbyEntities(LivingEntity.class, this.enchantTargeting, playerIn, playerIn.getBoundingBox().inflate(16.0D));
				List<LivingEntity> hasEnchantedMoblist = level.getNearbyEntities(LivingEntity.class, this.alreadyEnchantTargeting, playerIn, playerIn.getBoundingBox().inflate(16.0D));

				if (hasEnchantedMoblist.isEmpty() || hasEnchantedMoblist.size() < 5) {
					if (!list.isEmpty()) {
						int size = list.size();
						final boolean[] flag = {false};
						for (int i = 0; i < size; ++i) {
							LivingEntity enchantedMob = list.get(i);

							if (i >= 5) {
								break;
							}

							if (!enchantedMob.canAttack(playerIn) && playerIn != enchantedMob) {
								if (enchantedMob instanceof IEnchantCap cap) {
									if (!cap.getEnchantCap().hasEnchant()) {
										if (flag[0]) {
											MobEnchantUtils.addUnstableItemMobEnchantToEntity(stack, enchantedMob, playerIn, cap);
										} else {
											flag[0] = MobEnchantUtils.addUnstableItemMobEnchantToEntity(stack, enchantedMob, playerIn, cap);
										}
									}
								}
								;
							}
						}

						//When flag is true, enchanting is success.
						if (flag[0]) {
							level.playSound(playerIn, playerIn.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);

							playerIn.getCooldowns().addCooldown(stack.getItem(), 40);

							return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
						}
					} else {
						playerIn.displayClientMessage(Component.translatable("enchantwithmob.cannot.no_enchantable_ally"), true);

						playerIn.getCooldowns().addCooldown(stack.getItem(), 20);

						return InteractionResultHolder.fail(stack);
					}
				} else {
					playerIn.displayClientMessage(Component.translatable("enchantwithmob.cannot.no_enchantable_ally"), true);

					playerIn.getCooldowns().addCooldown(stack.getItem(), 20);

					return InteractionResultHolder.fail(stack);
				}
			}
		return super.use(level, playerIn, handIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable TooltipContext level, List<Component> tooltip, TooltipFlag p_41424_) {
		super.appendHoverText(stack, level, tooltip, p_41424_);
		ChatFormatting[] textformatting2 = new ChatFormatting[]{ChatFormatting.DARK_PURPLE};

		tooltip.add(Component.translatable("mobenchant.enchantwithmob.enchanter_book.tooltip").withStyle(textformatting2));
	}

	@Override
	public boolean isFoil(ItemStack p_77636_1_) {
		return true;
	}
}
