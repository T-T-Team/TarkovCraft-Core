package tnt.tarkovcraft.core.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.util.Objects;
import java.util.Optional;

public class PartialAttachmentTypeSerializer<V> implements IAttachmentSerializer<Tag, V> {

    private final Codec<V> codec;

    private PartialAttachmentTypeSerializer(Codec<V> codec) {
        this.codec = codec;
    }

    public static <V> PartialAttachmentTypeSerializer<V> withCodec(Codec<V> codec) {
        Objects.requireNonNull(codec);
        return new PartialAttachmentTypeSerializer<>(codec);
    }

    @Override
    public @Nullable Tag write(V attachment, HolderLookup.Provider provider) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        DataResult<Tag> result = this.codec.encodeStart(ops, attachment);
        Optional<Tag> optional = result.resultOrPartial(error -> TarkovCraftCore.LOGGER.warn("Failed to fully serialize data attachment due to error: {}", error));
        return optional.orElseGet(() -> result.getOrThrow(message -> new IllegalStateException("Failed to serialize data attachment due to error: " + message)));
    }

    @Override
    public V read(IAttachmentHolder holder, Tag tag, HolderLookup.Provider provider) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        DataResult<V> result = this.codec.parse(ops, tag);
        Optional<V> optional = result.resultOrPartial(error -> TarkovCraftCore.LOGGER.warn("Failed to fully deserialize data attachment due to error: {}", error));
        return optional.orElseGet(() -> result.getOrThrow(message -> new IllegalStateException("Failed to deserialize data attachment due to error: " + message)));
    }
}
