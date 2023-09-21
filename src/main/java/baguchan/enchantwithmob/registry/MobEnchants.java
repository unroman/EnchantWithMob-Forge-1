package baguchan.enchantwithmob.registry;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.mobenchant.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobEnchants {
	public static final ResourceKey<Registry<MobEnchant>> MOB_ENCHANT = key("mob_enchant");
	public static final DeferredRegister<MobEnchant> MOB_ENCHANTS_REGISTER = DeferredRegister.create(MOB_ENCHANT, EnchantWithMob.MODID);
	public static final Supplier<IForgeRegistry<MobEnchant>> MOB_ENCHANTS = MOB_ENCHANTS_REGISTER.makeRegistry(MobEnchant.class, () -> new RegistryBuilder<MobEnchant>()
			.addCallback(MobEnchant.class)
			.setName(new ResourceLocation(EnchantWithMob.MODID, "mob_enchant"))
			.setDefaultKey(new ResourceLocation(EnchantWithMob.MODID, "protection")));

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(new ResourceLocation(name));
	}

	public static final MobEnchant PROTECTION = new ProtectionMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.COMMON, 4));
	public static final MobEnchant TOUGH = new ToughMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 2)).addAttributesModifier(Attributes.ARMOR, "313644c5-ead2-4670-b9eb-0b41d59ce5a2", (double) 2.0F, AttributeModifier.Operation.ADDITION).addAttributesModifier(Attributes.ARMOR_TOUGHNESS, "8135df8f-38d9-490a-8d6f-c908fa973b34", (double) 0.5F, AttributeModifier.Operation.ADDITION);
	public static final MobEnchant SPEEDY = new SpeedyMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.UNCOMMON, 2)).addAttributesModifier(Attributes.MOVEMENT_SPEED, "501f27a9-4a75-4c2e-a2ab-91eeed71d748", (double) 0.05F, AttributeModifier.Operation.ADDITION);
	public static final MobEnchant STRONG = new StrongMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.COMMON, 3));
	public static final MobEnchant THORN = new ThornEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 3));
	public static final MobEnchant HEALTH_BOOST = new HealthBoostMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 5)).addAttributesModifier(Attributes.MAX_HEALTH, "f5d32c9f-2a3d-4157-bbf7-469d348ce097", 2.0D, AttributeModifier.Operation.ADDITION);
	public static final MobEnchant POISON = new PoisonMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.RARE, 3));
	public static final MobEnchant POISON_CLOUD = new PoisonCloudMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.RARE, 2));
	public static final MobEnchant HUGE = new HugeMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 2)).addAttributesModifier(Attributes.MAX_HEALTH, "c988bca7-7fa9-4fea-bb44-c3625ac74241", 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final MobEnchant MULTISHOT = new MultiShotMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.RARE, 1));

	public static final MobEnchant DEFLECT = new DeflectMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 1));
	public static final MobEnchant SMALL = new SmallMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 2)).addAttributesModifier(Attributes.MAX_HEALTH, "b4170c63-d50b-a0ee-15b7-9156c6e41940", -0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final MobEnchant FAST = new FastMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 2));
	public static final MobEnchant SLOW = new SlowMobEnchant(new MobEnchant.Properties(MobEnchant.Rarity.VERY_RARE, 2));
	@SubscribeEvent
	public static void registerMobEnchant(RegistryEvent.Register<MobEnchant> event) {
		event.getRegistry().register(PROTECTION.setRegistryName("protection"));
		event.getRegistry().register(TOUGH.setRegistryName("tough"));
		event.getRegistry().register(SPEEDY.setRegistryName("speedy"));
		event.getRegistry().register(STRONG.setRegistryName("strong"));
		event.getRegistry().register(THORN.setRegistryName("thorn"));
		event.getRegistry().register(HEALTH_BOOST.setRegistryName("health_boost"));
		event.getRegistry().register(POISON.setRegistryName("poison"));
		event.getRegistry().register(POISON_CLOUD.setRegistryName("poison_cloud"));
		event.getRegistry().register(HUGE.setRegistryName("huge"));
		event.getRegistry().register(MULTISHOT.setRegistryName("multishot"));
		event.getRegistry().register(DEFLECT.setRegistryName("deflect"));
		event.getRegistry().register(SMALL.setRegistryName("small"));
		event.getRegistry().register(FAST.setRegistryName("fast"));
		event.getRegistry().register(SLOW.setRegistryName("slow"));
	}
}