package tnt.tarkovcraft.core.network;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface Synchronizable {

    CompoundTag serialize(HolderLookup.Provider provider);

    void deserialize(CompoundTag tag, HolderLookup.Provider provider);
}
