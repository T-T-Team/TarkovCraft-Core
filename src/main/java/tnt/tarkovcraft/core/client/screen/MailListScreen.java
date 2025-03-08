package tnt.tarkovcraft.core.client.screen;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.mail.MailList;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.common.mail.UserMailManager;
import tnt.tarkovcraft.core.util.LocalizationHelper;

import java.util.List;
import java.util.UUID;

public class MailListScreen extends OverlayScreen {

    public static final Component TITLE = LocalizationHelper.createScreenTitle(TarkovCraftCore.MOD_ID, "mail");

    private UserMailManager userMailManager;
    private MailSource selectedChat;

    private int chatScrollIndex;

    public MailListScreen(Screen parent) {
        super(TITLE, parent);
    }

    @Override
    protected void init() {
        Player player = Minecraft.getInstance().player;
        this.userMailManager = player.getData(BaseDataAttachments.MAIL_LIST);

        // TODO scrollable list widget
        List<MailList> chats = this.userMailManager.listChats().stream()
                .filter(MailList::hasMessages)
                .sorted()
                .toList();
        for (int i = this.chatScrollIndex; i < chats.size(); i++) {
            MailList chat = chats.get(i);
            ChatWidget widget = this.addRenderableWidget(new ChatWidget(0, 0, this.width / 3, 20, chat, this.font));
        }

        if (this.selectedChat != null) {

        }
    }

    protected void onChatSelected(MailList mailList) {
        if (this.selectedChat != null && this.selectedChat.equals(mailList.getSource())) {
            return;
        }
        this.selectedChat = mailList.getSource();
        this.init(this.minecraft, this.width, this.height);
    }

    public final class ChatWidget extends AbstractWidget {

        private final MailList chat;
        private final Font font;
        private final ResourceLocation icon;

        public ChatWidget(int x, int y, int width, int height, MailList chat, Font font) {
            super(x, y, width, height, chat.getSource().getName());
            this.chat = chat;
            this.font = font;
            if (this.chat.getSource().isSystemChat()) {
                this.icon = this.chat.getSource().getIcon();
            } else {
                UUID sourcePlayer = this.chat.getSource().getSourceId();
                GameProfile profile = new GameProfile(sourcePlayer, "");
                PlayerSkin skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile);
                this.icon = skin.texture();
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {
            MailListScreen.this.onChatSelected(this.chat);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (this.chat.getSource().equals(MailListScreen.this.selectedChat)) {
                guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), BaseColorPalette.BG_HOVER_LIGHT);
            }
            int x = this.getX() + 2;
            int y = this.getY() + 2;
            if (this.chat.getSource().isSystemChat()) {
                guiGraphics.blit(RenderType::guiTextured, this.icon, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
            } else {
                PlayerFaceRenderer.draw(guiGraphics, this.icon, x, y, 16, false, false, -1);
            }
            guiGraphics.drawScrollingString(this.font, this.chat.getSource().getName(), x + 20, this.getRight() - 10, y, 0xFFFFFFFF);
            if (this.chat.getUnreadMessages() > 0) {
                String text = "[" + this.chat.getUnreadMessages() + "]";
                int textWidth = this.font.width(text);
                int textLeft = this.getRight() - textWidth - 4;
                int textY = y + 8;
                guiGraphics.fill(textLeft - 1, textY - 1, textLeft + textWidth + 1, textY + 11, 0xFF00FF00);
                guiGraphics.drawString(this.font, text, textLeft, textY, 0xFF000000);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
