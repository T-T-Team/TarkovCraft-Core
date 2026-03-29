package tnt.tarkovcraft.core.common.weight;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemInstance;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.event.EntityWeightUpdateEvent;
import tnt.tarkovcraft.core.api.event.RegisterWeightProvidersEvent;
import tnt.tarkovcraft.core.common.attribute.AttributeSystem;
import tnt.tarkovcraft.core.common.config.WeightConfig;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreItemDataComponents;
import tnt.tarkovcraft.core.common.weight.provider.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class WeightSystem {

    public static final WeightSystem INSTANCE = new WeightSystem();
    public static final DecimalFormat FORMAT = new DecimalFormat("0.###");
    public static final Marker MARKER = MarkerManager.getMarker("WeightSystem");
    public static final Identifier OVERWEIGHT_ATTRIBUTE_MODIFIER = TarkovCraftCore.createIdentifier("overweight");
    public static final BiFunction<Integer, Style, Style> BASE_LABEL_STYLE = (weight, style) -> style.withColor(ChatFormatting.GRAY);
    public static final BiFunction<Integer, Style, Style> BASE_VALUE_STYLE = (weight, style) -> style.withColor(ChatFormatting.YELLOW);
    public static final BiFunction<Integer, Style, Style> NO_STYLE = (weight, style) -> style;

    private final Map<Identifier, WeightProvider> providerMap = new HashMap<>();
    private final Multimap<WeightProvider.WeightSource, WeightProvider> typeProviderMap = ArrayListMultimap.create();

    public static boolean isEnabled() {
        return TarkovCraftCore.getConfig().weightConfig.enableWeightSystem;
    }

    public static boolean isContainerWeightEnabled() {
        return TarkovCraftCore.getConfig().weightConfig.calculateContainerWeight;
    }

    public static int getItemWeight(ItemInstance instance) {
        return INSTANCE.getWeight(instance);
    }

    public static int getWeight(LivingEntity entity) {
        return entity.hasData(CoreDataAttachments.WEIGHT) ? entity.getData(CoreDataAttachments.WEIGHT) : 0;
    }

    public static boolean isOverweight(LivingEntity entity) {
        int weight = getWeight(entity);
        int limit = AttributeSystem.getIntValue(entity, CoreAttributes.WEIGHT_LIMIT, 0);
        return limit > 0 && weight > limit;
    }

    public static float getOverweightEffectFactor(LivingEntity entity) {
        int weight = getWeight(entity);
        int limit = AttributeSystem.getIntValue(entity, CoreAttributes.WEIGHT_LIMIT, 0);
        if (limit <= 0 || weight <= limit) {
            return 0.0F;
        }
        float factor = (weight - limit) / (float) limit;
        float attributeFactor = AttributeSystem.getFloatValue(entity, CoreAttributes.WEIGHT_EFFECT_FACTOR, 1.0F);
        return factor * attributeFactor;
    }

    public static void applyWeightEffects(LivingEntity entity) {
        if (!isEnabled())
            return;
        int weight = INSTANCE.calculateWeight(entity);
        int originalWeight = getWeight(entity);
        entity.setData(CoreDataAttachments.WEIGHT, weight);
        float factor = getOverweightEffectFactor(entity);
        WeightConfig config = TarkovCraftCore.getConfig().weightConfig;
        updateVanillaAttributeModifier(entity, Attributes.MOVEMENT_SPEED, config.overweightSpeedReduction, factor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        updateVanillaAttributeModifier(entity, Attributes.JUMP_STRENGTH, config.overweightJumpReduction, factor,  AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        updateVanillaAttributeModifier(entity, Attributes.SAFE_FALL_DISTANCE, config.overweightSafeFallDistanceReduction, factor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        updateVanillaAttributeModifier(entity, Attributes.STEP_HEIGHT, config.overweightStepHeightReduction, factor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        NeoForge.EVENT_BUS.post(new EntityWeightUpdateEvent(entity, originalWeight, weight, factor));
    }

    public static Component getWeightDisplay(int weight, BiFunction<Integer, Style, Style> baseStyleApplicator, BiFunction<Integer, Style, Style> weightStyleApplicator) {
        Component weightLabel = getWeightValueDisplay(weight, weightStyleApplicator);
        return Component.translatable("label.tarkovcraft_core.weight", weightLabel).withStyle(style -> baseStyleApplicator.apply(weight, style));
    }

    public static Component getWeightValueDisplay(int weight, BiFunction<Integer, Style, Style> weightStyleApplicator) {
        String unitSuffix = weight < 1000 ? "g" : "kg";
        float unit = weight < 1000 ? 1.0F : 1000.0F;
        Component weightComponent = Component.literal(FORMAT.format(weight / unit)).withStyle(style -> weightStyleApplicator.apply(weight, style));
        return Component.translatable("label.tarkovcraft_core.weight.unit." + unitSuffix, weightComponent).withStyle(style -> weightStyleApplicator.apply(weight, style));
    }

    public static Component getWeightDisplay(int weight) {
        return getWeightDisplay(weight, BASE_LABEL_STYLE, BASE_VALUE_STYLE);
    }

    public static void updateVanillaAttributeModifier(LivingEntity entity, Holder<Attribute> attribute, double amount, float factor, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance.hasModifier(OVERWEIGHT_ATTRIBUTE_MODIFIER)) {
            instance.removeModifier(OVERWEIGHT_ATTRIBUTE_MODIFIER);
        }
        if (factor > 0.0F) {
            AttributeModifier modifier = new AttributeModifier(OVERWEIGHT_ATTRIBUTE_MODIFIER, amount * factor, operation);
            instance.addTransientModifier(modifier);
        }
    }

    public int getWeight(ItemInstance instance) {
        if (!isEnabled())
            return 0;
        int count = instance.count();
        int baseWeight = instance.getOrDefault(CoreItemDataComponents.WEIGHT, 0);
        int weight = count * baseWeight;
        if (isContainerWeightEnabled()) {
            Collection<WeightProvider> additionalWeightProviders = this.typeProviderMap.get(WeightProvider.WeightSource.ITEM);
            WeightContext itemCtx = WeightContext.itemStack(baseWeight, instance, this::getWeight);
            for (WeightProvider provider : additionalWeightProviders) {
                weight += (count * provider.getWeight(itemCtx));
            }
        }
        return weight;
    }

    public int calculateWeight(LivingEntity entity) {
        if (!isEnabled())
            return 0;
        int weight = 0;
        Collection<WeightProvider> additionalInventoryProviders = this.typeProviderMap.get(WeightProvider.WeightSource.ENTITY);
        WeightContext entityCtx = WeightContext.entity(entity, this::getWeight);
        for (WeightProvider provider : additionalInventoryProviders) {
            weight += provider.getWeight(entityCtx);
        }
        return weight;
    }

    @ApiStatus.Internal
    public void init() {
        if (!this.providerMap.isEmpty())
            throw new IllegalStateException("Already initialized");
        ModLoader.postEvent(new RegisterWeightProvidersEvent(this::register));
    }

    @ApiStatus.Internal
    public void registerDefaultProviders(RegisterWeightProvidersEvent event) {
        event.register(BundleWeightProvider.IDENTIFIER, new BundleWeightProvider());
        event.register(CurrencyWeightProvider.IDENTIFIER, new CurrencyWeightProvider());
        event.register(ContainerWeightProvider.IDENTIFIER, new ContainerWeightProvider());

        event.register(EntityEquipmentWeightProvider.IDENTIFIER, new EntityEquipmentWeightProvider());
        event.register(PlayerInventoryWeightProvider.IDENTIFIER, new PlayerInventoryWeightProvider());
    }

    private synchronized void register(Identifier id, WeightProvider provider) {
        if (this.providerMap.putIfAbsent(Objects.requireNonNull(id), Objects.requireNonNull(provider)) != null) {
            TarkovCraftCore.LOGGER.error(MARKER, "Detected attempted weight provider override for ID {}, value '{}', skipping registration!", id, provider.getClass().getCanonicalName());
            return;
        }
        WeightProvider.WeightSource source = provider.getSource();
        this.typeProviderMap.put(source, provider);
    }

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        FORMAT.setDecimalFormatSymbols(symbols);
    }
}
