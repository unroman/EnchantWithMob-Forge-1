package baguchan.enchantwithmob.mixin.client;

import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.client.render.layer.EnchantLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.state.WitherSkullRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WitherSkullRenderer.class, remap = false)
public class WitherSkullRendererMixin {

	@Shadow
	@Final
	private SkullModel model;

	@Inject(method = "render", at = @At("TAIL"))
	public void render(WitherSkullRenderState p_116484_, float p_116485_, float p_116486_, PoseStack p_116487_, MultiBufferSource p_116488_, int p_116489_, CallbackInfo callbackInfo) {
		if (p_116484_ instanceof IEnchantCap cap) {
            if (cap.getEnchantCap().hasEnchant()) {
                if (cap.getEnchantCap().hasEnchant()) {
                    p_116487_.pushPose();
                    p_116487_.scale(-1.0F, -1.0F, 1.0F);
                    VertexConsumer vertexconsumer = p_116488_.getBuffer(EnchantLayer.enchantSwirl(cap.getEnchantCap().isAncient() ? EnchantLayer.ANCIENT_GLINT : ItemRenderer.ENCHANTED_GLINT_ENTITY));
					this.model.setupAnim(0.0F, p_116484_.yRot, p_116484_.xRot);
                    this.model.renderToBuffer(p_116487_, vertexconsumer, p_116489_, OverlayTexture.NO_OVERLAY);
                    p_116487_.popPose();
				}
			}
			;
		}
	}
}
