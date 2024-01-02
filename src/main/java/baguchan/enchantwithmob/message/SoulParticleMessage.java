package baguchan.enchantwithmob.message;

import baguchan.enchantwithmob.EnchantWithMob;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SoulParticleMessage implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(EnchantWithMob.MODID, "soul_particle");

	private int entityId;

	public SoulParticleMessage(Entity entity) {
		this.entityId = entity.getId();
	}

	public SoulParticleMessage(int id) {
		this.entityId = id;
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.entityId);
	}

	public SoulParticleMessage(FriendlyByteBuf buffer) {
		this(buffer.readInt());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static boolean handle(SoulParticleMessage message, PlayPayloadContext context) {
		context.workHandler().execute(() -> {
			Entity entity = Minecraft.getInstance().level.getEntity(message.entityId);
			if (entity != null) {
				for (int i = 0; i < 4; i++) {
					entity.level().addParticle(ParticleTypes.SCULK_SOUL, entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0.0F, 0.1F, 0.0F);
				}
			}
		});
		return true;
    }
}