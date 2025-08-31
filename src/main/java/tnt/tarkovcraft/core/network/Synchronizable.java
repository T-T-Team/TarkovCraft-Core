package tnt.tarkovcraft.core.network;

import com.mojang.serialization.Codec;

@Deprecated // TODO find replacement for data screens - should be triggered from sync handler or smth like that
public interface Synchronizable<T> {
    Codec<T> networkCodec();
    default void preSyncPrepare() {}
}
