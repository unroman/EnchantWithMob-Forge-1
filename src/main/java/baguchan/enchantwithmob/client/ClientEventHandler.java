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
import net.neoforged.fml.common.Mod;
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
			if (cap.getEnchantCap().hasOwner()) {

				LivingEntity entity = cap.getEnchantCap().getEnchantOwner();
                if (entity != null) {
                    renderBeam(cap.getEnchantCap(), event.getEntity(), particalTick, matrixStack, bufferBuilder, entity, event.getRenderer());
                }
            }
        }
    }

	public static Vec3 getPosition(Entity p_114803_, double p_114804_, float p_114805_) {
		double d0 = Mth.lerp((double) p_114805_, p_114803_.xOld, p_114803_.getX());
		double d1 = Mth.lerp((double) p_114805_, p_114803_.yOld, p_114803_.getY()) + p_114804_;
		double d2 = Mth.lerp((double) p_114805_, p_114803_.zOld, p_114803_.getZ());
		return new Vec3(d0, d1, d2);
	}

	protected static float getXRotD(LivingEntity livingEntity, Entity target) {
		double d0 = target.getX() - livingEntity.getX();
		double d1 = target.getEyeY() - livingEntity.getEyeY();
		double d2 = target.getZ() - livingEntity.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		return (float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
	}

	protected static float getYRotD(LivingEntity livingEntity, Entity target) {
		double d0 = target.getX() - livingEntity.getX();
		double d1 = target.getZ() - livingEntity.getZ();
		return (float) (Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
	}

	protected static Vec3 calculateViewVector(float p_20172_, float p_20173_) {
		float f = p_20172_ * ((float) Math.PI / 180F);
		float f1 = -p_20173_ * ((float) Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		return new Vec3((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
	}

	private static void renderBeam(@NotNull MobEnchantCapability cap, LivingEntity livingEntity, float totalTickTime, PoseStack poseStack, MultiBufferSource multiBufferSource, Entity target, LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer) {

		poseStack.pushPose();

		double d3 = Mth.lerp((double) totalTickTime, livingEntity.xo, livingEntity.getX());
		double d4 = Mth.lerp((double) totalTickTime, livingEntity.yo, livingEntity.getY()) + livingEntity.getEyeHeight();
		double d5 = Mth.lerp((double) totalTickTime, livingEntity.zo, livingEntity.getZ());
		Vec3 vector3d = target.getRopeHoldPosition(totalTickTime);
		Vec3 vec31 = new Vec3(d3, d4, d5);
		Vec3 vec32 = vector3d.subtract(vec31);
		Vec3 lookAngle = calculateViewVector(getXRotD(livingEntity, target), getYRotD(livingEntity, target));
		float q = totalTickTime * 0.05f * -1.5f;
		float v = 0.2f;
		float w2 = 0.5f;
		float length = (float) (vec32.length() + 0.01D);
		float uv2 = -1.0f + (totalTickTime * -0.2f % 1.0f);
		float uv1 = length * 2.5f + uv2;
		float x1 = Mth.cos(q + (float) Math.PI) * v;
		float z1 = Mth.sin(q + (float) Math.PI) * v;
		float x2 = Mth.cos(q + 0.0f) * v;
		float z2 = Mth.sin(q + 0.0f) * v;
		float x3 = Mth.cos(q + 1.5707964f) * v;
		float z3 = Mth.sin(q + 1.5707964f) * v;
		float x4 = Mth.cos(q + 4.712389f) * v;
		float z4 = Mth.sin(q + 4.712389f) * v;
		float y1 = length;
		float y2 = 0.0f;
		float ux1 = 0.4999f;
		float ux2 = 0.0f;

		poseStack.translate(lookAngle.x(), livingEntity.getEyeHeight() + lookAngle.y(), lookAngle.z());
		int j1;

		VertexConsumer consumer = multiBufferSource.getBuffer(enchantBeamSwirl(cap.isAncient() ? ANCIENT_GLINT : ItemRenderer.ENCHANTED_GLINT_ENTITY));
		poseStack.mulPose(Axis.YP.rotationDegrees(-getYRotD(livingEntity, target)));
		poseStack.mulPose(Axis.XP.rotationDegrees(getXRotD(livingEntity, target)));
		poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

		Matrix4f matrix4f = poseStack.last().pose();
		Matrix3f matrix3f = poseStack.last().normal();
		vertex(consumer, matrix4f, matrix3f, x1, y1, z1, 255, 255, 255, ux1, uv1);
		vertex(consumer, matrix4f, matrix3f, x1, y2, z1, 255, 255, 255, ux1, uv2);
		vertex(consumer, matrix4f, matrix3f, x2, y2, z2, 255, 255, 255, ux2, uv2);
		vertex(consumer, matrix4f, matrix3f, x2, y1, z2, 255, 255, 255, ux2, uv1);
		vertex(consumer, matrix4f, matrix3f, x3, y1, z3, 255, 255, 255, ux1, uv1);
		vertex(consumer, matrix4f, matrix3f, x3, y2, z3, 255, 255, 255, ux1, uv2);
		vertex(consumer, matrix4f, matrix3f, x4, y2, z4, 255, 255, 255, ux2, uv2);
		vertex(consumer, matrix4f, matrix3f, x4, y1, z4, 255, 255, 255, ux2, uv1);

		poseStack.popPose();
	}


	private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, int red, int green, int blue, float ux, float uz) {
		vertexConsumer
				.vertex(matrix4f, x, y, z)
				.color(red, green, blue, 255)
				.uv(ux, uz)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(0xF000F0)
				.normal(matrix3f, 0.0f, 1.0f, 0.0f)
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
