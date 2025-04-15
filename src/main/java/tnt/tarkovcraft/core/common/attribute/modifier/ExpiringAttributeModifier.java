package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.init.CoreAttributeModifiers;

import java.util.UUID;

public class ExpiringAttributeModifier extends AttributeModifier {

    public static final MapCodec<ExpiringAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(AttributeModifier::identifier),
            AttributeModifierType.ID_CODEC.fieldOf("value").forGetter(t -> t.value),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("lifetime").forGetter(t -> t.maxLifetime),
            Codec.INT.optionalFieldOf("currentLifetime", 0).forGetter(t -> t.lifetime)
    ).apply(instance, ExpiringAttributeModifier::new));

    private final AttributeModifier value;
    private final int maxLifetime;
    private int lifetime;

    public ExpiringAttributeModifier(UUID identifier, AttributeModifier value, int maxLifetime) {
        this(identifier, value, maxLifetime, 0);
    }

    private ExpiringAttributeModifier(UUID identifier, AttributeModifier value, int maxLifetime, int lifetime) {
        super(identifier);
        this.value = value;
        this.maxLifetime = maxLifetime;
        this.lifetime = lifetime;
    }

    @Override
    public double calculateValue(AttributeInstance source, double value) {
        return this.value.calculateValue(source, value);
    }

    @Override
    public int ordering() {
        return this.value.ordering();
    }

    @Override
    public boolean onCancellationTick(AttributeInstance source) {
        boolean cancel = this.value.onCancellationTick(source);
        return cancel || this.lifetime++ >= this.maxLifetime;
    }

    @Override
    public AttributeModifierType<?> getType() {
        return CoreAttributeModifiers.EXPIRING.get();
    }

    @Override
    public String toString() {
        return String.format("Expiring[lifetime=%d,maxLifetime=%d,value=%s]", this.lifetime, this.maxLifetime, this.value);
    }
}
