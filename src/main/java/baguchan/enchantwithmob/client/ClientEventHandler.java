package baguchan.enchantwithmob.client;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static baguchan.enchantwithmob.client.render.layer.EnchantLayer.ANCIENT_GLINT;
import static baguchan.enchantwithmob.client.render.layer.EnchantLayer.enchantBeamSwirl;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
	protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
	protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_GLINT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityGlintShader);
	protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader);
	protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
	protected static final RenderStateShard.TexturingStateShard ENTITY_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
		setupGlintTexturing(0.16F);
	}, () -> {
		RenderSystem.resetTextureMatrix();
	});

	@SubscribeEvent
	public static void renderEnchantBeam(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        PoseStack matrixStack = event.getPoseStack();
        MultiBufferSource bufferBuilder = event.getMultiBufferSource();
        float particalTick = event.getPartialTick();
        if (event.getEntity() instanceof IEnchantCap cap) {
            if (cap.getEnchantCap().getEnchantOwner().isPresent()) {

                LivingEntity entity = cap.getEnchantCap().getEnchantOwner().get();
                if (entity != null) {
                    renderBeam(cap.getEnchantCap(), event.getEntity(), particalTick, matrixStack, bufferBuilder, entity, event.getRenderer());
                }
            }
        }
        ;

    }

	public static Vec3 getPosition(Entity p_114803_, double p_114804_, float p_114805_) {
		double d0 = Mth.lerp((double) p_114805_, p_114803_.xOld, p_114803_.getX());
		double d1 = Mth.lerp((double) p_114805_, p_114803_.yOld, p_114803_.getY()) + p_114804_;
		double d2 = Mth.lerp((double) p_114805_, p_114803_.zOld, p_114803_.getZ());
		return new Vec3(d0, d1, d2);
	}

	private static void renderBeam(@NotNull MobEnchantCapability cap, LivingEntity livingEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, Entity target, LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer) {
		float tick = (float) livingEntity.tickCount + partialTick;
		poseStack.pushPose();
		Vec3 vector3d = target.getRopeHoldPosition(partialTick);
		float f3 = target.getEyeHeight();
		Vec3 vec3 = getPosition(livingEntity, (double) livingEntity.getBbHeight() * 0.5D, partialTick);
		Vec3 vec31 = getPosition(target, (double) f3, partialTick);
		Vec3 vec32 = vec3.subtract(vec31);
		float f4 = (float) (vec32.length() + 1.0D);
		vec32 = vec32.normalize();
		float f5 = (float) Math.acos(vec32.y);
		float f6 = (float) Math.atan2(vec32.z, vec32.x);
		poseStack.mulPose(Axis.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
		poseStack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));
		int i = 1;
		float f7 = partialTick * 0.05F * -1.5F;
		float f9 = 0.2F;
		float f10 = 0.282F;
		float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
		float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
		float f13 = Mth.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
		float f14 = Mth.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
		float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
		float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
		float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
		float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
		float f19 = Mth.cos(f7 + (float) Math.PI) * 0.2F;
		float f20 = Mth.sin(f7 + (float) Math.PI) * 0.2F;
		float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
		float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
		float f23 = Mth.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
		float f24 = Mth.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
		float f25 = Mth.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
		float f26 = Mth.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
		float f27 = 0.0F;
		float f28 = 0.4999F;
		float f29 = -1.0F + partialTick;
		float f30 = f4 * 2.5F + f29;
		float f31 = 0.0F;
		VertexConsumer vertexconsumer = multiBufferSource.getBuffer(enchantBeamSwirl(cap.isAncient() ? ANCIENT_GLINT : ItemRenderer.ENCHANTED_GLINT_ENTITY));
		Matrix4f matrix4f = poseStack.last().pose();
		Matrix3f matrix3f = poseStack.last().normal();
		vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, 1F, 1F, 1F, 0.4999F, f30);
		vertex(vertexconsumer, matrix4f, matrix3f, f19, 0.0F, f20, 1F, 1F, 1F, 0.4999F, f29);
		vertex(vertexconsumer, matrix4f, matrix3f, f21, 0.0F, f22, 1F, 1F, 1F, 0.0F, f29);
		vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, 1F, 1F, 1F, 0.0F, f30);
		vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, 1F, 1F, 1F, 0.4999F, f30);
		vertex(vertexconsumer, matrix4f, matrix3f, f23, 0.0F, f24, 1F, 1F, 1F, 0.4999F, f29);
		vertex(vertexconsumer, matrix4f, matrix3f, f25, 0.0F, f26, 1F, 1F, 1F, 0.0F, f29);
		vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, 1F, 1F, 1F, 0.0F, f30);

		vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, 1F, 1F, 1F, 0.5F, f31 + 0.5F);
		vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, 1F, 1F, 1F, 1.0F, f31 + 0.5F);
		vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, 1F, 1F, 1F, 1.0F, f31);
		vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, 1F, 1F, 1F, 0.5F, f31);
		poseStack.popPose();
	}

	private static void vertex(VertexConsumer p_253637_, Matrix4f p_253920_, Matrix3f p_253881_, float p_253994_, float p_254492_, float p_254474_, float red, float green, float blue, float u, float v) {
		p_253637_.vertex(p_253920_, p_253994_, p_254492_, p_254474_).color(red, green, blue, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_253881_, 0.0F, 1.0F, 0.0F).endVertex();
	}

	protected static int getSkyLightLevel(Entity p_239381_1_, BlockPos p_239381_2_) {
        return p_239381_1_.level().getBrightness(LightLayer.SKY, p_239381_2_);
	}

	protected static int getBlockLightLevel(Entity p_225624_1_, BlockPos p_225624_2_) {
        return p_225624_1_.isOnFire() ? 15 : p_225624_1_.level().getBrightness(LightLayer.BLOCK, p_225624_2_);
	}

	private static void setupGlintTexturing(float p_110187_) {
		long i = Util.getMillis() * 8L;
		float f = (float) (i % 110000L) / 110000.0F;
		float f1 = (float) (i % 30000L) / 30000.0F;
		Matrix4f matrix4f = (new Matrix4f()).translation(-f, f1, 0.0F);
		matrix4f.rotateZ(0.17453292F).scale(p_110187_);
		RenderSystem.setTextureMatrix(matrix4f);
	}
}
