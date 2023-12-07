package baguchan.enchantwithmob.capability;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class ItemMobEnchantCapability implements INBTSerializable<CompoundTag> {
	private boolean hasEnchant;

	public boolean hasEnchant() {
		return hasEnchant;
	}

	public void setHasEnchant(boolean hasEnchant) {
		this.hasEnchant = hasEnchant;
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