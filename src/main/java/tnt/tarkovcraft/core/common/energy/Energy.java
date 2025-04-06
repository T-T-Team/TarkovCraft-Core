package tnt.tarkovcraft.core.common.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

public class Energy {

    public static final Codec<Energy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TarkovCraftRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("maxLevel").forGetter(t -> t.maxLevelAttribute),
            TarkovCraftRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("consumption").forGetter(t -> t.consumptionAttribute),
            TarkovCraftRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("recovery").forGetter(t -> t.recoveryAttribute),
            Codec.FLOAT.fieldOf("energy").forGetter(t -> t.energy)
    ).apply(instance, Energy::new));

    private final Holder<Attribute> maxLevelAttribute;
    private final Holder<Attribute> consumptionAttribute;
    private final Holder<Attribute> recoveryAttribute;
    private float energy;

    public Energy(Holder<Attribute> maxLevelAttribute, Holder<Attribute> consumptionAttribute, Holder<Attribute> recoveryAttribute, float energy) {
        this.maxLevelAttribute = maxLevelAttribute;
        this.consumptionAttribute = consumptionAttribute;
        this.recoveryAttribute = recoveryAttribute;
        this.energy = energy;
    }

    public boolean hasEnergy(float requiredValue) {
        return this.energy >= requiredValue;
    }

    public boolean hasAnyEnergy() {
        return this.energy > 0;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getEnergy() {
        return energy;
    }

    public void set(EntityAttributeData data, float value) {
        float max = this.getMaxEnergy(data);
        this.energy = Mth.clamp(value, 0.0F, max);
    }

    void setInternal(float value) {
        this.energy = value;
    }

    public void recover(EntityAttributeData data) {
        float max = this.getMaxEnergy(data);
        if (this.energy < max) {
            float recoveryAmount = Math.abs(this.getRecoveryAmount(data));
            this.energy = Math.min(max, this.energy + recoveryAmount);
        }
    }

    public void consume(EntityAttributeData data, float amount) {
        float consumptionMultiplier = Math.abs(this.getConsumptionMultiplier(data));
        this.set(data, this.energy - amount * consumptionMultiplier);
    }

    public float getMaxEnergy(EntityAttributeData data) {
        return this.value(data, this.maxLevelAttribute);
    }

    public float getRecoveryAmount(EntityAttributeData data) {
        return this.value(data, this.recoveryAttribute);
    }

    public float getConsumptionMultiplier(EntityAttributeData data) {
        return this.value(data, this.consumptionAttribute);
    }

    private float value(EntityAttributeData data, Holder<Attribute> ref) {
        return data.getAttribute(ref).floatValue();
    }
}
