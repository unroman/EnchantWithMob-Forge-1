package baguchan.enchantwithmob.capability;

import baguchan.enchantwithmob.EnchantWithMob;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemMobEnchantCapability implements ICapabilityProvider, INBTSerializable<CompoundTag> {
	private boolean hasEnchant;

	public boolean hasEnchant() {
		return hasEnchant;
	}

	public void setHasEnchant(boolean hasEnchant) {
		this.hasEnchant = hasEnchant;
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		return capability == EnchantWithMob.ITEM_MOB_ENCHANT_CAP ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
	}

	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();

		nbt.putBoolean("HasEnchant", hasEnchant);


		return nbt;
	}

	public void deserializeNBT(CompoundTag nbt) {
		hasEnchant = nbt.getBoolean("HasEnchant");
	}
}