package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;
import java.util.UUID;

public abstract class AttributeModifier {

    public static final Codec<AttributeModifier> CODEC = CoreRegistries.ATTRIBUTE_MODIFIER.byNameCodec().dispatch(AttributeModifier::getType, AttributeModifierType::codec);

    public static final int ORDER_MATH_PARENTHESES = 100;
    public static final int ORDER_MATH_EXP = 200;
    public static final int ORDER_MATH_MULTIPLICATION = 300;
    public static final int ORDER_MATH_ADDITION = 400;

    private final UUID identifier;

    protected AttributeModifier(UUID identifier) {
        this.identifier = identifier;
    }

    public static SetValueAttributeModifier set(UUID id, double value) {
        return new SetValueAttributeModifier(id, value);
    }

    public static SetValueAttributeModifier set(String uuid, double value) {
        return set(UUID.fromString(uuid), value);
    }

    public static AddValueModifier add(UUID id, double value) {
        return new AddValueModifier(id, value);
    }

    public static AddValueModifier add(String uuid, double value) {
        return add(UUID.fromString(uuid), value);
    }

    public static AddValueModifier subtract(UUID id, double value) {
        return new AddValueModifier(id, -value);
    }

    public static AddValueModifier subtract(String uuid, double value) {
        return subtract(UUID.fromString(uuid), value);
    }

    public static MultiplyValueAttributeModifier multiplier(UUID id, double multiplier) {
        return new MultiplyValueAttributeModifier(id, multiplier);
    }

    public static MultiplyValueAttributeModifier multiply(String uuid, double multiplier) {
        return multiplier(UUID.fromString(uuid), multiplier);
    }

    public static MultiplyValueAttributeModifier multiplyBase(UUID id, double multiplier) {
        return new MultiplyValueAttributeModifier(id, 1.0F + multiplier);
    }

    public static MultiplyValueAttributeModifier multiplyBase(String uuid, double multiplier) {
        return multiplyBase(UUID.fromString(uuid), multiplier);
    }

    public abstract double calculateValue(AttributeInstance source, double value);

    public abstract int ordering();

    public abstract AttributeModifierType<?> getType();

    public final UUID identifier() {
        return this.identifier;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AttributeModifier modifier)) return false;
        return Objects.equals(identifier, modifier.identifier);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(identifier);
    }
}
