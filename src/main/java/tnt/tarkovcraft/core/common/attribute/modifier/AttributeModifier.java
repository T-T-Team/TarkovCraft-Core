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

    public abstract double calculateValue(AttributeInstance source, double value);

    public abstract int ordering();

    public abstract AttributeModifierType<?> getType();

    public boolean onCancellationTick(AttributeInstance source) {
        return false;
    }

    public final UUID identifier() {
        return this.identifier;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ExpiringAttributeModifier that)) return false;
        return Objects.equals(identifier, that.identifier());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(identifier);
    }
}
