package baguchan.enchantwithmob.mixin;

import baguchan.enchantwithmob.api.IEnchantedTime;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements IEnchantedTime {
    private float differentTime;

    @Override
    public float getDifferentTime() {
        return differentTime;
    }

    @Override
    public void setDifferentTime(float time) {
        differentTime = time;
    }
}
