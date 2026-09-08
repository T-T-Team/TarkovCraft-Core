package tnt.tarkovcraft.core.api.event;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.api.StaminaComponent;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.weight.WeightProvider;
import tnt.tarkovcraft.core.util.UserActionResult;

import java.util.Stack;
import java.util.function.BiConsumer;

public final class CoreEventHooks {

    public static void onWeightUpdate(LivingEntity entity, int originalWeight, int newWeight, float overweightFactor) {
        NeoForge.EVENT_BUS.post(new EntityWeightUpdateEvent(entity, originalWeight, newWeight, overweightFactor));
    }

    public static void onWeightProviderRegistration(BiConsumer<Identifier, WeightProvider> handler) {
        ModLoader.postEvent(new RegisterWeightProvidersEvent(handler));
    }

    public static void onAttributeConstructing(EntityAttributeData holder, Attribute attribute, AttributeInstance instance) {
        NeoForge.EVENT_BUS.post(new EntityAttributeEvent.AttributeInstanceConstructing(holder, attribute, instance));
    }

    public static void onLivingApplyDamage(LivingEntity entity, Stack<DamageContainer> damageContainers) {
        NeoForge.EVENT_BUS.post(new LivingDamageApplyEvent(entity, damageContainers.peek()));
    }

    public static boolean canEntitySprint(MovementStaminaComponent component, LivingEntity entity) {
        StaminaEvent.CanSprint event = NeoForge.EVENT_BUS.post(new StaminaEvent.CanSprint(component, entity));
        Boolean eventResult = event.canSprint();
        return eventResult != null ? eventResult : true;
    }

    public static void onEntitySprint(MovementStaminaComponent component, LivingEntity entity) {
        NeoForge.EVENT_BUS.post(new StaminaEvent.AfterSprint(component, entity));
    }

    public static boolean canEntityJump(MovementStaminaComponent component, LivingEntity entity) {
        StaminaEvent.CanJump event = NeoForge.EVENT_BUS.post(new StaminaEvent.CanJump(component, entity));
        Boolean eventResult = event.canJump();
        return eventResult != null ? eventResult : true;
    }

    public static void onEntityJump(MovementStaminaComponent component, LivingEntity entity) {
        NeoForge.EVENT_BUS.post(new StaminaEvent.AfterJump(component, entity));
    }

    public static float consumeStamina(StaminaComponent component, LivingEntity entity, float consumption) {
        StaminaEvent.Consuming event = NeoForge.EVENT_BUS.post(new StaminaEvent.Consuming(component, entity, consumption));
        return Math.max(event.getConsumeAmount(), 0.0F);
    }

    public static float recoverStamina(StaminaComponent component, LivingEntity entity, float recovery) {
        StaminaEvent.Recovering event = NeoForge.EVENT_BUS.post(new StaminaEvent.Recovering(component, entity, recovery));
        return Math.max(event.getRecoverAmount(), 0.0F);
    }

    public static UserActionResult<Void> checkInteractionAvailability(EntityInteraction.Type<?> type, EntityInteraction.Context context, UserActionResult<Void> baseCheckResult) {
        EntityInteractionEvent.OnAvailabilityCheck event = NeoForge.EVENT_BUS.post(new EntityInteractionEvent.OnAvailabilityCheck(type, context, baseCheckResult));
        return event.getResult();
    }

    public static void onInteractionStarted(EntityInteraction interaction, EntityInteraction.Context context) {
        NeoForge.EVENT_BUS.post(new EntityInteractionEvent.OnStarted(interaction.type(), context, interaction));
    }

    public static void onInteractionFinished(EntityInteraction interaction, EntityInteraction.Context context, EntityInteraction.InteractionResult interactionResult) {
        NeoForge.EVENT_BUS.post(new EntityInteractionEvent.OnFinished(interaction.type(), context, interaction, interactionResult));
    }
}
