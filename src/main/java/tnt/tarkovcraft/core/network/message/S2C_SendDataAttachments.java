package tnt.tarkovcraft.core.network.message;

import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class S2C_SendDataAttachments implements CustomPacketPayload {

    public static final ResourceLocation ID = TarkovCraftCoreNetwork.createId(S2C_SendDataAttachments.class);
    public static final Type<S2C_SendDataAttachments> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, S2C_SendDataAttachments> CODEC = StreamCodec.of(
            S2C_SendDataAttachments::encode,
            S2C_SendDataAttachments::new
    );

    private final int entityId;
    private final List<ResourceLocation> attachments;
    private final CompoundTag data;

    public S2C_SendDataAttachments(Entity entity, AttachmentType<? extends Synchronizable<?>> type) {
        this(entity, Collections.singletonList(type));
    }

    public <T extends Synchronizable<T>> S2C_SendDataAttachments(Entity entity, List<AttachmentType<? extends Synchronizable<?>>> types) {
        this.entityId = entity.getId();
        this.attachments = new ArrayList<>(types.size());
        this.data = new CompoundTag();

        RegistryAccess access = entity.registryAccess();
        RegistryOps<Tag> ops = access.createSerializationContext(NbtOps.INSTANCE);
        List<SerializableHolder<T>> serializables = new ArrayList<>();
        // pre sync prepare to update dependencies and so on
        for (AttachmentType<?> type : types) {
            ResourceLocation typeIdentifier = NeoForgeRegistries.ATTACHMENT_TYPES.getKey(type);
            this.attachments.add(typeIdentifier);
            T serializable = (T) entity.getData(type);
            serializable.preSyncPrepare();
            serializables.add(new SerializableHolder<>(typeIdentifier, serializable));
        }
        // serialize saved data
        for (SerializableHolder<T> holder : serializables) {
            T serializable = holder.serializable();
            Codec<T> networkCodec = serializable.networkCodec();
            Tag tag = networkCodec.encodeStart(ops, serializable).getOrThrow();
            ResourceLocation identifier = holder.id();
            this.data.put(identifier.toString(), tag);
        }
    }

    private S2C_SendDataAttachments(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        int count = buf.readInt();
        this.attachments = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ResourceLocation key = buf.readResourceLocation();
            this.attachments.add(key);
        }
        this.data = buf.readNbt();
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public <T extends Synchronizable<T>> void handleMessage(IPayloadContext ctx) {
        Player player = ctx.player();
        Level level = player.level();
        Entity entity = level.getEntity(this.entityId);
        if (entity == null) {
            TarkovCraftCore.LOGGER.warn(TarkovCraftCore.MARKER, "Entity not found in level by ID: {}", this.entityId);
            return;
        }
        RegistryOps<Tag> ops = entity.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        for (ResourceLocation attachment : this.attachments) {
            AttachmentType<T> type = (AttachmentType<T>) NeoForgeRegistries.ATTACHMENT_TYPES.getValue(attachment);
            T value = entity.getData(type);
            Codec<T> codec = value.networkCodec();
            T data = codec.parse(ops, this.data.getCompoundOrEmpty(attachment.toString())).getPartialOrThrow();
            entity.setData(type, data);
            TarkovCraftCoreClient.sendDataSyncEvent(entity, type, data);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(FriendlyByteBuf buffer, S2C_SendDataAttachments message) {
        buffer.writeInt(message.entityId);
        buffer.writeInt(message.attachments.size());
        message.attachments.forEach(buffer::writeResourceLocation);
        buffer.writeNbt(message.data);
    }

    private record SerializableHolder<T>(ResourceLocation id, T serializable) {
    }
}
