package tnt.tarkovcraft.core.client.decoration;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import org.joml.Matrix3x2fStack;
import tnt.tarkovcraft.core.common.item.Currency;

public class CurrencyItemDecorator implements IItemDecorator {

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        Currency currency = Currency.getFromItemStack(stack);
        if (currency == null || currency.amount() <= 1)
            return false;
        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        String text = String.valueOf(currency.amount());
        pose.translate(xOffset + 16, yOffset + 12);
        pose.scale(0.45F);
        guiGraphics.drawString(font, text, -font.width(text), 0, currency.type().value().displayColor());
        pose.popMatrix();
        return false;
    }
}
