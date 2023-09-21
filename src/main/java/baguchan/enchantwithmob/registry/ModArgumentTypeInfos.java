package baguchan.enchantwithmob.registry;

import baguchan.enchantwithmob.command.MobEnchantArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;

public class ModArgumentTypeInfos {

	public static void init() {
		ArgumentTypes.register("mob_enchant", MobEnchantArgument.class, new EmptyArgumentSerializer<>(MobEnchantArgument::mobEnchantment));

	}
}
