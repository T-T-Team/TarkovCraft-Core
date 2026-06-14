package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.api.client.SynchronizableScreen;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.navigation.NavigationEntry;
import tnt.tarkovcraft.core.client.screen.renderable.LabelRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.NotificationScreen;
import tnt.tarkovcraft.core.client.screen.renderable.VerticalLineRenderable;
import tnt.tarkovcraft.core.client.screen.widget.HorizontalNavigationMenu;
import tnt.tarkovcraft.core.client.screen.widget.LabelButton;

import java.util.Optional;
import java.util.UUID;

public abstract class CharacterSubScreen extends NotificationScreen implements SynchronizableScreen {

    protected final UUID characterProfileId;
    protected final NavigationEntry selectedPage;
    protected boolean isMyProfile;

    public CharacterSubScreen(UUID characterProfileId, NavigationEntry selectedPage) {
        super(selectedPage.label());
        this.characterProfileId = characterProfileId;
        this.selectedPage = selectedPage;
    }

    protected Optional<Player> getPlayer() {
        return Optional.ofNullable(this.minecraft.level.getPlayerByUUID(this.characterProfileId));
    }

    @Override
    protected void init() {
        this.isMyProfile = this.minecraft.player.getUUID().equals(this.characterProfileId);
        this.addHeader();
    }

    protected void addHeader() {
        PlayerInfo info = this.minecraft.getConnection().getPlayerInfo(this.characterProfileId);
        String name = info != null ? info.getProfile().getName() : "???";
        Component title = Component.translatable("label.tarkovcraft_core.profile", name);
        int titleWidth = this.font.width(title);
        LabelRenderable titleLabel = this.addRenderableOnly(LabelRenderable.fromComponent(5, 8, titleWidth, 10, this.font, title));
        titleLabel.setScrolling(false);
        this.addRenderableOnly(new VerticalLineRenderable(10 + titleWidth, 5, 19, ColorPalette.TEXT_COLOR));

        this.addRenderableWidget(new HorizontalNavigationMenu<>(15 + titleWidth, 0, this.width - 30 - titleWidth, 25, this, this.characterProfileId, CoreNavigators.CHARACTER_NAVIGATION_PROVIDER, (entry, parent, userId) -> buildNavItem(entry)));
    }

    protected AbstractWidget buildNavItem(NavigationEntry entry) {
        Component title = entry.label();
        int itemWidth = this.font.width(title);
        LabelButton button = new LabelButton(
                Button.builder(title, b -> this.minecraft.setScreen(entry.getScreen(this, this.characterProfileId)))
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
        this.renderBlurredBackground(partialTick);
        guiGraphics.fill(0, 0, this.width, this.height, ColorPalette.BG_TRANSPARENT_NORMAL);
        guiGraphics.fill(0, 0, this.width, 25, ColorPalette.BG_TRANSPARENT_WEAK);
    }
}
