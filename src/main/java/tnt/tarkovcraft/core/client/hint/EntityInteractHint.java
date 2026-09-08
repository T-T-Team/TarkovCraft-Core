package tnt.tarkovcraft.core.client.hint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHintRenderer;
import tnt.tarkovcraft.core.api.client.hint.TextHint;

import java.util.List;

public final class EntityInteractHint extends AbstractOnScreenHint implements TextHint {

    private static final Component ACTION_LABEL = Component.translatable("label.tarkovcraft_core.action.interact");

    @Override
    public void tick(Minecraft client) {
        this.setVisible(false);
        Entity entity = client.crosshairPickEntity;
        if (!(entity instanceof LivingEntity livingEntity))
            return;
        EntityInteraction.Context context = new EntityInteraction.Context(client.player, livingEntity);
        List<EntityInteraction.Type<?>> available = EntityInteraction.Type.listAvailableInteractions(context);
        if (available.isEmpty())
            return;
        this.setVisible(true);
    }

    @Override
    public OnScreenHintRenderer<?> createRenderer() {
        throw new UnsupportedOperationException("EntityInteractHint cannot be rendered directly.");
    }

    @Override
    public Component text() {
        Minecraft client = Minecraft.getInstance();
        Options options = client.options;
        Component crouchActionName = options.keyShift.getDisplayName();
        Component interactActionName = options.keyUse.getTranslatedKeyMessage();
        return Component.literal("[").append(crouchActionName).append(" + ").append(interactActionName).append("] ").append(ACTION_LABEL);
    }
}
