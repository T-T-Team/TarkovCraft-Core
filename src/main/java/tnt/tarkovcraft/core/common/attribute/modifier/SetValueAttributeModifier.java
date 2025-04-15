package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.init.CoreAttributeModifiers;

import java.util.UUID;

public class SetValueAttributeModifier extends AttributeModifier {

    public static final MapCodec<SetValueAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(AttributeModifier::identifier),
            Codec.DOUBLE.fieldOf("value").forGetter(t -> t.value),
            Codec.INT.optionalFieldOf("order", ORDER_MATH_PARENTHESES).forGetter(t -> t.order)
    ).apply(instance, SetValueAttributeModifier::new));

    private final double value;
    private final int order;

    public SetValueAttributeModifier(UUID identifier, double value) {
        this(identifier, value, ORDER_MATH_PARENTHESES);
    }

    public SetValueAttributeModifier(UUID identifier, double value, int order) {
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
    public AttributeModifierType<?> getType() {
        return CoreAttributeModifiers.SET_VALUE.get();
    }

    @Override
    public String toString() {
        return String.format("SetValue=[value=%f]", this.value);
    }
}
