package baguchan.enchantwithmob.client;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
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
import org.joml.Vector3d;

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
			if (cap.getEnchantCap().hasOwner()) {

				LivingEntity entity = cap.getEnchantCap().getEnchantOwner();
                if (entity != null) {
                    renderBeam(cap.getEnchantCap(), event.getEntity(), particalTick, matrixStack, bufferBuilder, entity, event.getRenderer());
                }
            }
        }
    }

	private static void renderBeam(@NotNull MobEnchantCapability cap, LivingEntity p_229118_1_, float p_229118_2_, PoseStack p_229118_3_, MultiBufferSource p_229118_4_, Entity p_229118_5_, LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer) {
		p_229118_3_.pushPose();
		Vec3 vector3d = p_229118_5_.getRopeHoldPosition(p_229118_2_);
		double d0 = (double) (Mth.lerp(p_229118_2_, p_229118_1_.yBodyRot, p_229118_1_.yBodyRotO) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
		Vector3d vector3d1 = new Vector3d(0.0D, (double) p_229118_1_.getEyeHeight() / 2, 0.0F);
		double d1 = Math.cos(d0) * vector3d1.z + Math.sin(d0) * vector3d1.x;
		double d2 = Math.sin(d0) * vector3d1.z - Math.cos(d0) * vector3d1.x;
		double d3 = Mth.lerp((double) p_229118_2_, p_229118_1_.xo, p_229118_1_.getX()) + d1;
		double d4 = Mth.lerp((double) p_229118_2_, p_229118_1_.yo, p_229118_1_.getY()) + vector3d1.y;
		double d5 = Mth.lerp((double) p_229118_2_, p_229118_1_.zo, p_229118_1_.getZ()) + d2;
		p_229118_3_.translate(d1, vector3d1.y, d2);
		float f = (float) (vector3d.x - d3);
		float f1 = (float) (vector3d.y - d4);
		float f2 = (float) (vector3d.z - d5);
		float f3 = 0.1F;
		VertexConsumer ivertexbuilder = p_229118_4_.getBuffer(enchantBeamSwirl(cap.isAncient() ? ANCIENT_GLINT : ItemRenderer.ENCHANTED_GLINT_ENTITY));
		Matrix4f matrix4f = p_229118_3_.last().pose();
		Matrix3f matrix3f = p_229118_3_.last().normal();
		float f4 = Mth.fastInvCubeRoot(f * f + f2 * f2) * 0.1F / 2.0F;
		float f5 = f2 * f4;
		float f6 = f * f4;
		BlockPos blockpos = BlockPos.containing(p_229118_1_.getEyePosition(p_229118_2_));
		BlockPos blockpos1 = BlockPos.containing(p_229118_5_.getEyePosition(p_229118_2_));
		int i = getBlockLightLevel(p_229118_1_, blockpos);
		int j = getBlockLightLevel(p_229118_5_, blockpos1);
		int k = p_229118_1_.level().getBrightness(LightLayer.SKY, blockpos);
		int l = p_229118_1_.level().getBrightness(LightLayer.SKY, blockpos1);
		int j1;
		for (j1 = 0; j1 <= 24; ++j1) {
			addVertexPair(ivertexbuilder, matrix4f, matrix3f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, j1, false);
		}

		for (j1 = 24; j1 >= 0; --j1) {
			addVertexPair(ivertexbuilder, matrix4f, matrix3f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
		}
		p_229118_3_.popPose();
	}


	private static void addVertexPair(VertexConsumer p_174308_, Matrix4f p_254405_, Matrix3f matrix3f, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
		float f = (float) p_174321_ / 24.0F;
		int i = (int) Mth.lerp(f, (float) p_174313_, (float) p_174314_);
		int j = (int) Mth.lerp(f, (float) p_174315_, (float) p_174316_);
		int k = LightTexture.pack(i, j);
		float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
		float f2 = 0.5F * f1;
		float f3 = 0.4F * f1;
		float f4 = 0.3F * f1;
		float f5 = p_174310_ * f;
		float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
		float f7 = p_174312_ * f;
		p_174308_.vertex(p_254405_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(1F, 1F, 1F, 1.0F).uv(!p_174322_ ? 0 : 1.0F, !p_174322_ ? 0 : 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(k).normal(matrix3f, 0F, 1F, 0F).endVertex();
		p_174308_.vertex(p_254405_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(1F, 1F, 1F, 1.0F).uv(p_174322_ ? 0 : 1.0F, p_174322_ ? 0 : 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(k).normal(matrix3f, 0F, 1F, 0F).endVertex();
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
