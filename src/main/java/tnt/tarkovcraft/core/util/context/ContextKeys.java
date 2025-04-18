package tnt.tarkovcraft.core.util.context;

import net.minecraft.core.BlockPos;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.util.UUID;

public final class ContextKeys {

    // Entity
    public static final ContextKey<Entity> ENTITY = new ContextKey<>(TarkovCraftCore.createResourceLocation("entity"));
    public static final ContextKey<LivingEntity> LIVING_ENTITY = new ContextKey<>(TarkovCraftCore.createResourceLocation("living_entity"));
    public static final ContextKey<Player> PLAYER = new ContextKey<>(TarkovCraftCore.createResourceLocation("player"));

    // Items
    public static final ContextKey<ItemStack> ITEM = new ContextKey<>(TarkovCraftCore.createResourceLocation("item"));

    // Inventory
    public static final ContextKey<Container> CONTAINER = new ContextKey<>(TarkovCraftCore.createResourceLocation("container"));

    // Other
    public static final ContextKey<Level> LEVEL = new ContextKey<>(TarkovCraftCore.createResourceLocation("level"));
    public static final ContextKey<DamageSource> DAMAGE_SOURCE = new ContextKey<>(TarkovCraftCore.createResourceLocation("damage_source"));
    public static final ContextKey<BlockPos> POSITION = new ContextKey<>(TarkovCraftCore.createResourceLocation("position"));
    public static final ContextKey<UUID> UUID = new ContextKey<>(TarkovCraftCore.createResourceLocation("uuid"));
}
