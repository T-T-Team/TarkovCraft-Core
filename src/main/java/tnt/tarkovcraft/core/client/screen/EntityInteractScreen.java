package tnt.tarkovcraft.core.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.client.screen.renderable.ShapeRenderable;
import tnt.tarkovcraft.core.client.screen.widget.ListWidget;
import tnt.tarkovcraft.core.common.data.duration.DurationUnit;
import tnt.tarkovcraft.core.common.interact.EntityInteractionData;
import tnt.tarkovcraft.core.network.message.C2S_RequestInteractionState;
import tnt.tarkovcraft.core.util.UserActionResult;
import tnt.tarkovcraft.core.util.helper.TextHelper;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

public final class EntityInteractScreen extends Screen {

    private static final Component TITLE = TextHelper.createScreenTitle(TarkovCraftCore.MOD_ID, "entity_interaction");

    private final LivingEntity entity;
    private final List<EntityInteraction.Type<?>> availableInteractions;
    private EntityInteractionData interactionData;
    private EntityInteraction.Context context;

    public EntityInteractScreen(LivingEntity entity, List<EntityInteraction.Type<?>> availableInteractions) {
        super(TITLE);
        this.entity = entity;
        this.availableInteractions = availableInteractions;
    }

    @Override
    protected void init() {
        this.interactionData = EntityInteractionData.getInteractionData(this.minecraft.player);
        this.context = new EntityInteraction.Context(this.minecraft.player, this.entity);
        this.addRenderableOnly(new ShapeRenderable(0, 0, this.width, this.height, ColorPalette.BG_TRANSPARENT_WEAK));

        int displayCount = Math.min(this.availableInteractions.size(), 7);
        int displayHeight = displayCount * 20;
        int colWidth = this.width / 6;
        int left = (this.width - colWidth) / 2;
        int top = (this.height - displayHeight) / 2;
        this.addRenderableWidget(new ListWidget<>(left, top, colWidth, displayHeight, this.availableInteractions, this::createInteractionButton));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int titleWidth = this.font.width(TITLE);
        graphics.text(this.font, TITLE, (this.width - titleWidth) / 2, 15, ColorPalette.WHITE);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        if (this.entity.isRemoved() || !this.entity.isAlive()) {
            this.minecraft.gui.setScreen(null);
        }
        double distance = this.minecraft.player.distanceToSqr(this.entity);
        if (distance > EntityInteraction.MAX_DISTANCE_SQR) {
            this.minecraft.gui.setScreen(null);
        }
    }

    private InteractionButton createInteractionButton(EntityInteraction.Type<?> interactionType, int index) {
        int buttonWidth = this.width / 6;
        int left = (this.width - buttonWidth) / 2;
        InteractionButton button = new InteractionButton(left, index * 20, buttonWidth, 20, interactionType);
        button.setOnInitiate(this::onInteractionStarted);
        button.setOnCancel(this::onInteractCancelled);
        button.setOnFinish(this::interactionCompleteCallback);
        UserActionResult<Void> evaluationResult = interactionType.canUseInteraction(this.context);
        button.active = evaluationResult.isSuccess();
        if (evaluationResult.isFailure()) {
            Component formattedMessage = evaluationResult.message().plainCopy().withStyle(ChatFormatting.RED);
            button.setTooltip(Tooltip.create(formattedMessage));
            button.setTooltipDelay(Duration.ofMillis(300));
        }

        return button;
    }

    private void onInteractionStarted(EntityInteraction.Type<?> interaction) {
        long initiationTime = this.minecraft.level.getGameTime();
        this.interactionData.startInteraction(interaction, this.context, initiationTime);
        ClientPacketDistributor.sendToServer(C2S_RequestInteractionState.start(interaction, this.entity, initiationTime));
    }

    private void onInteractCancelled(EntityInteraction.Type<?> interaction) {
        this.interactionData.cancelInteraction(this.context, EntityInteraction.InteractionResult.CANCELLED);
        ClientPacketDistributor.sendToServer(C2S_RequestInteractionState.cancel(interaction, this.entity));
    }

    private void interactionCompleteCallback(EntityInteraction.Type<?> interaction) {
        this.interactionData.finishInteraction(this.context);
        ClientPacketDistributor.sendToServer(C2S_RequestInteractionState.finish(interaction, this.entity));
        this.minecraft.gui.setScreen(null);
    }

    @Override
    public void removed() {
        if (this.interactionData.isAnyInteractionActive()) {
            EntityInteraction interaction = this.interactionData.getActiveInteraction();
            this.interactionData.cancelInteraction(this.context, EntityInteraction.InteractionResult.CANCELLED);
            ClientPacketDistributor.sendToServer(C2S_RequestInteractionState.cancel(interaction.type(), this.entity));
        }
    }

    private static final class InteractionButton extends AbstractButton {

        private final EntityInteraction.Type<?> interactionType;

        private InteractStateCallback onInitiate = _ -> {};
        private InteractStateCallback onCancel = _ -> {};
        private InteractStateCallback onFinish = _ -> {};
        private long pressStartTs = -1;

        public InteractionButton(int x, int y, int width, int height, EntityInteraction.Type<?> interactionType) {
            super(x, y, width, height, interactionType.displayName());
            this.interactionType = interactionType;
        }

        public void setOnInitiate(InteractStateCallback onInitiate) {
            this.onInitiate = onInitiate;
        }

        public void setOnCancel(InteractStateCallback onCancel) {
            this.onCancel = onCancel;
        }

        public void setOnFinish(InteractStateCallback onFinish) {
            this.onFinish = onFinish;
        }

        @Override
        public void onPress(InputWithModifiers input) {
            if (this.pressStartTs > 0) {
                this.pressStartTs = -1;
                this.onCancel.onStateChangedCallback(this.interactionType);
                return;
            }
            if (this.interactionType.duration() <= 0) {
                this.onFinish.onStateChangedCallback(this.interactionType);
                return;
            }
            this.pressStartTs = System.currentTimeMillis();
            this.onInitiate.onStateChangedCallback(this.interactionType);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            this.extractDefaultSprite(graphics);
            Font font = Minecraft.getInstance().font;
            Component content = this.getDisplayContent();
            int contentWidth = font.width(content);
            graphics.text(font, content, this.getX() + (this.width - contentWidth) / 2, this.getY() + (this.height - 8) / 2, this.active ? 0xFFFFFFFF : 0xFFAAAAAA);

            if (this.isPressed() && this.isFinished()) {
                this.onFinish.onStateChangedCallback(this.interactionType);
                this.pressStartTs = -1;
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
        }

        private Component getDisplayContent() {
            if (this.isPressed()) {
                if (this.isHovered) {
                    return CommonComponents.GUI_CANCEL;
                }
                int durationMs = this.interactionType.duration() * 50;
                long elapsed = System.currentTimeMillis() - this.pressStartTs;
                long remaining = durationMs - elapsed;
                int remainingTicks = (int) (remaining / 50L);
                double seconds = remainingTicks / 20.0D;
                return Component.literal(String.format(Locale.ROOT, "%.1f", seconds)).append(DurationUnit.SECONDS.getShortName());
            }
            return this.getMessage();
        }

        private boolean isPressed() {
            return this.pressStartTs > 0;
        }

        private boolean isFinished() {
            return this.pressStartTs + this.interactionType.duration() * 50L < System.currentTimeMillis();
        }

        @FunctionalInterface
        public interface InteractStateCallback {
            void onStateChangedCallback(EntityInteraction.Type<?> interaction);
        }
    }
}
