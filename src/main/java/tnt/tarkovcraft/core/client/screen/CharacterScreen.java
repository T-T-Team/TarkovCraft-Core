package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.renderable.NotificationScreen;
import tnt.tarkovcraft.core.client.screen.widget.LabelButton;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.helper.LocalizationHelper;

import java.util.UUID;

public final class CharacterScreen extends NotificationScreen implements DataScreen {

    public static final Component TITLE = LocalizationHelper.createScreenTitle(TarkovCraftCore.MOD_ID, "character");

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
    public void onAttachmentDataReceived(Entity entity, AttachmentType<?> attachmentType, Synchronizable<?> data) {
        if (entity.getUUID().equals(this.characterProfileId)) {
            this.init(this.minecraft, this.width, this.height); // reload data if applicable
        }
    }

    @Override
    protected void init() {
        super.init();
        this.isMyProfile = this.minecraft.player.getUUID().equals(this.characterProfileId);

        Component messageTitle = LocalizationHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "character", "mail");
        LabelButton messages = addRenderableWidget(new LabelButton(Button.builder(messageTitle, b -> this.minecraft.setScreen(new MailListScreen(this)))
                .bounds(this.width - 55, 5, 50, 15)
        ));

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, ColorPalette.BG_TRANSPARENT_NORMAL);
        guiGraphics.fill(0, 0, this.width, 25, ColorPalette.BG_TRANSPARENT_WEAK);
    }
}
