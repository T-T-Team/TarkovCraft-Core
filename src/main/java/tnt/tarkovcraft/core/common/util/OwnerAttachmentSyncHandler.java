package tnt.tarkovcraft.core.common.util;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jspecify.annotations.Nullable;

/**
 * Sync handler which sends data only to the attachment holder itself
 */
public class OwnerAttachmentSyncHandler<T> implements AttachmentSyncHandler<T> {

    private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;

    public OwnerAttachmentSyncHandler(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        this.codec = codec;
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
        return this.codec.decode(buf);
    }
}
