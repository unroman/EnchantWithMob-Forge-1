package baguchan.enchantwithmob;

import baguchan.enchantwithmob.command.MobEnchantingCommand;
import baguchan.enchantwithmob.message.*;
import baguchan.enchantwithmob.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.raid.Raid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EnchantWithMob.MODID)
public class EnchantWithMob {

	// Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

	public static final String MODID = "enchantwithmob";
    public static final String NETWORK_PROTOCOL = "2";
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "net"))
			.networkProtocolVersion(() -> "2")
			.clientAcceptedVersions("2"::equals)
			.serverAcceptedVersions("2"::equals)
            .simpleChannel();


	public EnchantWithMob(IEventBus modEventBus) {

        this.setupMessages();
        // Register the setup method for modloading
		modEventBus.addListener(this::preSetup);
		modEventBus.addListener(this::setup);
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

	private void setupMessages() {
        CHANNEL.messageBuilder(SoulParticleMessage.class, 0)
                .encoder(SoulParticleMessage::serialize).decoder(SoulParticleMessage::deserialize)
                .consumerMainThread(SoulParticleMessage::handle)
                .add();
        CHANNEL.messageBuilder(AncientMessage.class, 1)
                .encoder(AncientMessage::serialize).decoder(AncientMessage::deserialize)
                .consumerMainThread(AncientMessage::handle)
                .add();
        CHANNEL.messageBuilder(MobEnchantedMessage.class, 2)
                .encoder(MobEnchantedMessage::serialize).decoder(MobEnchantedMessage::deserialize)
                .consumerMainThread(MobEnchantedMessage::handle)
                .add();
        CHANNEL.messageBuilder(MobEnchantFromOwnerMessage.class, 3)
                .encoder(MobEnchantFromOwnerMessage::serialize).decoder(MobEnchantFromOwnerMessage::deserialize)
                .consumerMainThread(MobEnchantFromOwnerMessage::handle)
                .add();
        CHANNEL.messageBuilder(RemoveAllMobEnchantMessage.class, 4)
                .encoder(RemoveAllMobEnchantMessage::serialize).decoder(RemoveAllMobEnchantMessage::deserialize)
                .consumerMainThread(RemoveAllMobEnchantMessage::handle)
                .add();
        CHANNEL.messageBuilder(RemoveMobEnchantOwnerMessage.class, 6)
                .encoder(RemoveMobEnchantOwnerMessage::serialize).decoder(RemoveMobEnchantOwnerMessage::deserialize)
                .consumerMainThread(RemoveMobEnchantOwnerMessage::handle)
                .add();
    }

	private void registerCommands(RegisterCommandsEvent evt) {
		MobEnchantingCommand.register(evt.getDispatcher());
	}
}
