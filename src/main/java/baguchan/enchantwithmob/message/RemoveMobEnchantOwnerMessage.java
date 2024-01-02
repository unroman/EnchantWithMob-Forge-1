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

public class RemoveMobEnchantOwnerMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnchantWithMob.MODID, "remove_mob_enchant_owner");

    private int entityId;

    public RemoveMobEnchantOwnerMessage(Entity entity) {
        this.entityId = entity.getId();
    }

    public RemoveMobEnchantOwnerMessage(int id) {
        this.entityId = id;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
    }

    public RemoveMobEnchantOwnerMessage(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public static boolean handle(RemoveMobEnchantOwnerMessage message, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            Entity entity = Minecraft.getInstance().player.level().getEntity(message.entityId);
                if (entity != null && entity instanceof LivingEntity livingEntity) {
                    if (livingEntity instanceof IEnchantCap cap) {
                        cap.getEnchantCap().removeOwner(livingEntity);
                    }
                }
            });
        return true;
    }
}