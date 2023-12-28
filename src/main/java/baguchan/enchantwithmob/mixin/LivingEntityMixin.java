package baguchan.enchantwithmob.mixin;

import bagu_chan.bagus_lib.api.IBaguPacket;
import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantCapability;
import baguchan.enchantwithmob.message.MobEnchantedMessage;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IEnchantCap, IBaguPacket {
    public MobEnchantCapability capability = new MobEnchantCapability();

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
        return this.capability;
    }

    @Override
    public void setEnchantCap(MobEnchantCapability cap) {
        this.capability = cap;
    }
    @Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true)
    public void getVoicePitch(CallbackInfoReturnable<Float> cir) {
        int fastTime = Mth.clamp(MobEnchantUtils.getMobEnchantLevelFromHandler(this.getEnchantCap().getMobEnchants(), MobEnchants.FAST.get()), 0, 2);
        int slowTime = Mth.clamp(MobEnchantUtils.getMobEnchantLevelFromHandler(this.getEnchantCap().getMobEnchants(), MobEnchants.SLOW.get()), 0, 2);
        float different = Mth.clamp(cir.getReturnValue() + fastTime * 0.125F - slowTime * 0.125F, 0.1F, 2.0F);

        cir.setReturnValue(different);
    }

    @Override
    public void resync(Entity entity, int id) {
        if (!this.level().isClientSide) {
            for (int i = 0; i < this.getEnchantCap().getMobEnchants().size(); i++) {
                MobEnchantedMessage message = new MobEnchantedMessage(this, this.getEnchantCap().getMobEnchants().get(i));
                EnchantWithMob.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), message);
            }
        }
    }

    @Shadow
    public boolean isBaby() {
        return false;
    }
}
