package tnt.tarkovcraft.core.client.config;

import dev.toma.configuration.config.Configurable;
import org.joml.Vector2f;
import tnt.tarkovcraft.core.util.HorizontalAlignment;
import tnt.tarkovcraft.core.util.VerticalAlignment;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

public class ConfigurableOverlay {

    @Configurable("config.option.tarkovcraft_core.global.overlay.enabled")
    public boolean enabled;

    @Configurable("config.option.tarkovcraft_core.global.overlay.horizontalAlignment")
    public HorizontalAlignment horizontalAlignment;

    @Configurable("config.option.tarkovcraft_core.global.overlay.verticalAlignment")
    public VerticalAlignment verticalAlignment;

    @Configurable("config.option.tarkovcraft_core.global.overlay.x")
    public int x;

    @Configurable("config.option.tarkovcraft_core.global.overlay.y")
    public int y;

    public ConfigurableOverlay() {
        this.enabled = true;
        this.horizontalAlignment = HorizontalAlignment.LEFT;
        this.verticalAlignment = VerticalAlignment.TOP;
        this.x = 0;
        this.y = 0;
    }

    public ConfigurableOverlay(boolean enabled, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int x, int y) {
        this.enabled = enabled;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.x = x;
        this.y = y;
    }

    public Vector2f getPosition(float x1, float y1, float x2, float y2, float width, float height) {
        Vector2f pos = RenderUtils.getPosition(x1, y1, x2, y2, width, height, this.horizontalAlignment, this.verticalAlignment);
        return pos.add(this.x, this.y);
    }
}
