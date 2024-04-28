package baguchan.enchantwithmob.client.overlay;

import baguchan.enchantwithmob.EnchantConfig;
import baguchan.enchantwithmob.api.IEnchantCap;
import baguchan.enchantwithmob.capability.MobEnchantHandler;
import baguchan.enchantwithmob.mobenchant.MobEnchant;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

public class MobEnchantOverlay implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.options.getCameraType().isMirrored()) {
            if (EnchantConfig.CLIENT.showEnchantedMobHud.get() && mc.player != null) {
                if (mc.player instanceof IEnchantCap cap) {
                    if (cap.getEnchantCap().hasEnchant()) {
                        guiGraphics.drawString(mc.font, mc.player.getDisplayName(), (int) 20, (int) 50, 0xe0e0e0);

                        for (MobEnchantHandler mobEnchantHandler : cap.getEnchantCap().getMobEnchants()) {
                            MobEnchant mobEnchant = mobEnchantHandler.getMobEnchant();
                            int mobEnchantLevel = mobEnchantHandler.getEnchantLevel();

                            ChatFormatting[] textformatting = new ChatFormatting[]{ChatFormatting.AQUA};

                            Component s = mobEnchant.getFullname(mobEnchantLevel);

                            int xOffset = 20;
                            int yOffset = cap.getEnchantCap().getMobEnchants().indexOf(mobEnchantHandler) * 10 + 60;

                            guiGraphics.drawString(mc.font, s, (int) (xOffset), (int) yOffset, 0xe0e0e0);
                        }
                    }
                }
            }
        } else {
            if (EnchantConfig.CLIENT.showEnchantedMobHud.get() && mc.crosshairPickEntity != null) {
                if (mc.crosshairPickEntity instanceof IEnchantCap cap) {
                    if (cap.getEnchantCap().hasEnchant()) {
                        guiGraphics.drawString(mc.font, mc.crosshairPickEntity.getDisplayName(), (int) 20, (int) 50, 0xe0e0e0);

                        for (MobEnchantHandler mobEnchantHandler : cap.getEnchantCap().getMobEnchants()) {
                            MobEnchant mobEnchant = mobEnchantHandler.getMobEnchant();
                            int mobEnchantLevel = mobEnchantHandler.getEnchantLevel();

                            ChatFormatting[] textformatting = new ChatFormatting[]{ChatFormatting.AQUA};

                            Component s = mobEnchant.getFullname(mobEnchantLevel);

                            int xOffset = 20;
                            int yOffset = cap.getEnchantCap().getMobEnchants().indexOf(mobEnchantHandler) * 10 + 60;

                            guiGraphics.drawString(mc.font, s, (int) (xOffset), (int) yOffset, 0xe0e0e0);
                        }
                    }
                }
            }
        }
	}
}
