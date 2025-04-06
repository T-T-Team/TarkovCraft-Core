package tnt.tarkovcraft.core.common.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.BaseAttributes;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.Codecs;

public class EnergyData implements Synchronizable {

    public static final Codec<EnergyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Energy.CODEC.fieldOf("arm").forGetter(t -> t.armEnergy),
            Energy.CODEC.fieldOf("leg").forGetter(t -> t.legEnergy)
    ).apply(instance, EnergyData::new));

    public static final float SPRINT_STAMINA_CONSUMPTION = 0.5F;

    private final Energy armEnergy;
    private final Energy legEnergy;

    public EnergyData() {
        this.armEnergy = new Energy(BaseAttributes.ARM_ENERGY_MAX, BaseAttributes.ARM_ENERGY_CONSUMPTION, BaseAttributes.ARM_ENERGY_RECOVERY, 100.0F);
        this.legEnergy = new Energy(BaseAttributes.LEG_ENERGY_MAX, BaseAttributes.LEG_ENERGY_CONSUMPTION, BaseAttributes.LEG_ENERGY_RECOVERY, 100.0F);
    }

    private EnergyData(Energy armEnergy, Energy legEnergy) {
        this.armEnergy = armEnergy;
        this.legEnergy = legEnergy;
    }

    public boolean hasEnergy(EnergyType type, float amount) {
        return this.getEnergyValue(type).hasEnergy(amount);
    }

    public void update(Entity entity) {
        EntityAttributeData attributeData = entity.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        Energy legEnergy = this.getEnergyValue(EnergyType.LEG_STAMINA);
        if (entity.isSprinting()) {
            if (!legEnergy.hasEnergy(SPRINT_STAMINA_CONSUMPTION)) {
                entity.setSprinting(false);
            }
            legEnergy.consume(attributeData, SPRINT_STAMINA_CONSUMPTION);
        } else {
            legEnergy.recover(attributeData);
        }
    }

    public Energy getEnergyValue(EnergyType type) {
        return switch (type) {
            case ARM_STAMINA -> armEnergy;
            case LEG_STAMINA -> legEnergy;
        };
    }

    @Override
    public CompoundTag serialize() {
        return Codecs.serialize(CODEC, this);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        EnergyData resolved = Codecs.deserialize(CODEC, tag);
        this.armEnergy.setInternal(resolved.armEnergy.getEnergy());
        this.legEnergy.setInternal(resolved.legEnergy.getEnergy());
    }
}
