package tnt.tarkovcraft.core.client.screen;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.widget.ChatMessagesWidget;
import tnt.tarkovcraft.core.client.screen.widget.LabelButton;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.mail.MailList;
import tnt.tarkovcraft.core.common.mail.MailManager;
import tnt.tarkovcraft.core.common.mail.MailMessage;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.network.message.mail.C2S_MailSendMessage;
import tnt.tarkovcraft.core.util.LocalizationHelper;

import java.util.List;
import java.util.UUID;

public class MailListScreen extends OverlayScreen implements DataScreen {

    public static final Component TITLE = LocalizationHelper.createScreenTitle(TarkovCraftCore.MOD_ID, "mail");
    public static final Component ACTIVE_CHAT = LocalizationHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "mail", "active_chat");
    public static final Component SEND_MESSAGE_HINT = LocalizationHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "mail", "hint.send").withStyle(ChatFormatting.ITALIC).withColor(ColorPalette.TEXT_COLOR_DISABLED);
    public static final Component CANNOT_CHAT = LocalizationHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "mail", "hint.no_chat").withStyle(ChatFormatting.ITALIC);

    private MailManager userMailManager;
    private MailSource selectedChat;

    private int chatScrollIndex;
    private double chatMessageScroll;
    private EditBox messageBox;
    private ChatMessagesWidget messages;

    public MailListScreen(Screen parent) {
        super(TITLE, parent);
    }

    @Override
    public void onAttachmentDataReceived(Entity entity, AttachmentType<?> attachmentType, Synchronizable data) {
        if (!entity.getUUID().equals(Minecraft.getInstance().player.getUUID()))
            return;
        if (attachmentType == BaseDataAttachments.MAIL_MANAGER.get()) {
            this.init(this.minecraft, this.width, this.height);
        }
    }

    @Override
    protected void init() {
        super.init();
        Player player = Minecraft.getInstance().player;
        this.userMailManager = player.getData(BaseDataAttachments.MAIL_MANAGER);

        this.addRenderableWidget(new LabelButton(Button.builder(CommonComponents.GUI_BACK, t -> this.openParentScreen())
                .bounds(this.width - 55, 0, 50, 25)
        ));

        // TODO scrollable list widget
        List<MailList> chats = this.userMailManager.listChats().stream()
                .filter(MailList::hasMessages)
                .sorted()
                .toList();
        for (int i = this.chatScrollIndex; i < chats.size(); i++) {
            MailList chat = chats.get(i);
            int index = i - this.chatScrollIndex;
            this.addRenderableWidget(new ChatWidget(0, index * 22, this.width / 3, 20, chat, this.font));
        }

        if (this.selectedChat != null) {
            int left = this.width / 3 + 1;
            MailList chat = this.userMailManager.getChat(this.selectedChat);
            this.messages = this.addRenderableWidget(new ChatMessagesWidget(left, 26, this.width - left, this.height - 52, player.getUUID(), this.font, chat));
            this.messages.setScrollAmount(this.chatMessageScroll);
            this.messages.setScrollChangeListener((x, y) -> this.chatMessageScroll = y);
            if (this.selectedChat.isChatAllowed() && this.isOnline(this.selectedChat)) {
                this.messageBox = this.addRenderableWidget(new EditBox(this.font, left + 5, this.height - 20, this.width - left - 10, 15, CommonComponents.EMPTY));
                this.messageBox.setMaxLength(256);
                this.messageBox.setHint(SEND_MESSAGE_HINT);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderBlurredBackground();
        guiGraphics.fill(0, 0, this.width, this.height, ColorPalette.BG_TRANSPARENT_VERY_DARK);
        guiGraphics.vLine(this.width / 3, -1, this.height, 0xFFFFFFFF);

        int left = this.width / 3 + 1;
        guiGraphics.fill(left, 0, this.width, 25, 0xFF << 24);
        guiGraphics.hLine(left, this.width, 25, 0xFFFFFFFF);
        Component chatName = ACTIVE_CHAT;
        if (this.selectedChat != null) {
            guiGraphics.fill(left, this.height - 25, this.width, this.height, 0xFF << 24);
            guiGraphics.hLine(left, this.width, this.height - 26, 0xFFFFFFFF);
            chatName = this.selectedChat.getName();
            if (!this.selectedChat.isChatAllowed()) {
                guiGraphics.drawScrollingString(this.font, CANNOT_CHAT, left + 5, this.width - 5, this.height - 25 + (25 - this.font.lineHeight) / 2, ColorPalette.TEXT_COLOR_ERROR);
            }
        }
        guiGraphics.drawScrollingString(this.font, chatName, left + 5, this.width - 5, (25 - this.font.lineHeight) / 2, ColorPalette.TEXT_COLOR);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && this.getFocused().equals(this.messageBox) && this.canSendCurrentMessage()) {
            String messageContent = this.messageBox.getValue();
            MailMessage message = MailMessage.simpleChatMessage(MailSource.player(this.minecraft.player), messageContent);
            PacketDistributor.sendToServer(new C2S_MailSendMessage(this.selectedChat.getSourceId(), message));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void onChatSelected(MailList mailList) {
        if (this.selectedChat != null && this.selectedChat.equals(mailList.getSource())) {
            return;
        }
        this.selectedChat = mailList.getSource();
        this.init(this.minecraft, this.width, this.height);
    }

    private boolean isOnline(MailSource source) {
        UUID id = source.getSourceId();
        return this.minecraft.getConnection().getPlayerInfo(id) != null;
    }

    private boolean canSendCurrentMessage() {
        return this.selectedChat != null && this.messageBox.active && !this.messageBox.getValue().isBlank();
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
            if (this.chat.getSource().equals(MailListScreen.this.selectedChat) || this.isHoveredOrFocused()) {
                guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), ColorPalette.BG_HOVER_LIGHT);
            }
            int x = this.getX() + 2;
            int y = this.getY() + 2;
            if (this.chat.getSource().isSystemChat()) {
                guiGraphics.blit(RenderType::guiTextured, this.icon, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
            } else {
                PlayerFaceRenderer.draw(guiGraphics, this.icon, x, y, 16, false, false, -1);
            }
            guiGraphics.drawScrollingString(this.font, this.chat.getSource().getName(), x + 20, this.getRight() - 10, y, ColorPalette.TEXT_COLOR);
            int count = this.chat.getUnreadMessages();
            if (count > 0) {
                String text = "[" + count + "]";
                int textWidth = this.font.width(text);
                int textLeft = this.getRight() - textWidth - 4;
                int textY = y + 8;
                guiGraphics.fill(textLeft - 1, textY - 1, textLeft + textWidth, textY + 8, 0xFF00BB00);
                guiGraphics.drawString(this.font, text, textLeft, textY, 0xFF005500, false);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
