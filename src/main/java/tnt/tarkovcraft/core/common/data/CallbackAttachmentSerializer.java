package tnt.tarkovcraft.core.common.data;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.Objects;
import java.util.function.BiConsumer;

public class CallbackAttachmentSerializer<V> implements IAttachmentSerializer<V> {

    private final MapCodec<V> codec;
    private final BiConsumer<V, IAttachmentHolder> holderConsumer;

    private CallbackAttachmentSerializer(MapCodec<V> codec, BiConsumer<V, IAttachmentHolder> holderConsumer) {
        this.codec = codec;
        this.holderConsumer = holderConsumer;
    }

    public static <V> CallbackAttachmentSerializer<V> create(MapCodec<V> codec, BiConsumer<V, IAttachmentHolder> holderConsumer) {
        Objects.requireNonNull(codec);
        Objects.requireNonNull(holderConsumer);
        return new CallbackAttachmentSerializer<>(codec, holderConsumer);
    }

    @Override
    public boolean write(V attachment, ValueOutput out) {
        out.store(this.codec, attachment);
        return true;
    }

    @Override
    public V read(IAttachmentHolder holder, ValueInput in) {
        V attachment = in.read(this.codec).orElseThrow(() -> new IllegalStateException("Failed to deserialize data attachment"));
        this.holderConsumer.accept(attachment, holder);
        return attachment;
    }
}
