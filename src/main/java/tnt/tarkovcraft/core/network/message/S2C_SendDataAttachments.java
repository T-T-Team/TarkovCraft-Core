package tnt.tarkovcraft.core.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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

    public S2C_SendDataAttachments(Entity entity, AttachmentType<? extends Synchronizable> type) {
        this(entity, Collections.singletonList(type));
    }

    public S2C_SendDataAttachments(Entity entity, List<AttachmentType<? extends Synchronizable>> types) {
        this.entityId = entity.getId();
        this.attachments = new ArrayList<>(types.size());
        this.data = new CompoundTag();
        types.stream().distinct().forEach(type -> {
            ResourceLocation key = NeoForgeRegistries.ATTACHMENT_TYPES.getKey(type);
            this.attachments.add(key);
            Synchronizable attachment = entity.getData(type);
            CompoundTag attachmentData = attachment.serialize(entity.registryAccess());
            this.data.put(key.toString(), attachmentData);
        });
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
    public <T extends Synchronizable> void handleMessage(IPayloadContext ctx) {
        Player player = ctx.player();
        Level level = player.level();
        Entity entity = level.getEntity(this.entityId);
        if (entity == null) {
            TarkovCraftCore.LOGGER.warn(TarkovCraftCore.MARKER, "Entity not found in level by ID: {}", this.entityId);
            return;
        }
        for (ResourceLocation attachment : this.attachments) {
            AttachmentType<?> type = NeoForgeRegistries.ATTACHMENT_TYPES.getValue(attachment);
            Synchronizable value = (Synchronizable) entity.getData(type);
            CompoundTag attachmentData = this.data.getCompoundOrEmpty(attachment.toString());
            value.deserialize(attachmentData, player.registryAccess());

            TarkovCraftCoreClient.sendDataSyncEvent(entity, type, value);
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
}
