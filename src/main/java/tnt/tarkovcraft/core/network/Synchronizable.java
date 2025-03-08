package tnt.tarkovcraft.core.network;

import net.minecraft.nbt.CompoundTag;

public interface Synchronizable {

    CompoundTag serialize();

    void deserialize(CompoundTag tag);
}
