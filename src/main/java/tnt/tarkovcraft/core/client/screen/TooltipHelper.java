package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public interface TooltipHelper {

    void setForNextRenderPass(List<FormattedCharSequence> tooltip);

    void setForNextRenderPass(Component tooltip);

    List<FormattedCharSequence> split(Component tooltip);

    static TooltipHelper screen(Screen screen) {
        return new ScreenTooltipHelper(screen);
    }

    class ScreenTooltipHelper implements TooltipHelper {

        private final Screen screen;

        private ScreenTooltipHelper(Screen screen) {
            this.screen = screen;
        }

        @Override
        public List<FormattedCharSequence> split(Component tooltip) {
            return Tooltip.splitTooltip(this.screen.getMinecraft(), tooltip);
        }

        @Override
        public void setForNextRenderPass(List<FormattedCharSequence> tooltip) {
            this.screen.setTooltipForNextRenderPass(tooltip);
        }

        @Override
        public void setForNextRenderPass(Component tooltip) {
            this.screen.setTooltipForNextRenderPass(tooltip);
        }
    }
}
