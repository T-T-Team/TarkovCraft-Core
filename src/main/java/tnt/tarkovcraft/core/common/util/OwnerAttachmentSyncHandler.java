package tnt.tarkovcraft.core.common.util;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Sync handler which sends data only to the attachment holder itself
 */
public class OwnerAttachmentSyncHandler<T> implements AttachmentSyncHandler<T> {

    private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;
    private BiConsumer<T, IAttachmentHolder> onRead = (t, holder) -> {};

    public OwnerAttachmentSyncHandler(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        this.codec = codec;
    }

    public OwnerAttachmentSyncHandler<T> onRead(BiConsumer<T, IAttachmentHolder> onRead) {
        this.onRead = Objects.requireNonNull(onRead);
        return this;
    }

    @Override
    public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
        return holder == to;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf, T attachment, boolean initialSync) {
        this.codec.encode(buf, attachment);
    }

    @Override
    public @Nullable T read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable T previousValue) {
        T t = this.codec.decode(buf);
        this.onRead.accept(t, holder);
        return t;
    }
}
