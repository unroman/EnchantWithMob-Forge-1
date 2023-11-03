package baguchan.enchantwithmob.mixin;

import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantCapability;
import baguchan.enchantwithmob.capability.MobEnchantHandler;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.registry.ModTrackedDatas;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingEntityMixin extends Entity implements IEnchantCap {
    private static final EntityDataAccessor<MobEnchantCapability> MOB_ENCHANT_CAP;

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callbackInfo) {
        if ((this.getEnchantCap().getEnchantOwner().isPresent() || this.getEnchantCap().isFromOwner()) && this.getEnchantCap().hasEnchant()) {
            if (this.getEnchantCap().getEnchantOwner().isEmpty() || !this.getEnchantCap().getEnchantOwner().get().isAlive()) {
                LivingEntity livingEntity = (LivingEntity) ((Object) this);
                this.getEnchantCap().removeMobEnchantFromOwner(livingEntity);
                this.playSound(SoundEvents.ITEM_BREAK, 1.5F, 1.6F);
            } else if (this.distanceToSqr(this.getEnchantCap().getEnchantOwner().get()) > 512) {
                LivingEntity livingEntity = (LivingEntity) ((Object) this);
                this.getEnchantCap().removeMobEnchantFromOwner(livingEntity);
                this.playSound(SoundEvents.ITEM_BREAK, 1.5F, 1.6F);
            }
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    public void defineSynchedData(CallbackInfo callbackInfo) {
        this.entityData.define(MOB_ENCHANT_CAP, new MobEnchantCapability());
    }

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_, CallbackInfo callbackInfo) {
        if (MOB_ENCHANT_CAP.equals(p_21104_)) {
            LivingEntity livingEntity = (LivingEntity) ((Object) this);
            for (MobEnchantHandler handler : this.getEnchantCap().getMobEnchants()) {
                this.getEnchantCap().onChangedEnchantEffect(livingEntity, handler.getMobEnchant(), handler.getEnchantLevel());
            }
            this.refreshDimensions();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.put("MobEnchantData", this.getEnchantCap().serializeNBT());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        MobEnchantCapability mobEnchantCapability = new MobEnchantCapability();
        mobEnchantCapability.deserializeNBT(nbt.getCompound("MobEnchantData"));
        this.setEnchantCap(mobEnchantCapability);
    }

    @Override
    public MobEnchantCapability getEnchantCap() {
        return this.entityData.get(MOB_ENCHANT_CAP);
    }

    @Override
    public void setEnchantCap(MobEnchantCapability cap) {
        this.entityData.set(MOB_ENCHANT_CAP, cap);
    }

    @Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true)
    public void getVoicePitch(CallbackInfoReturnable<Float> cir) {
        int fastTime = Mth.clamp(MobEnchantUtils.getMobEnchantLevelFromHandler(this.getEnchantCap().getMobEnchants(), MobEnchants.FAST.get()), 0, 2);
        int slowTime = Mth.clamp(MobEnchantUtils.getMobEnchantLevelFromHandler(this.getEnchantCap().getMobEnchants(), MobEnchants.SLOW.get()), 0, 2);
        float different = Mth.clamp(cir.getReturnValue() + fastTime * 0.125F - slowTime * 0.125F, 0.1F, 2.0F);

        cir.setReturnValue(different);
    }

    @Shadow
    public boolean isBaby() {
        return false;
    }

    static {
        MOB_ENCHANT_CAP = SynchedEntityData.defineId(LivingEntity.class, ModTrackedDatas.MOB_ENCHANT_CAPABILITY);
    }
}
