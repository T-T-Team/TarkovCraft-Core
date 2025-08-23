package tnt.tarkovcraft.core.common.weight.provider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.weight.WeightContext;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

import java.util.EnumSet;
import java.util.Set;

public class EntityEquipmentWeightProvider implements WeightProvider {

    public static final ResourceLocation IDENTIFIER = TarkovCraftCore.createResourceLocation("weight/entity/equipment");
    public static final Set<EquipmentSlot> EQUIPMENT_SLOTS = EnumSet.of(
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD,
            EquipmentSlot.BODY, EquipmentSlot.SADDLE,
            EquipmentSlot.OFFHAND
    );

    @Override
    public int getWeight(WeightContext context) {
        LivingEntity entity = context.entity();
        int weight = 0;
        for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
            ItemStack equipped = entity.getItemBySlot(slot);
            if (!equipped.isEmpty()) {
                weight += context.getWeight(equipped);
            }
        }
        return weight;
    }

    @Override
    public WeightSource getSource() {
        return WeightSource.ENTITY;
    }
}
