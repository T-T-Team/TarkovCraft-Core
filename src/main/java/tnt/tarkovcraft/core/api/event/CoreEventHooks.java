package tnt.tarkovcraft.core.api.event;

import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.util.UserActionResult;

public final class CoreEventHooks {

    public static UserActionResult<Void> checkInteractionAvailability(EntityInteraction.Type<?> type, EntityInteraction.Context context, UserActionResult<Void> baseCheckResult) {
        EntityInteractionEvent.OnAvailabilityCheck event = NeoForge.EVENT_BUS.post(new EntityInteractionEvent.OnAvailabilityCheck(type, context, baseCheckResult));
        return event.getResult();
    }

    public static void onInteractionFinished(EntityInteraction interaction, EntityInteraction.Context context, EntityInteraction.InteractionResult interactionResult) {
        NeoForge.EVENT_BUS.post(new EntityInteractionEvent.OnFinished(interaction.type(), context, interaction, interactionResult));
    }
}
