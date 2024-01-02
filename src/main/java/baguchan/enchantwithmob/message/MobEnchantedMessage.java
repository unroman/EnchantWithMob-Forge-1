package baguchan.enchantwithmob.message;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantHandler;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import baguchan.enchantwithmob.registry.MobEnchants;
import baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MobEnchantedMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnchantWithMob.MODID, "mob_enchant");

    private int entityId;
    private MobEnchant enchantType;
    private int level;

    public MobEnchantedMessage(Entity entity, MobEnchantHandler enchantType) {
        this.entityId = entity.getId();
        this.enchantType = enchantType.getMobEnchant();
        this.level = enchantType.getEnchantLevel();
    }

    public MobEnchantedMessage(int id, MobEnchantHandler enchantType) {
        this.entityId = id;
        this.enchantType = enchantType.getMobEnchant();
        this.level = enchantType.getEnchantLevel();
    }

    public MobEnchantedMessage(Entity entity, MobEnchant enchantType, int level) {
        this.entityId = entity.getId();
        this.enchantType = enchantType;
        this.level = level;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeResourceKey(MobEnchants.getRegistry().getResourceKey(this.enchantType).get());
        buffer.writeInt(this.level);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public MobEnchantedMessage(FriendlyByteBuf buffer) {
        this(buffer.readInt(), new MobEnchantHandler(MobEnchants.getRegistry().get(buffer.readResourceKey(MobEnchants.MOB_ENCHANT_REGISTRY)), buffer.readInt()));
    }


    public static boolean handle(MobEnchantedMessage message, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            Entity entity = Minecraft.getInstance().player.level().getEntity(message.entityId);
                if (entity != null && entity instanceof LivingEntity livingEntity) {
                    if (livingEntity instanceof IEnchantCap cap) {
                        if (!MobEnchantUtils.findMobEnchantHandler(cap.getEnchantCap().getMobEnchants(), message.enchantType)) {
                            cap.getEnchantCap().addMobEnchant((LivingEntity) entity, message.enchantType, message.level);
                        }
                    }
                }
        });
        return true;
    }
}