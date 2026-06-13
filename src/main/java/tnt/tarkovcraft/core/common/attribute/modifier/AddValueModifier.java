package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;

import java.util.Locale;

public class AddValueModifier extends AttributeModifier {

    public static final MapCodec<AddValueModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codec(instance).and(instance.group(
            Codec.DOUBLE.fieldOf("value").forGetter(t -> t.value),
            Codec.INT.optionalFieldOf("order", ORDER_MATH_ADDITION).forGetter(t -> t.ordering)
    )).apply(instance, AddValueModifier::new));

    private final double value;
    private final int ordering;

    public AddValueModifier(Identifier identifier, double value) {
        this(identifier, value, ORDER_MATH_ADDITION);
    }

    public AddValueModifier(Identifier identifier, double value, int ordering) {
        super(identifier);
        this.value = value;
        this.ordering = ordering;
    }

    @Override
    public double calculateValue(AttributeInstance source, double value) {
        return value + this.value;
    }

    @Override
    public int ordering() {
        return this.ordering;
    }

    @Override
    public MapCodec<? extends AttributeModifier> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s: AddValue[value=%f]", this.identifier(), this.value);
    }
}
