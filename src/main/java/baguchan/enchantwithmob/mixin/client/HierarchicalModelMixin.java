package baguchan.enchantwithmob.mixin.client;

import baguchan.enchantwithmob.api.IEnchantedTime;
import net.minecraft.client.model.HierarchicalModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HierarchicalModel.class)
public class HierarchicalModelMixin implements IEnchantedTime {
    private float differentTime = 1.0F;

    @Override
    public float getDifferentTime() {
        return differentTime;
    }

    @Override
    public void setDifferentTime(float time) {
        differentTime = time;
    }
}
