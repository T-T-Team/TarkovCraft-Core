package tnt.tarkovcraft.core.common.attribute;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;

import java.util.function.Function;
import java.util.function.Supplier;

public final class AttributeSystem {

    public static boolean isEnabledForEntity(Entity entity) {
        return entity != null && (entity.getType() == EntityType.PLAYER || entity.hasData(CoreDataAttachments.ENTITY_ATTRIBUTES));
    }

    public static EntityAttributeData getAttributes(Entity entity) {
        return entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
    }

    @Nullable
    public static EntityAttributeData getExistingAttributes(Entity entity) {
        return isEnabledForEntity(entity) ? getAttributes(entity) : null;
    }

    public static boolean getBooleanValue(Entity entity, Holder<Attribute> attribute, boolean defaultValue) {
        return getValue(entity, attribute, AttributeInstance::booleanValue, defaultValue);
    }

    public static int getIntValue(Entity entity, Holder<Attribute> attribute, int defaultValue) {
        return getValue(entity, attribute, AttributeInstance::intValue, defaultValue);
    }

    public static float getFloatValue(Entity entity, Holder<Attribute> attribute, float defaultValue) {
        return getValue(entity, attribute, AttributeInstance::floatValue, defaultValue);
    }

    public static double getDoubleValue(Entity entity, Holder<Attribute> attribute, double defaultValue) {
        return getValue(entity, attribute, AttributeInstance::value, defaultValue);
    }

    public static boolean getBooleanValue(Entity entity, Supplier<Attribute> attribute, boolean defaultValue) {
        return getValue(entity, attribute, AttributeInstance::booleanValue, defaultValue);
    }

    public static int getIntValue(Entity entity, Supplier<Attribute> attribute, int defaultValue) {
        return getValue(entity, attribute, AttributeInstance::intValue, defaultValue);
    }

    public static float getFloatValue(Entity entity, Supplier<Attribute> attribute, float defaultValue) {
        return getValue(entity, attribute, AttributeInstance::floatValue, defaultValue);
    }

    public static double getDoubleValue(Entity entity, Supplier<Attribute> attribute, double defaultValue) {
        return getValue(entity, attribute, AttributeInstance::value, defaultValue);
    }

    public static <T> T getValue(Entity entity, Supplier<Attribute> attribute, Function<AttributeInstance, T> getter, T defaultValue) {
        return getValue(entity, attribute.get(), getter, defaultValue);
    }

    public static <T> T getValue(Entity entity, Holder<Attribute> attribute, Function<AttributeInstance, T> getter, T defaultValue) {
        return getValue(entity, attribute.value(), getter, defaultValue);
    }

    public static <T> T getValue(Entity entity, Attribute attribute, Function<AttributeInstance, T> getter, T defaultValue) {
        if (isEnabledForEntity(entity)) {
            EntityAttributeData data = getAttributes(entity);
            AttributeInstance instance = data.getAttribute(attribute);
            return getter.apply(instance);
        }
        return defaultValue;
    }

    public static void addModifier(Entity entity, Attribute attribute, AttributeModifier modifier, boolean replace) {
        if (isEnabledForEntity(entity)) {
            EntityAttributeData data = getAttributes(entity);
            AttributeInstance instance = data.getAttribute(attribute);
            if (replace || !instance.hasModifier(modifier)) {
                instance.addModifier(modifier);
            }
        }
    }

    public static void addModifier(Entity entity, Holder<Attribute> attribute, AttributeModifier modifier, boolean replace) {
        addModifier(entity, attribute.value(), modifier, replace);
    }

    public static void addModifier(Entity entity, Supplier<Attribute> attribute, AttributeModifier modifier, boolean replace) {
        addModifier(entity, attribute.get(), modifier, replace);
    }

    public static void removeModifier(Entity entity, Attribute attribute, Identifier modifier) {
        if (isEnabledForEntity(entity)) {
            EntityAttributeData data = getAttributes(entity);
            AttributeInstance instance = data.getAttribute(attribute);
            if (instance.hasModifier(modifier)) {
                instance.removeModifier(modifier);
            }
        }
    }

    public static void removeModifier(Entity entity, Attribute attribute, AttributeModifier modifier) {
        if (isEnabledForEntity(entity)) {
            EntityAttributeData data = getAttributes(entity);
            AttributeInstance instance = data.getAttribute(attribute);
            if (instance.hasModifier(modifier)) {
                instance.removeModifier(modifier);
            }
        }
    }

    public static void removeModifier(Entity entity, Holder<Attribute> attribute, Identifier modifier) {
        removeModifier(entity, attribute.value(), modifier);
    }

    public static void removeModifier(Entity entity, Supplier<Attribute> attribute, Identifier modifier) {
        removeModifier(entity, attribute.get(), modifier);
    }

    public static void removeModifier(Entity entity, Holder<Attribute> attribute, AttributeModifier modifier) {
        removeModifier(entity, attribute.value(), modifier);
    }

    public static void removeModifier(Entity entity, Supplier<Attribute> attribute, AttributeModifier modifier) {
        removeModifier(entity, attribute.get(), modifier);
    }

    public static void sync(Entity entity) {
        entity.syncData(CoreDataAttachments.ENTITY_ATTRIBUTES);
    }
}
