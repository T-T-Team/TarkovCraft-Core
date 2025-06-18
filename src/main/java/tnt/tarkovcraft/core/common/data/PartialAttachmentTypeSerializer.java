package tnt.tarkovcraft.core.common.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.Objects;
import java.util.function.BiConsumer;

public class PartialAttachmentTypeSerializer<V> implements IAttachmentSerializer<V> {

    private final Codec<V> codec;
    private final BiConsumer<V, IAttachmentHolder> holderConsumer;

    private PartialAttachmentTypeSerializer(Codec<V> codec, BiConsumer<V, IAttachmentHolder> holderConsumer) {
        this.codec = codec;
        this.holderConsumer = holderConsumer;
    }

    public static <V> PartialAttachmentTypeSerializer<V> withCodec(Codec<V> codec) {
        Objects.requireNonNull(codec);
        return new PartialAttachmentTypeSerializer<>(codec, (v, iAttachmentHolder) -> {});
    }

    public static <V> PartialAttachmentTypeSerializer<V> withCodecAndHolder(Codec<V> codec, BiConsumer<V, IAttachmentHolder> holderConsumer) {
        Objects.requireNonNull(codec);
        Objects.requireNonNull(holderConsumer);
        return new PartialAttachmentTypeSerializer<>(codec, holderConsumer);
    }

    @Override
    public boolean write(V attachment, ValueOutput out) {
        out.store("data", this.codec, attachment);
        return true;
    }

    @Override
    public V read(IAttachmentHolder holder, ValueInput in) {
        V attachment = in.read("data", this.codec).orElseThrow(() -> new IllegalStateException("Failed to deserialize data attachment"));
        this.holderConsumer.accept(attachment, holder);
        return attachment;
    }
}
