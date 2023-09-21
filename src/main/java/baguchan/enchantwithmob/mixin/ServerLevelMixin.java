package baguchan.enchantwithmob.mixin;

import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.api.IEnchantedTime;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    protected ServerLevelMixin(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_);
    }

    @Inject(method = "tickNonPassenger", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;tickCount:I", opcode = Opcodes.PUTFIELD), cancellable = true)
    public void tickNonPassenger(Entity instance, CallbackInfo ci) {
        if (instance instanceof IEnchantCap enchantCap && instance instanceof IEnchantedTime enchantedTime) {
            //ajust time
            int fastTime = Mth.clamp(MobEnchantUtils.getMobEnchantLevelFromHandler(enchantCap.getEnchantCap().getMobEnchants(), MobEnchants.FAST), 0, 2);
            int slowTime = Mth.clamp(MobEnchantUtils.getMobEnchantLevelFromHandler(enchantCap.getEnchantCap().getMobEnchants(), MobEnchants.SLOW), 0, 2);

            float different = 1 + fastTime * 0.175F - slowTime * 0.175F;


            if (different > 1F || different < 1F) {
                enchantedTime.setDifferentTime(enchantedTime.getDifferentTime() + different);
                instance.tickCount -= 1;

                ProfilerFiller profilerfiller = this.getProfiler();
                while (enchantedTime.getDifferentTime() >= 1F) {
                    instance.tickCount += 1;
                    this.getProfiler().push(() -> {
                        return Registry.ENTITY_TYPE.getKey(instance.getType()).toString();
                    });
                    profilerfiller.incrementCounter("tickNonPassenger");
                    instance.tick();
                    this.getProfiler().pop();
                    enchantedTime.setDifferentTime(enchantedTime.getDifferentTime() - 1F);
                }

                for (Entity entity : instance.getPassengers()) {
                    this.tickPassenger(instance, entity);
                }
                ci.cancel();
            } else {
                enchantedTime.setDifferentTime(0);
            }
        }
    }

    @Shadow
    private void tickPassenger(Entity p_8663_, Entity p_8664_) {

    }
}
