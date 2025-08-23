package tnt.tarkovcraft.core.common.weight.provider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.weight.WeightContext;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

public class PlayerInventoryWeightProvider implements WeightProvider {

    public static final ResourceLocation IDENTIFIER = TarkovCraftCore.createResourceLocation("weight/entity/player_inventory");

    @Override
    public int getWeight(WeightContext context) {
        LivingEntity entity = context.entity();
        if (entity.getType() != EntityType.PLAYER) {
            return 0;
        }
        Player player = (Player) entity;
        Inventory inventory = player.getInventory();
        int weight = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                weight += context.getWeight(stack);
            }
        }
        return weight;
    }

    @Override
    public WeightSource getSource() {
        return WeightSource.ENTITY;
    }
}
