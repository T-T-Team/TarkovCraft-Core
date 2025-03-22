package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import tnt.tarkovcraft.core.client.screen.ColorPalette;
import tnt.tarkovcraft.core.client.screen.listener.ScrollChangeListener;
import tnt.tarkovcraft.core.common.mail.MailList;
import tnt.tarkovcraft.core.common.mail.MailMessage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatMessagesWidget extends AbstractWidget {

    private final UUID me;
    private final Font font;
    private final MailList chat;
    private final List<Message> compiledMessages = new ArrayList<>();

    private int backgroundColor;
    private ScrollChangeListener scrollChangeListener;
    private double scrollAmount;

    public ChatMessagesWidget(int x, int y, int width, int height, UUID me, Font font, MailList chat) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.me = me;
        this.font = font;
        this.chat = chat;

        List<MailMessage> mailMessages = this.chat.listMessages();
        for (int i = mailMessages.size() - 1; i >= 0; i--) {
            MailMessage message = mailMessages.get(i);
            Component text = message.getContent();
            List<Message> formattedText = this.font.split(text, this.width - 4).stream()
                    .map(seq -> new Message(message.getMessageReceptionTime(), message.getSender(), seq))
                    .toList();
            this.compiledMessages.addAll(formattedText);
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setScrollAmount(double scrollAmount) {
        this.scrollAmount = scrollAmount;
    }

    public void setScrollChangeListener(ScrollChangeListener scrollChangeListener) {
        this.scrollChangeListener = scrollChangeListener;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int index = (int) (this.scrollAmount / 10.0);
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
        guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.backgroundColor);
        float y = this.getBottom() - 10 + (float) (this.scrollAmount - index * 10.0F);
        for (int i = index; i < this.compiledMessages.size(); i++) {
            Message message = this.compiledMessages.get(i);
            boolean isMine = this.me.equals(message.owner);
            if (isMine) {
                int width = this.font.width(message.text());
                guiGraphics.drawString(this.font, message.text(), this.getRight() - 2 - width, y, ColorPalette.TEXT_COLOR, false);
            } else {
                guiGraphics.drawString(this.font, message.text(), this.getX() + 2, y, ColorPalette.TEXT_COLOR_DISABLED, false);
            }
            if ((y -= 10) < this.getY()) {
                break;
            }
        }
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        List<MailMessage> messages = this.chat.listMessages();
        if (messages.isEmpty())
            return false;
        int messageCount = messages.size() - 1;
        this.scrollAmount = Math.clamp(this.scrollAmount + scrollY, 0.0, messageCount * 10);
        if (this.scrollChangeListener != null) {
            this.scrollChangeListener.onScrollChange(scrollX, this.scrollAmount);
        }
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    private record Message(ZonedDateTime ts, UUID owner, FormattedCharSequence text) {}
}
