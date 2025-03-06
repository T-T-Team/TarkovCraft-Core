package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class CharacterScreen extends Screen implements DataScreen {

    public static final Component TITLE = Component.translatable("screen.tarkovcraft_core.character.title");

    private final UUID characterProfileId;
    private boolean isMyProfile;

    public CharacterScreen(UUID characterProfileId) {
        super(TITLE);
        this.characterProfileId = characterProfileId;
    }

    public CharacterScreen() {
        this(Minecraft.getInstance().player.getUUID());
    }

    @Override
    public void onPlayerProfileDataReceived(Player player) {
        if (player.getUUID().equals(this.characterProfileId))
            this.init(this.minecraft, this.width, this.height); // reload data if applicable
    }

    @Override
    protected void init() {
        this.isMyProfile = this.minecraft.player.getUUID().equals(this.characterProfileId);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // BG
        guiGraphics.fill(0, 0, this.width, this.height, BaseColorPalette.BG_TRANSPARENT_NORMAL);

        // Top
        guiGraphics.fill(0, 0, this.width, 25, BaseColorPalette.BG_TRANSPARENT_WEAK); // will be combined with the default BG, making it darker
    }
}
