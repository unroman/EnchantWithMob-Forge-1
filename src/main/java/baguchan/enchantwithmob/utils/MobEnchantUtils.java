package baguchan.enchantwithmob.utils;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantHandler;
import baguchan.enchantwithmob.item.mobenchant.ItemMobEnchantments;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.registry.ModDataCompnents;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MobEnchantUtils {
	public static final String TAG_MOBENCHANT = "MobEnchant";
	public static final String TAG_ENCHANT_LEVEL = "EnchantLevel";
	public static final String TAG_STORED_MOBENCHANTS = "StoredMobEnchants";

	//when projectile Shooter has mob enchant, start Runnable
	public static void executeIfPresent(Entity entity, MobEnchant mobEnchantment, Runnable runnable) {
		if (entity != null) {
			if (entity instanceof IEnchantCap cap) {
				if (MobEnchantUtils.findMobEnchantFromHandler(cap.getEnchantCap().getMobEnchants(), mobEnchantment)) {
					runnable.run();
				}
			}
		}
	}

	public static boolean hasWindEnchant(List<MobEnchantHandler> list) {
		return findMobEnchantFromHandler(list, MobEnchants.WIND.get());
	}

	public static void executeIfPresent(Entity entity, Runnable runnable) {
		if (entity != null) {
			if (entity instanceof IEnchantCap cap) {
				if (cap.getEnchantCap().hasEnchant()) {
					runnable.run();
				}
			}
		}
	}

	/**
	 * get MobEnchant From NBT
	 *
	 * @param tag nbt tag
	 */
	@Nullable
	public static MobEnchant getEnchantFromNBT(@Nullable CompoundTag tag) {
		if (tag != null && MobEnchants.getRegistry().containsKey(ResourceLocation.tryParse(tag.getString(TAG_MOBENCHANT)))) {
			return MobEnchants.getRegistry().get(ResourceLocation.tryParse(tag.getString(TAG_MOBENCHANT)));
		} else {
			return null;
		}
	}

	/**
	 * get MobEnchant Level From NBT
	 *
	 * @param tag nbt tag
	 */
	public static int getEnchantLevelFromNBT(@Nullable CompoundTag tag) {
		if (tag != null) {
			return tag.getInt(TAG_ENCHANT_LEVEL);
		} else {
			return 0;
		}
	}

	/**
	 * get MobEnchant From String
	 *
	 * @param id MobEnchant id
	 */
	@Nullable
	public static MobEnchant getEnchantFromString(@Nullable String id) {
		if (id != null && MobEnchants.getRegistry().containsKey(ResourceLocation.tryParse(id))) {
			return MobEnchants.getRegistry().get(ResourceLocation.tryParse(id));
		} else {
			return null;
		}
	}

	@Nullable
	public static MobEnchant getEnchantFromResourceLocation(@Nullable ResourceLocation id) {
		if (id != null && MobEnchants.getRegistry().containsKey(id)) {
			return MobEnchants.getRegistry().get(id);
		} else {
			return null;
		}
	}

	/**
	 * check ItemStack has Mob Enchant
	 *
	 * @param stack MobEnchanted Item
	 */
	public static boolean hasMobEnchant(ItemStack stack) {
        @Nullable ItemMobEnchantments itemMobEnchantments = stack.get(ModDataCompnents.MOB_ENCHANTMENTS);
        return itemMobEnchantments != null;
	}

	/**
	 * check NBT has Mob Enchant
	 *
	 * @param compoundnbt nbt tag
	 */
	public static ListTag getEnchantmentListForNBT(CompoundTag compoundnbt) {
		return compoundnbt != null ? compoundnbt.getList(TAG_STORED_MOBENCHANTS, 10) : new ListTag();
	}


	/*
	 * item Mob Enchant Start
	 */
    public static ItemMobEnchantments getEnchantmentsForCrafting(ItemStack stack) {
        return stack.getOrDefault(ModDataCompnents.MOB_ENCHANTMENTS.get(), ItemMobEnchantments.EMPTY);
	}

    public static void setEnchantments(ItemStack stack, ItemMobEnchantments p_332148_) {
        stack.set(ModDataCompnents.MOB_ENCHANTMENTS.get(), p_332148_);
    }

    public static boolean canStoreEnchantments(ItemStack p_330666_) {
        return p_330666_.has(ModDataCompnents.MOB_ENCHANTMENTS.get());
	}

	//if using make mob enchant. use this
	public static void enchant(MobEnchant p_41664_, ItemStack stack, int p_41665_) {
        updateEnchantments(stack, p_330091_ -> p_330091_.upgrade(p_41664_, p_41665_));
    }

    public static ItemMobEnchantments updateEnchantments(ItemStack p_331034_, Consumer<ItemMobEnchantments.Mutable> p_332031_) {
        DataComponentType<ItemMobEnchantments> datacomponenttype = ModDataCompnents.MOB_ENCHANTMENTS.get();
		ItemMobEnchantments itemenchantments = p_331034_.getOrDefault(datacomponenttype, ItemMobEnchantments.EMPTY);

		ItemMobEnchantments.Mutable itemenchantments$mutable = new ItemMobEnchantments.Mutable(itemenchantments);
		p_332031_.accept(itemenchantments$mutable);
		ItemMobEnchantments itemenchantments1 = itemenchantments$mutable.toImmutable();
		p_331034_.set(datacomponenttype, itemenchantments1);
		return itemenchantments1;
	}

	/*
	 * item Mob Enchant End
	 */


	/**
	 * add Mob Enchantments From ItemStack
	 *
	 * @param itemIn     MobEnchanted Item
	 * @param entity     Enchanting target
	 * @param capability MobEnchant Capability
	 */
	public static boolean addItemMobEnchantToEntity(ItemStack itemIn, LivingEntity entity, LivingEntity user, IEnchantCap capability) {
        ItemMobEnchantments itemMobEnchantments = getEnchantmentsForCrafting(itemIn);
		boolean flag = false;

        for (Holder<MobEnchant> mobEnchant : itemMobEnchantments.keySet()) {
            int level = itemMobEnchantments.getLevel(mobEnchant.value());
            if (checkAllowMobEnchantFromMob(mobEnchant.value(), entity, capability)) {
                capability.getEnchantCap().addMobEnchant(entity, mobEnchant.value(), level);
				flag = true;

				if (!user.level().isClientSide()) {
                    itemIn.hurtAndBreak(1, user, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));

				}
			}
		}
		return flag;
	}

	public static boolean addUnstableItemMobEnchantToEntity(ItemStack itemIn, LivingEntity entity, LivingEntity owner, IEnchantCap capability) {
        ItemMobEnchantments itemMobEnchantments = getEnchantmentsForCrafting(itemIn);
		boolean flag = false;

        for (Holder<MobEnchant> mobEnchant : itemMobEnchantments.keySet()) {
            int level = itemMobEnchantments.getLevel(mobEnchant.value());
            if (checkAllowMobEnchantFromMob(mobEnchant.value(), entity, capability)) {
                capability.getEnchantCap().addMobEnchantFromOwner(entity, mobEnchant.value(), level, owner);
				flag = true;

				if (!owner.level().isClientSide()) {
                    itemIn.hurtAndBreak(1, owner, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));

				}
			}
		}
		return flag;
	}
	public static void removeMobEnchantToEntity(LivingEntity entity, IEnchantCap capability) {
		capability.getEnchantCap().removeAllMobEnchant(entity);
	}

	public static int getExperienceFromMob(IEnchantCap cap) {
		int l = 0;
		for (MobEnchantHandler list : cap.getEnchantCap().getMobEnchants()) {
			MobEnchant enchantment = list.getMobEnchant();
			int integer = list.getEnchantLevel();
			l += enchantment.getMinEnchantability(integer);
		}
		return l;
	}

	/**
	 * add Mob Enchantments To Entity
	 *
	 * @param livingEntity Enchanting target
	 * @param capability   MobEnchant Capability
	 * @param data         MobEnchant Data
	 */
	public static boolean addEnchantmentToEntity(LivingEntity livingEntity, IEnchantCap capability, MobEnchantmentData data) {
		boolean flag = false;
		if (checkAllowMobEnchantFromMob(data.enchantment, livingEntity, capability)) {
			capability.getEnchantCap().addMobEnchant(livingEntity, data.enchantment, data.enchantmentLevel, false);
			flag = true;
		}
		return flag;
	}

	public static boolean addEnchantmentToEntity(LivingEntity livingEntity, IEnchantCap capability, MobEnchantmentData data, boolean ancient) {
		boolean flag = false;
		if (checkAllowMobEnchantFromMob(data.enchantment, livingEntity, capability)) {
			capability.getEnchantCap().addMobEnchant(livingEntity, data.enchantment, data.enchantmentLevel, ancient);
			flag = true;
		}
		return flag;
	}

	/**
	 * add Mob Enchantments To Entity
	 *
	 * @param livingEntity Enchanting target
	 * @param capability   MobEnchant Capability
	 * @param random       Random
	 * @param level        max limit level MobEnchant
	 * @param allowTresure setting is allow rare enchant
	 */
	public static boolean addRandomEnchantmentToEntity(LivingEntity livingEntity, IEnchantCap capability, RandomSource random, int level, boolean allowTresure, boolean allowCurse, boolean ancient) {
		List<MobEnchantmentData> list = buildEnchantmentList(random, level, allowTresure, allowCurse);

		boolean flag = false;
		for (MobEnchantmentData enchantmentdata : list) {
			if (checkAllowMobEnchantFromMob(enchantmentdata.enchantment, livingEntity, capability)) {
				capability.getEnchantCap().addMobEnchant(livingEntity, enchantmentdata.enchantment, enchantmentdata.enchantmentLevel, ancient);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * add Mob Enchantments To Entity(but unstable enchant)
	 *
	 * @param livingEntity Enchanting target
	 * @param capability   MobEnchant Capability
	 * @param random       Random
	 * @param level        max limit level MobEnchant
	 * @param allowTresure setting is allow rare enchant
	 */
	public static boolean addUnstableRandomEnchantmentToEntity(LivingEntity livingEntity, LivingEntity ownerEntity, IEnchantCap capability, RandomSource random, int level, boolean allowTresure, boolean allowCurse) {
		List<MobEnchantmentData> list = buildEnchantmentList(random, level, allowTresure, allowCurse);

		boolean flag = false;

		for (MobEnchantmentData enchantmentdata : list) {
			if (checkAllowMobEnchantFromMob(enchantmentdata.enchantment, livingEntity, capability)) {
				capability.getEnchantCap().addMobEnchantFromOwner(livingEntity, enchantmentdata.enchantment, enchantmentdata.enchantmentLevel, ownerEntity);
				flag = true;
			}
		}
		return flag;
	}

	public static ItemStack addRandomEnchantmentToItemStack(RandomSource random, ItemStack stack, int level, boolean allowRare, boolean allowCurse) {
		List<MobEnchantmentData> list = buildEnchantmentList(random, level, allowRare, allowCurse);

		for (MobEnchantmentData enchantmentdata : list) {
			if (!enchantmentdata.enchantment.isDisabled()) {
				enchant(enchantmentdata.enchantment, stack, enchantmentdata.enchantmentLevel);
			}
		}

		return stack;
	}

	public static boolean findMobEnchantHandler(List<MobEnchantHandler> list, MobEnchant findMobEnchant) {
		for (MobEnchantHandler mobEnchant : list) {
			if (mobEnchant.getMobEnchant().equals(findMobEnchant)) {
				return true;
			}
		}
		return false;
	}

	public static boolean findMobEnchant(List<MobEnchant> list, MobEnchant findMobEnchant) {
		if (list.contains(findMobEnchant)) {
			return true;
		}
		return false;
	}

	public static boolean findMobEnchantFromHandler(List<MobEnchantHandler> list, MobEnchant findMobEnchant) {
		for (MobEnchantHandler mobEnchant : list) {
			if (mobEnchant != null && !findMobEnchant.isDisabled()) {
				if (mobEnchant.getMobEnchant().equals(findMobEnchant)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkAllowMobEnchantFromMob(@Nullable MobEnchant mobEnchant, LivingEntity livingEntity, IEnchantCap capability) {
		if (!EnchantConfig.COMMON.universalEnchant.get()) {
			if (mobEnchant != null && !mobEnchant.isCompatibleMob(livingEntity)) {
				return false;
			}
		}

		if (mobEnchant.isDisabled()) {
			return false;
		}

		for (MobEnchantHandler enchantHandler : capability.getEnchantCap().getMobEnchants()) {
			if (mobEnchant != null && enchantHandler.getMobEnchant() != null && !enchantHandler.getMobEnchant().isCompatibleWith(mobEnchant)) {
				return false;
			}
		}

		//check mob enchant is not null
		return mobEnchant != null;
	}

	public static int getMobEnchantLevelFromHandler(List<MobEnchantHandler> list, MobEnchant findMobEnchant) {
		for (MobEnchantHandler mobEnchant : list) {
			if (mobEnchant != null) {
				if (mobEnchant.getMobEnchant().equals(findMobEnchant)) {
					return mobEnchant.getEnchantLevel();
				}
			}
		}
		return 0;
	}

	/*
	 * build MobEnchantment list like vanilla's enchantment
	 */
	public static List<MobEnchantmentData> buildEnchantmentList(RandomSource randomIn, int level, boolean allowTresure, boolean allowCursed) {
		List<MobEnchantmentData> list = Lists.newArrayList();
		int i = 1; //Enchantability
		if (i <= 0) {
			return list;
		} else {
			level = level + 1 + randomIn.nextInt(i / 4 + 1) + randomIn.nextInt(i / 4 + 1);
			float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
			level = Mth.clamp(Math.round((float) level + (float) level * f), 1, Integer.MAX_VALUE);
			List<MobEnchantmentData> list1 = makeMobEnchantmentDatas(level, allowTresure, allowCursed);
			if (!list1.isEmpty()) {
				WeightedRandom.getRandomItem(randomIn, list1).ifPresent(list::add);

				while (randomIn.nextInt(50) <= level) {
					if (!list.isEmpty()) {
						removeIncompatible(list1, Util.lastOf(list));
					}
					if (list1.isEmpty()) {
						break;
					}

					WeightedRandom.getRandomItem(randomIn, list1).ifPresent(list::add);
					level /= 2;
				}
			}

			return list;
		}
	}

	/*
	 * get MobEnchantment data.
	 * when not allow rare enchantment,Ignore rare enchantment
	 */
	public static List<MobEnchantmentData> makeMobEnchantmentDatas(int p_185291_0_, boolean allowTresure, boolean allowCursed) {
		List<MobEnchantmentData> list = Lists.newArrayList();

		for (MobEnchant enchantment : MobEnchants.getRegistry()) {
			if ((!enchantment.isCursedEnchant() || allowCursed) && (!enchantment.isTresureEnchant() || allowTresure) && enchantment.isDiscoverable()) {
				for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
					if (p_185291_0_ >= enchantment.getMinEnchantability(i) && p_185291_0_ <= enchantment.getMaxEnchantability(i)) {
						list.add(new MobEnchantmentData(enchantment, i));
						break;
					}
				}
			}
		}

		return list;
	}

	private static void removeIncompatible(List<MobEnchantmentData> dataList, MobEnchantmentData data) {
		Iterator<MobEnchantmentData> iterator = dataList.iterator();

		while (iterator.hasNext()) {
			if (!data.enchantment.isCompatibleWith((iterator.next()).enchantment) || data.enchantment.isDisabled()) {
				iterator.remove();
			}
		}

	}
}