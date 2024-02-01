package baguchan.enchantwithmob.message;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MobEnchantFromOwnerMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnchantWithMob.MODID, "mob_enchant_from_owner");

    private int entityId;
    private int ownerID;

    public MobEnchantFromOwnerMessage(Entity entity, Entity ownerEntity) {
        this.entityId = entity.getId();
        this.ownerID = ownerEntity.getId();
    }

    public MobEnchantFromOwnerMessage(int id, int ownerID) {
        this.entityId = id;
        this.ownerID = ownerID;
    }


    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.ownerID);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public MobEnchantFromOwnerMessage(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

    public static boolean handle(MobEnchantFromOwnerMessage message, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            Entity entity = Minecraft.getInstance().player.level().getEntity(message.entityId);
            Entity ownerEntity = Minecraft.getInstance().player.level().getEntity(message.ownerID);
            if (entity instanceof LivingEntity livingEntity && ownerEntity instanceof LivingEntity) {
                    if (livingEntity instanceof IEnchantCap cap) {
                        cap.getEnchantCap().addOwner((LivingEntity) entity, (LivingEntity) ownerEntity);
                    }
                }
            });
        return true;
    }
}