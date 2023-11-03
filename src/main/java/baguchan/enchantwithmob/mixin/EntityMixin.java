package baguchan.enchantwithmob.mixin;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.api.IEnchantedTime;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, remap = false)
public class EntityMixin implements IEnchantedTime {

    @Shadow
    private EntityDimensions dimensions;
    @Shadow
    private float eyeHeight;
    private float differentTime = 1.0F;

    @Override
    public float getDifferentTime() {
        return differentTime;
    }

    @Override
    public void setDifferentTime(float time) {
        differentTime = time;
    }


    @Inject(method = "refreshDimensions", at = @At("RETURN"))
    public void refreshDimensions(CallbackInfo callbackInfo) {
        if (this instanceof IEnchantCap cap) {
            if (cap.getEnchantCap().hasEnchant()) {
                if (MobEnchantUtils.findMobEnchantFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.SMALL.get())) {
                    int level = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.SMALL.get());
                    float cappedScale = Mth.clamp(0.15F * level, 0.0F, 0.9F);
                    float totalWidth = this.dimensions.width * (1.0F - cappedScale);
                    float totalHeight = this.dimensions.height * (1.0F - cappedScale);
                    this.eyeHeight = (this.eyeHeight * (1.0F - cappedScale));

                    dimensions = EntityDimensions.fixed(totalWidth, totalHeight);
                } else if (MobEnchantUtils.findMobEnchantFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.HUGE.get())) {
                    int level = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getEnchantCap().getMobEnchants(), MobEnchants.HUGE.get());

                    float totalWidth = this.dimensions.width * (1.0F + level * 0.15F);
                    float totalHeight = this.dimensions.height * (1.0F + level * 0.15F);
                    this.eyeHeight = (this.eyeHeight * (1.0F + level * 0.15F));
                    dimensions = EntityDimensions.fixed(totalWidth, totalHeight);
                } else if (EnchantConfig.COMMON.changeSizeWhenEnchant.get()) {
                    float totalWidth = this.dimensions.width * 1.025F;
                    float totalHeight = this.dimensions.height * 1.025F;
                    this.eyeHeight = (this.eyeHeight * (1.025F));
                    dimensions = EntityDimensions.fixed(totalWidth, totalHeight);
                }
            }
        }
    }

    public final float getEyeHeight() {
        return this.eyeHeight;
    }

}
