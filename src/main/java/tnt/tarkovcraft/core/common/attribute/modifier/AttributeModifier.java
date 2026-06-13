package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;
import java.util.function.Function;

public abstract class AttributeModifier {

    public static final Codec<AttributeModifier> CODEC = CoreRegistries.ATTRIBUTE_MODIFIER
            .byNameCodec()
            .dispatch(AttributeModifier::codec, Function.identity());

    public static final int ORDER_MATH_PARENTHESES = 100;
    public static final int ORDER_MATH_EXP = 200;
    public static final int ORDER_MATH_MULTIPLICATION = 300;
    public static final int ORDER_MATH_ADDITION = 400;

    private final Identifier identifier;

    protected AttributeModifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public static SetValueAttributeModifier set(Identifier identifier, double value) {
        return new SetValueAttributeModifier(identifier, value);
    }

    public static AddValueModifier add(Identifier identifier, double value) {
        return new AddValueModifier(identifier, value);
    }

    public static AddValueModifier subtract(Identifier identifier, double value) {
        return new AddValueModifier(identifier, -value);
    }

    public static MultiplyValueAttributeModifier multiplier(Identifier identifier, double multiplier) {
        return new MultiplyValueAttributeModifier(identifier, multiplier);
    }

    public static MultiplyValueAttributeModifier multiplyBase(Identifier identifier, double multiplier) {
        return new MultiplyValueAttributeModifier(identifier, 1.0F + multiplier);
    }

    public abstract double calculateValue(AttributeInstance source, double value);

    public abstract int ordering();

    public abstract MapCodec<? extends AttributeModifier> codec();

    public final Identifier identifier() {
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

    public static <A extends AttributeModifier> Products.P1<RecordCodecBuilder.Mu<A>, Identifier> codec(RecordCodecBuilder.Instance<A> instance) {
        return instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(AttributeModifier::identifier)
        );
    }
}
