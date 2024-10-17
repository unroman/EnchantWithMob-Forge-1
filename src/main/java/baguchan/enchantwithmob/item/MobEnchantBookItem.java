package baguchan.enchantwithmob.item;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.registry.ModDataCompnents;
import baguchan.enchantwithmob.registry.ModItems;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class MobEnchantBookItem extends Item {
	public MobEnchantBookItem(Item.Properties group) {
		super(group);
	}


    /*
     * Implemented onRightClick (method) inside CommonEventHandler instead of this method
     */
    /*@Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (MobEnchantUtils.hasMobEnchant(stack)) {
            target.getCapability(EnchantWithMob.MOB_ENCHANT_CAP).ifPresent(cap ->
            {
                MobEnchantUtils.addMobEnchantToEntityFromItem(stack, target, cap);
            });
            playerIn.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);

            stack.damageItem(1, playerIn, (entity) -> entity.sendBreakAnimation(hand));

            return ActionResultType.SUCCESS;
        }

        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }*/

	@Override
	public boolean isEnabled(FeatureFlagSet p_249172_) {
		return super.isEnabled(p_249172_) && !EnchantConfig.COMMON.disableMobEnchantStuffItems.get();
	}

	@Override
	public InteractionResult use(Level level, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		if (EnchantConfig.COMMON.enchantYourSelf.get() && MobEnchantUtils.hasMobEnchant(stack)) {
				if (playerIn instanceof IEnchantCap cap) {
					boolean flag = MobEnchantUtils.addItemMobEnchantToEntity(stack, playerIn, playerIn, cap);


					//When flag is true, enchanting is success.
					if (flag) {
						level.playSound(playerIn, playerIn.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);
						playerIn.getCooldowns().addCooldown(stack, 40);

						return InteractionResult.SUCCESS;
					} else {
						playerIn.displayClientMessage(Component.translatable("enchantwithmob.cannot.enchant_yourself"), true);

						playerIn.getCooldowns().addCooldown(stack, 20);

						return InteractionResult.FAIL;
					}
				}
		}
		return super.use(level, playerIn, handIn);
	}

	public static List<ItemStack> generateMobEnchantmentBookTypesOnlyMaxLevel() {
		List<ItemStack> items = Lists.newArrayList();
		for (MobEnchant mobEnchant : MobEnchants.getRegistry()) {
			if (!mobEnchant.isDisabled()) {
				ItemStack stack = new ItemStack(ModItems.MOB_ENCHANT_BOOK.get());
                MobEnchantUtils.enchant(mobEnchant, stack, mobEnchant.getMaxLevel());
				items.add(stack);
			}
		}
		for (MobEnchant mobEnchant : MobEnchants.getRegistry()) {
			if (!mobEnchant.isDisabled()) {
				ItemStack stack2 = new ItemStack(ModItems.ENCHANTERS_BOOK.get());
                MobEnchantUtils.enchant(mobEnchant, stack2, mobEnchant.getMaxLevel());
				items.add(stack2);
			}
		}
		return items;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable TooltipContext level, List<Component> tooltip, TooltipFlag p_41424_) {
		super.appendHoverText(stack, level, tooltip, p_41424_);
		ChatFormatting[] textformatting2 = new ChatFormatting[]{ChatFormatting.DARK_PURPLE};
        Consumer<Component> consumer = tooltip::add;
        stack.addToTooltip(ModDataCompnents.MOB_ENCHANTMENTS.get(), level, consumer, p_41424_);
		tooltip.add(Component.translatable("mobenchant.enchantwithmob.mob_enchant_book.tooltip").withStyle(textformatting2));
	}

    @Override
    public boolean isFoil(ItemStack p_77636_1_) {
        return true;
    }
}
