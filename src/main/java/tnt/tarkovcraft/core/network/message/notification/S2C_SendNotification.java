package tnt.tarkovcraft.core.network.message.notification;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.client.notification.NotificationChannel;
import tnt.tarkovcraft.core.common.Notification;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;

public record S2C_SendNotification(Notification notification) implements CustomPacketPayload {

    public static final ResourceLocation ID = TarkovCraftCoreNetwork.createId(S2C_SendNotification.class);
    public static final Type<S2C_SendNotification> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2C_SendNotification> CODEC = StreamCodec.composite(
            Notification.STREAM_CODEC, S2C_SendNotification::notification,
            S2C_SendNotification::new
    );

    @OnlyIn(Dist.CLIENT)
    public void handleMessage(IPayloadContext context) {
        NotificationChannel channel = NotificationChannel.MAIN;
        channel.add(this.notification());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
