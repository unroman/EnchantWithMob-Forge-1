package baguchan.enchantwithmob.message;

import baguchan.enchantwithmob.EnchantWithMob;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class AncientMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(EnchantWithMob.MODID, "ancient");

    private int entityId;
    private boolean isAncient;

    public AncientMessage(Entity entity, boolean ancient) {
        this.entityId = entity.getId();
        this.isAncient = ancient;
    }

    public AncientMessage(int id, boolean ancient) {
        this.entityId = id;
        this.isAncient = ancient;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeBoolean(this.isAncient);
    }

    public AncientMessage(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readBoolean());
    }

    public static boolean handle(AncientMessage message, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            Entity entity = Minecraft.getInstance().player.level().getEntity(message.entityId);
            if (entity != null && entity instanceof LivingEntity livingEntity) {
                if (livingEntity instanceof IEnchantCap cap) {
                    cap.getEnchantCap().setEnchantType((LivingEntity) entity, message.isAncient ? MobEnchantCapability.EnchantType.ANCIENT : MobEnchantCapability.EnchantType.NORMAL);
                }

            }
        });
        return true;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}