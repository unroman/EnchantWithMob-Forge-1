package baguchi.enchantwithmob.event;

import baguchi.enchantwithmob.EnchantWithMob;
import baguchi.enchantwithmob.mobenchant.MobEnchant;
import baguchi.enchantwithmob.registry.MobEnchants;
import baguchi.enchantwithmob.registry.ModItems;
import baguchi.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import javax.annotation.Nullable;
import java.util.List;

@EventBusSubscriber(modid = EnchantWithMob.MODID)
public class TradeEvent {
    @SubscribeEvent
    public static void wanderTradeEvent(WandererTradesEvent event) {
		List<VillagerTrades.ItemListing> trades = event.getRareTrades();
        trades.add(new MobEnchantedBookForEmeraldsTrade(15));
    }

	static class MobEnchantedBookForEmeraldsTrade implements VillagerTrades.ItemListing {
		private final int xpValue;

		public MobEnchantedBookForEmeraldsTrade(int xpValueIn) {
			this.xpValue = xpValueIn;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource rand) {
            List<MobEnchant> list = MobEnchants.getRegistry().stream().filter(MobEnchant::isDiscoverable).toList();
			MobEnchant enchantment = list.get(rand.nextInt(list.size()));
			int i = Mth.nextInt(rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
			ItemStack itemstack = new ItemStack(ModItems.MOB_ENCHANT_BOOK.get());
            MobEnchantUtils.enchant(enchantment, itemstack, i);
			int j = 2 + rand.nextInt(5 + i * 10) + 3 * i;

			if (enchantment.isTresureEnchant()) {
				j += 4;
			}

			if (j > 64) {
				j = 64;
			}

            return new MerchantOffer(new ItemCost(Items.EMERALD, j), itemstack, 12, this.xpValue, 0.2F);
		}
    }
}
