package tnt.tarkovcraft.core.network;

import com.mojang.serialization.Codec;

public interface Synchronizable<T> {
    Codec<T> networkCodec();
    default void preSyncPrepare() {}
}
