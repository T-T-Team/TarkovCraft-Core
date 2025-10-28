package tnt.tarkovcraft.core.client.decoration;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import tnt.tarkovcraft.core.common.item.Currency;

public class CurrencyItemDecorator implements IItemDecorator {

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        Currency currency = Currency.getFromItemStack(stack);
        if (currency == null || currency.amount() <= 1)
            return false;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        String text = String.valueOf(currency.amount());
        poseStack.translate(xOffset + 16, yOffset + 12, 0);
        poseStack.scale(0.45F, 0.45F, 0.45F);
        guiGraphics.drawString(font, text, -font.width(text), 0, currency.type().value().displayColor());
        poseStack.popPose();
        return false;
    }
}
