package baguchi.enchantwithmob.client.render.state;

import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.world.entity.AnimationState;

public class EnchanterRenderState extends IllagerRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState castingAnimationState = new AnimationState();

}
