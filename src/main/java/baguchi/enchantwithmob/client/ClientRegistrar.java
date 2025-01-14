package baguchi.enchantwithmob.client;

import baguchi.enchantwithmob.EnchantWithMob;
import baguchi.enchantwithmob.client.model.EnchantedWindModel;
import baguchi.enchantwithmob.client.model.EnchanterModel;
import baguchi.enchantwithmob.client.overlay.MobEnchantOverlay;
import baguchi.enchantwithmob.client.render.EnchanterRenderer;
import baguchi.enchantwithmob.client.render.layer.EnchantLayer;
import baguchi.enchantwithmob.client.render.layer.EnchantedEyesLayer;
import baguchi.enchantwithmob.client.render.layer.EnchantedWindLayer;
import baguchi.enchantwithmob.client.render.layer.SlimeEnchantLayer;
import baguchi.enchantwithmob.registry.ModEntities;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = EnchantWithMob.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistrar {
	private static final RenderType BLAZE_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_blaze_eyes.png"));
	private static final RenderType CREEPER_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_creeper_eyes.png"));
	private static final RenderType EVOKER_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_evoker_eyes.png"));
	private static final RenderType PILLAGER_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_pillager_eyes.png"));
	private static final RenderType SKELETON_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_skeleton_eyes.png"));
	private static final RenderType SLIME_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_slime_eyes.png"));
	private static final RenderType SPIDER_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_spider_eyes.png"));
	private static final RenderType VEX_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_vex_eyes.png"));
	private static final RenderType VINDICATOR_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_vindicator_eyes.png"));
	private static final RenderType WITCH_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_witch_eyes.png"));
	private static final RenderType WOLF_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_wolf_eyes.png"));
	private static final RenderType ZOMBIE_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_zombie_eyes.png"));
	private static final RenderType GUARDIAN_EYES = EnchantedEyesLayer.enchantedEyes(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "textures/entity/enchant_eye/enchanted_guardian_eyes.png"));

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.ENCHANTER.get(), EnchanterRenderer::new);
	}

	@SubscribeEvent
	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.ENCHANTER, EnchanterModel::createBodyLayer);
		event.registerLayerDefinition(ModModelLayers.ENCHANTED_WIND, EnchantedWindModel::createWindBodyLayer);
	}

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.AddLayers event) {
		event.getContext().getEntityRenderDispatcher().getSkinMap().forEach((model, player) ->
		{
			if (event.getSkin(model) != null) {
				if (player instanceof LivingEntityRenderer) {
					((LivingEntityRenderer<?, ?, ?>) player).addLayer(new EnchantLayer(event.getSkin(model)));
					((LivingEntityRenderer<?, ?, ?>) player).addLayer(new EnchantedWindLayer(event.getSkin(model), event.getEntityModels()));

				}
			}
		});
		event.getEntityTypes().forEach(entityType -> {
			if (event.getRenderer(entityType) instanceof SlimeRenderer r) {
				(r).addLayer(new SlimeEnchantLayer<>(r, event.getEntityModels()));
			}

			if (event.getRenderer(entityType) instanceof LivingEntityRenderer r) {
				r.addLayer(new EnchantLayer(r));
				r.addLayer(new EnchantedWindLayer(r, event.getEntityModels()));

			}


			if (event.getRenderer(entityType) instanceof LivingEntityRenderer r) {
				if (entityType == EntityType.BLAZE) {
					r.addLayer(new EnchantedEyesLayer(r, BLAZE_EYES));
				}
				if (entityType == EntityType.CREEPER) {
					r.addLayer(new EnchantedEyesLayer(r, CREEPER_EYES));
				}
				if (entityType == EntityType.EVOKER) {
					r.addLayer(new EnchantedEyesLayer(r, EVOKER_EYES));
				}
				if (entityType == EntityType.PILLAGER) {
					r.addLayer(new EnchantedEyesLayer(r, PILLAGER_EYES));
				}
				if (entityType == EntityType.STRAY || entityType == EntityType.WITHER || entityType == EntityType.SKELETON || entityType == EntityType.BOGGED) {
					r.addLayer(new EnchantedEyesLayer(r, SKELETON_EYES));
				}
				if (entityType == EntityType.SLIME) {
					r.addLayer(new EnchantedEyesLayer(r, SLIME_EYES));
				}
				if (entityType == EntityType.SPIDER || entityType == EntityType.CAVE_SPIDER) {
					r.addLayer(new EnchantedEyesLayer(r, SPIDER_EYES));
				}
				if (entityType == EntityType.VINDICATOR) {
					r.addLayer(new EnchantedEyesLayer(r, VINDICATOR_EYES));
				}
				if (entityType == EntityType.WITCH) {
					r.addLayer(new EnchantedEyesLayer(r, WITCH_EYES));
				}
				if (entityType == EntityType.WOLF) {
					r.addLayer(new EnchantedEyesLayer(r, WOLF_EYES));
				}
				if (entityType == EntityType.ZOMBIE || entityType == EntityType.HUSK) {
					r.addLayer(new EnchantedEyesLayer(r, ZOMBIE_EYES));
				}
				if (entityType == EntityType.GUARDIAN || entityType == EntityType.ELDER_GUARDIAN) {
					r.addLayer(new EnchantedEyesLayer(r, GUARDIAN_EYES));
				}

			}
		});
    }

    @SubscribeEvent
	public static void registerOverlay(RegisterGuiLayersEvent event) {
		event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(EnchantWithMob.MODID, "mobenchant"), new MobEnchantOverlay());
    }
}
