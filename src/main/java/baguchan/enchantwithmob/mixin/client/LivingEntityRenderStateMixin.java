package baguchan.enchantwithmob.mixin.client;

import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantCapability;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements IEnchantCap {
    public MobEnchantCapability enchantCap;

    @Override
    public MobEnchantCapability getEnchantCap() {
        return enchantCap;
    }

    @Override
    public void setEnchantCap(MobEnchantCapability capability) {
        enchantCap = capability;
    }
}
