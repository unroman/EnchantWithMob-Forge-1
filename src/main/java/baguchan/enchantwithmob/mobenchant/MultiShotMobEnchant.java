package baguchan.enchantwithmob.mobenchant;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.registry.ModCapability;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class MultiShotMobEnchant extends MobEnchant {
	private static boolean isAdding = false;

	public MultiShotMobEnchant(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (!event.loadedFromDisk()) {
			Entity entity = event.getEntity();
			Level level = event.getLevel();
			if (entity instanceof Projectile) {
				Projectile projectile = (Projectile) entity;
                if (!shooterIsLiving(projectile) || !EnchantConfig.COMMON.ALLOW_MULTISHOT_PROJECTILE.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))
					return;
				LivingEntity owner = (LivingEntity) projectile.getOwner();
				MobEnchantUtils.executeIfPresent(owner, MobEnchants.MULTISHOT.get(), () -> {
					if (!level.isClientSide && projectile.tickCount == 0 && !isAdding) {
						isAdding = true;
						CompoundTag compoundNBT = new CompoundTag();
						compoundNBT = projectile.saveWithoutId(compoundNBT);
						addProjectile(projectile, compoundNBT, level, 15.0F);
						addProjectile(projectile, compoundNBT, level, -15.0F);
						isAdding = false;
					}
				});
			}
		}
	}

	private static void addProjectile(Projectile projectile, CompoundTag compoundNBT, Level level, float rotation) {
		Projectile newProjectile = (Projectile) projectile.getType().create(level);
		UUID uuid = newProjectile.getUUID();
		newProjectile.load(compoundNBT);
		newProjectile.setUUID(uuid);
		Vec3 vector3d = newProjectile.getDeltaMovement().yRot((float) (Math.PI / rotation));

		newProjectile.setDeltaMovement(vector3d);
		float f = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
		newProjectile.setYRot((float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)));
		newProjectile.setXRot((float) (Mth.atan2(vector3d.y, (double) f) * (double) (180F / (float) Math.PI)));
		newProjectile.yRotO = newProjectile.getYRot();
		newProjectile.xRotO = newProjectile.getXRot();
		if (newProjectile instanceof Projectile) {
			Projectile newDamagingProjectile = (Projectile) newProjectile;
			Vec3 newPower = new Vec3(newDamagingProjectile.getDeltaMovement().x, newDamagingProjectile.getDeltaMovement().y, newDamagingProjectile.getDeltaMovement().z).yRot((float) (Math.PI / rotation));

			newDamagingProjectile.setDeltaMovement(newPower);
		}

		if (newProjectile instanceof AbstractArrow) {
			((AbstractArrow) newProjectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		}

		newProjectile.getData(ModCapability.ITEM_MOB_ENCHANT.get()).setHasEnchant(true);

		level.addFreshEntity(newProjectile);
	}

	public static boolean shooterIsLiving(Projectile projectile) {
		return projectile.getOwner() != null && projectile.getOwner() instanceof LivingEntity;
	}

	public int getMinEnchantability(int enchantmentLevel) {
		return 10;
	}

	public int getMaxEnchantability(int enchantmentLevel) {
		return this.getMinEnchantability(enchantmentLevel) + 40;
	}

	@Override
	public boolean isCompatibleMob(LivingEntity livingEntity) {
        return EnchantConfig.COMMON.WHITELIST_SHOOT_ENTITY.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString()) || super.isCompatibleMob(livingEntity);
	}
}
