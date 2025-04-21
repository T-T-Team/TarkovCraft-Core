package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.ClientContextKeys;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.navigation.NavigationEntry;
import tnt.tarkovcraft.core.client.screen.renderable.AbstractTextRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.NotificationScreen;
import tnt.tarkovcraft.core.client.screen.renderable.VerticalLineRenderable;
import tnt.tarkovcraft.core.client.screen.widget.HorizontalNavigationMenu;
import tnt.tarkovcraft.core.client.screen.widget.LabelButton;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;
import tnt.tarkovcraft.core.util.context.ContextKeys;
import tnt.tarkovcraft.core.util.helper.TextHelper;

import java.util.Optional;
import java.util.UUID;

public abstract class CharacterSubScreen extends NotificationScreen implements DataScreen {

    protected final UUID characterProfileId;
    protected final NavigationEntry selectedPage;
    protected boolean isMyProfile;

    public CharacterSubScreen(UUID characterProfileId, NavigationEntry selectedPage) {
        super(selectedPage.label());
        this.characterProfileId = characterProfileId;
        this.selectedPage = selectedPage;
    }

    @Override
    public void onAttachmentDataReceived(Entity entity, AttachmentType<?> attachmentType, Synchronizable<?> data) {
        if (entity.getUUID().equals(this.characterProfileId)) {
            this.init(this.minecraft, this.width, this.height); // reload data if applicable
        }
    }

    protected Optional<Player> getPlayer() {
        return Optional.ofNullable(this.minecraft.level.getPlayerByUUID(this.characterProfileId));
    }

    @Override
    protected void init() {
        super.init();
        this.isMyProfile = this.minecraft.player.getUUID().equals(this.characterProfileId);
        this.addHeader();
    }

    protected void addHeader() {
        PlayerInfo info = this.minecraft.getConnection().getPlayerInfo(this.characterProfileId);
        String name = info != null ? info.getProfile().getName() : "???";
        Component title = Component.translatable("label.tarkovcraft_core.profile", name);
        int titleWidth = this.font.width(title);
        this.addRenderableOnly(new AbstractTextRenderable.Component(5, 8, titleWidth, 10, ColorPalette.TEXT_COLOR, false, this.font, title));
        this.addRenderableOnly(new VerticalLineRenderable(10 + titleWidth, 5, 19, ARGB.opaque(ColorPalette.TEXT_COLOR)));
        this.addRenderableOnly(new VerticalLineRenderable(this.width - 60, 5, 19, ARGB.opaque(ColorPalette.TEXT_COLOR)));

        Context context = ContextImpl.of(
                ClientContextKeys.PARENT_SCREEN, this,
                ContextKeys.UUID, this.characterProfileId
        );
        this.addRenderableWidget(new HorizontalNavigationMenu<>(15 + titleWidth, 0, this.width - 80 - titleWidth, 25, context, CoreNavigators.CHARACTER_NAVIGATION_PROVIDER, this::buildNavItem));

        Component messageTitle = TextHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "character", "mail");
        LabelButton messages = addRenderableWidget(new LabelButton(Button.builder(messageTitle, b -> this.minecraft.setScreen(new MailListScreen(this)))
                .bounds(this.width - 55, 5, 50, 15)
        ));
        messages.setBackgroundHoverColor(0);
    }

    protected AbstractWidget buildNavItem(NavigationEntry entry, Context context) {
        Component title = entry.label();
        int itemWidth = this.font.width(title);
        LabelButton button = new LabelButton(
                Button.builder(title, b -> this.minecraft.setScreen(entry.getScreen(context)))
                        .size(itemWidth + 4, 15)
                        .pos(0, 5)
        );
        button.active = entry != this.selectedPage;
        button.setBackgroundHoverColor(0);
        button.setColorSelected(ColorPalette.WHITE);
        button.setColorDisabled(ColorPalette.YELLOW);
        button.setColor(ColorPalette.TEXT_COLOR);
        return button;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, ColorPalette.BG_TRANSPARENT_NORMAL);
        guiGraphics.fill(0, 0, this.width, 25, ColorPalette.BG_TRANSPARENT_WEAK);
    }
}
