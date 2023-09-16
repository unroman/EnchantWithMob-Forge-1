package baguchan.enchantwithmob.mixin;

import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.api.IEnchantedTime;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tickNonPassenger", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;tickCount:I", opcode = Opcodes.PUTFIELD))
    public void tickNonPassenger(Entity p_8648_, CallbackInfo ci) {
        if (p_8648_ instanceof IEnchantCap enchantCap && p_8648_ instanceof IEnchantedTime enchantedTime) {
            int timeLevel = MobEnchantUtils.getMobEnchantLevelFromHandler(enchantCap.getEnchantCap().getMobEnchants(), MobEnchants.FAST.get());

            enchantedTime.setDifferentTime(enchantedTime.getDifferentTime() + timeLevel * 0.175F);
            while (enchantedTime.getDifferentTime() >= 1F) {
                p_8648_.tickCount += 1;
                p_8648_.tick();
                enchantedTime.setDifferentTime(enchantedTime.getDifferentTime() - 1F);
            }
        }
    }
}
