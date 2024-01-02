package baguchan.enchantwithmob;

import baguchan.enchantwithmob.command.MobEnchantingCommand;
import baguchan.enchantwithmob.message.*;
import baguchan.enchantwithmob.registry.*;
import net.minecraft.world.entity.raid.Raid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EnchantWithMob.MODID)
public class EnchantWithMob {

	// Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

	public static final String MODID = "enchantwithmob";
    public static final String NETWORK_PROTOCOL = "2";


	public EnchantWithMob(IEventBus modEventBus) {
        // Register the setup method for modloading
		modEventBus.addListener(this::preSetup);
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::setupPackets);
		MobEnchants.MOB_ENCHANT.register(modEventBus);
		ModEntities.ENTITIES_REGISTRY.register(modEventBus);
		ModItems.ITEM_REGISTRY.register(modEventBus);
		ModLootItemFunctions.LOOT_REGISTRY.register(modEventBus);
		ModCapability.ATTACHMENT_TYPES.register(modEventBus);

		NeoForge.EVENT_BUS.addListener(this::registerCommands);


		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EnchantConfig.COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EnchantConfig.CLIENT_SPEC);
	}

	private void preSetup(final FMLConstructModEvent event) {
	}

	private void setup(final FMLCommonSetupEvent event) {
		Raid.RaiderType.create("enchanter", ModEntities.ENCHANTER.get(), new int[]{0, 0, 1, 0, 1, 1, 2, 1});
	}

	public void setupPackets(RegisterPayloadHandlerEvent event) {
		IPayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();
		registrar.play(AncientMessage.ID, AncientMessage::new, payload -> payload.client(AncientMessage::handle));
		registrar.play(MobEnchantedMessage.ID, MobEnchantedMessage::new, payload -> payload.client(MobEnchantedMessage::handle));
		registrar.play(MobEnchantFromOwnerMessage.ID, MobEnchantFromOwnerMessage::new, payload -> payload.client(MobEnchantFromOwnerMessage::handle));
		registrar.play(RemoveAllMobEnchantMessage.ID, RemoveAllMobEnchantMessage::new, payload -> payload.client(RemoveAllMobEnchantMessage::handle));
		registrar.play(RemoveMobEnchantOwnerMessage.ID, RemoveMobEnchantOwnerMessage::new, payload -> payload.client(RemoveMobEnchantOwnerMessage::handle));
		registrar.play(SoulParticleMessage.ID, SoulParticleMessage::new, payload -> payload.server(SoulParticleMessage::handle));
	}

	private void registerCommands(RegisterCommandsEvent evt) {
		MobEnchantingCommand.register(evt.getDispatcher());
	}
}
