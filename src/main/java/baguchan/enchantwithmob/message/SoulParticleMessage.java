package baguchan.enchantwithmob.message;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
public class SoulParticleMessage {
	private int entityId;

	public SoulParticleMessage(Entity entity) {
		this.entityId = entity.getId();
	}

	public SoulParticleMessage(int id) {
		this.entityId = id;
	}

	public void serialize(FriendlyByteBuf buffer) {
		buffer.writeInt(this.entityId);
	}

	public static SoulParticleMessage deserialize(FriendlyByteBuf buffer) {
		int entityId = buffer.readInt();

		return new SoulParticleMessage(entityId);
	}

    public void handle(CustomPayloadEvent.Context context) {
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);
				if (entity != null) {
					for (int i = 0; i < 4; i++) {
						entity.level().addParticle(ParticleTypes.SCULK_SOUL, entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0.0F, 0.1F, 0.0F);
					}
				}
			});
		}
		context.setPacketHandled(true);
    }
}