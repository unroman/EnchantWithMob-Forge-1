package baguchan.enchantwithmob.mixin;

import baguchan.enchantwithmob.entity.EnchanterEntity;
import baguchan.enchantwithmob.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WoodlandMansionPieces.WoodlandMansionPiece.class)
public class WoodlandMansionPieceMixin {
	@Inject(method = "handleDataMarker", at = @At("HEAD"), cancellable = true)
	protected void handleDataMarker(String p_73921_, BlockPos p_73922_, ServerLevelAccessor p_73923_, Random p_73924_, BoundingBox p_73925_, CallbackInfo ci) {
		if (p_73921_.equals("Enchanter")) {
			EnchanterEntity entity = ModEntities.ENCHANTER.get().create(p_73923_.getLevel());
			entity.setPersistenceRequired();
			entity.moveTo(p_73922_, 0.0F, 0.0F);
			entity.finalizeSpawn(p_73923_, p_73923_.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.STRUCTURE, (SpawnGroupData) null, (CompoundTag) null);
			p_73923_.addFreshEntityWithPassengers(entity);
			p_73923_.setBlock(p_73922_, Blocks.AIR.defaultBlockState(), 2);
			ci.cancel();
		}
	}
}
