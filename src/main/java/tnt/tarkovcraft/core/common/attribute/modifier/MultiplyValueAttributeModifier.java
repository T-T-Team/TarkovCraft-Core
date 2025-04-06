package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.init.BaseAttributeModifiers;

import java.util.UUID;

public class MultiplyValueAttributeModifier extends AttributeModifier {

    public static final MapCodec<MultiplyValueAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(AttributeModifier::identifier),
            Codec.DOUBLE.fieldOf("value").forGetter(t -> t.value),
            Codec.INT.optionalFieldOf("order", ORDER_MATH_MULTIPLICATION).forGetter(t -> t.ordering)
    ).apply(instance, MultiplyValueAttributeModifier::new));

    private final double value;
    private final int ordering;

    public MultiplyValueAttributeModifier(UUID identifier, double value) {
        this(identifier, value, ORDER_MATH_MULTIPLICATION);
    }

    public MultiplyValueAttributeModifier(UUID identifier, double value, int ordering) {
        super(identifier);
        this.value = value;
        this.ordering = ordering;
    }

    @Override
    public double calculateValue(AttributeInstance source, double value) {
        return value * this.value;
    }

    @Override
    public int ordering() {
        return this.ordering;
    }

    @Override
    public AttributeModifierType<?> getType() {
        return BaseAttributeModifiers.MUL_VALUE.get();
    }

    @Override
    public String toString() {
        return String.format("MultiplyValue=[value=%f]", this.value);
    }
}
