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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static baguchan.enchantwithmob.client.render.layer.EnchantLayer.ANCIENT_GLINT;
import static baguchan.enchantwithmob.client.render.layer.EnchantLayer.enchantBeamSwirl;

/*
 * Base from Bumble Zone Lazer Layer
 * https://github.com/TelepathicGrunt/Bumblezone/blob/1.20-Arch/common/src/main/java/com/telepathicgrunt/the_bumblezone/client/rendering/cosmiccrystal/CosmicCrystalRenderer.java
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = EnchantWithMob.MODID, value = Dist.CLIENT)
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
			if (cap.getEnchantCap().hasOwner()) {

				LivingEntity entity = cap.getEnchantCap().getEnchantOwner();
                if (entity != null) {
                    renderBeam(cap.getEnchantCap(), event.getEntity(), particalTick, matrixStack, bufferBuilder, entity, event.getRenderer());
                }
            }
        }
    }

	public static Vec3 getPosition(Entity p_114803_, double p_114804_, float p_114805_) {
		double d0 = Mth.lerp(p_114805_, p_114803_.xOld, p_114803_.getX());
		double d1 = Mth.lerp(p_114805_, p_114803_.yOld, p_114803_.getY()) + p_114804_;
		double d2 = Mth.lerp(p_114805_, p_114803_.zOld, p_114803_.getZ());
		return new Vec3(d0, d1, d2);
	}

	protected static float getXRotD(LivingEntity livingEntity, Entity target, float totalTick) {
		double d0 = target.getPosition(totalTick).x - livingEntity.getPosition(totalTick).x;
		double d1 = target.getPosition(totalTick).y - livingEntity.getPosition(totalTick).y;
		double d2 = target.getPosition(totalTick).z - livingEntity.getPosition(totalTick).z;
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		return (float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
	}

	protected static float getYRotD(LivingEntity livingEntity, Entity target, float totalTick) {
		double d0 = target.getPosition(totalTick).x - livingEntity.getPosition(totalTick).x;
		double d1 = target.getPosition(totalTick).z - livingEntity.getPosition(totalTick).z;
		return (float) (Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
	}

	private static void renderBeam(@NotNull MobEnchantCapability cap, LivingEntity livingEntity, float totalTickTime, PoseStack poseStack, MultiBufferSource multiBufferSource, Entity target, LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer) {

		poseStack.pushPose();

		double d3 = Mth.lerp(totalTickTime, livingEntity.xo, livingEntity.getX());
		double d4 = Mth.lerp(totalTickTime, livingEntity.yo, livingEntity.getY()) + livingEntity.getBbHeight() * 0.5F;
		double d5 = Mth.lerp(totalTickTime, livingEntity.zo, livingEntity.getZ());
		Vec3 vector3d = target.getEyePosition(totalTickTime);
		Vec3 vec31 = new Vec3(d3, d4, d5);
		Vec3 vec32 = vector3d.subtract(vec31);

		poseStack.translate(0, +livingEntity.getBbHeight() * 0.5F, 0);
		int j1;

		float xRot = getXRotD(livingEntity, target, totalTickTime);
		float yRot = getYRotD(livingEntity, target, totalTickTime);


		poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
		poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
		poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

		Matrix4f matrix4f = poseStack.last().pose();
		Matrix3f matrix3f = poseStack.last().normal();
		float intensity = cap.getMobEnchants().size() < 3 ? ((float) cap.getMobEnchants().size() / 3) : 3;
		int intensityCacl = (int) (intensity / 3 * 255);
		int i = 1;
		float f = 1.0F;
		float f1 = totalTickTime;
		float f2 = f1 * 0.5F % 1.0F;
		float f4 = (float) (vec32.length() + 1.0);
		float f7 = f1 * 0.05F * -1.5F;
		int j = 255;
		int k = 255;
		int l = 255;
		float f11 = Mth.cos(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
		float f12 = Mth.sin(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
		float f13 = Mth.cos(f7 + (float) (Math.PI / 4)) * 0.282F;
		float f14 = Mth.sin(f7 + (float) (Math.PI / 4)) * 0.282F;
		float f15 = Mth.cos(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
		float f16 = Mth.sin(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
		float f17 = Mth.cos(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
		float f18 = Mth.sin(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
		float f19 = Mth.cos(f7 + (float) Math.PI) * 0.2F;
		float f20 = Mth.sin(f7 + (float) Math.PI) * 0.2F;
		float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
		float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
		float f23 = Mth.cos(f7 + (float) (Math.PI / 2)) * 0.2F;
		float f24 = Mth.sin(f7 + (float) (Math.PI / 2)) * 0.2F;
		float f25 = Mth.cos(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
		float f26 = Mth.sin(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
		float f27 = 0.0F;
		float f28 = 0.4999F;
		float f29 = -1.0F + f2;
		float f30 = f4 * 2.5F + f29;

		VertexConsumer consumer = multiBufferSource.getBuffer(enchantBeamSwirl(cap.isAncient() ? ANCIENT_GLINT : ItemRenderer.ENCHANTED_GLINT_ENTITY));
		PoseStack.Pose posestack$pose = poseStack.last();
        vertex(consumer, matrix4f, posestack$pose, f19, f4, f20, j, k, l, 0.4999F, f30, intensity);
        vertex(consumer, matrix4f, posestack$pose, f19, 0.0F, f20, j, k, l, 0.4999F, f29, intensity);
        vertex(consumer, matrix4f, posestack$pose, f21, 0.0F, f22, j, k, l, 0.0F, f29, intensity);
        vertex(consumer, matrix4f, posestack$pose, f21, f4, f22, j, k, l, 0.0F, f30, intensity);
        vertex(consumer, matrix4f, posestack$pose, f23, f4, f24, j, k, l, 0.4999F, f30, intensity);
        vertex(consumer, matrix4f, posestack$pose, f23, 0.0F, f24, j, k, l, 0.4999F, f29, intensity);
        vertex(consumer, matrix4f, posestack$pose, f25, 0.0F, f26, j, k, l, 0.0F, f29, intensity);
        vertex(consumer, matrix4f, posestack$pose, f25, f4, f26, j, k, l, 0.0F, f30, intensity);
		float f31 = 0.0F;

        vertex(consumer, matrix4f, posestack$pose, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F, intensity);
        vertex(consumer, matrix4f, posestack$pose, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F, intensity);
        vertex(consumer, matrix4f, posestack$pose, f17, f4, f18, j, k, l, 1.0F, f31, intensity);
        vertex(consumer, matrix4f, posestack$pose, f15, f4, f16, j, k, l, 0.5F, f31, intensity);
		poseStack.popPose();
	}


    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, PoseStack.Pose pose, float x, float y, float z, int red, int green, int blue, float ux, float uz, float alpha) {
		vertexConsumer
				.vertex(matrix4f, x, y, z)
				.color(red, green, blue, alpha)
				.uv(ux, uz)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(0xF000F0)
                .normal(pose, 0.0f, 1.0f, 0.0f)
				.endVertex();
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
