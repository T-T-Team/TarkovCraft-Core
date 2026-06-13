package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;

import java.util.Locale;

public class SetValueAttributeModifier extends AttributeModifier {

    public static final MapCodec<SetValueAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codec(instance).and(instance.group(
            Codec.DOUBLE.fieldOf("value").forGetter(t -> t.value),
            Codec.INT.optionalFieldOf("order", ORDER_MATH_PARENTHESES).forGetter(t -> t.order)
    )).apply(instance, SetValueAttributeModifier::new));

    private final double value;
    private final int order;

    public SetValueAttributeModifier(Identifier identifier, double value) {
        this(identifier, value, ORDER_MATH_PARENTHESES);
    }

    public SetValueAttributeModifier(Identifier identifier, double value, int order) {
        super(identifier);
        this.value = value;
        this.order = order;
    }

    @Override
    public double calculateValue(AttributeInstance source, double value) {
        return this.value;
    }

    @Override
    public int ordering() {
        return this.order;
    }

    @Override
    public MapCodec<? extends AttributeModifier> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s: SetValue=[value=%f]", this.identifier(), this.value);
    }
}
