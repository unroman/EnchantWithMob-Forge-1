package baguchan.enchantwithmob.message;

import baguchan.enchantwithmob.api.IEnchantCap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;

public class RemoveAllMobEnchantMessage {
    private int entityId;

    public RemoveAllMobEnchantMessage(Entity entity) {
        this.entityId = entity.getId();
    }

    public RemoveAllMobEnchantMessage(int id) {
        this.entityId = id;
    }

    public void serialize(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
    }

    public static RemoveAllMobEnchantMessage deserialize(FriendlyByteBuf buffer) {
        int entityId = buffer.readInt();

        return new RemoveAllMobEnchantMessage(entityId);
    }

    public void handle(CustomPayloadEvent.Context context) {

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().player.level().getEntity(entityId);
                if (entity != null && entity instanceof LivingEntity livingEntity) {
                    if (livingEntity instanceof IEnchantCap cap) {
                        cap.getEnchantCap().removeAllMobEnchant((LivingEntity) entity);
                    }
                }
            });
        }
    }
}