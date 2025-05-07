package tnt.tarkovcraft.core.network.message.mail;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.Notification;
import tnt.tarkovcraft.core.common.mail.MailMessage;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.common.mail.MailSystem;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.UUID;

public record C2S_MailSendMessage(UUID target, MailMessage message) implements CustomPacketPayload {

    public static final ResourceLocation ID = TarkovCraftCoreNetwork.createId(C2S_MailSendMessage.class);
    public static final Type<C2S_MailSendMessage> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, C2S_MailSendMessage> CODEC = StreamCodec.of(C2S_MailSendMessage::encode, C2S_MailSendMessage::decode);

    public void handleMessage(IPayloadContext ctx) {
        Player sender = ctx.player();
        MinecraftServer server = sender.getServer();
        ServerPlayer target = server.getPlayerList().getPlayer(this.target());
        if (target == null) {
            Notification notification = Notification.error(MailSystem.FAILED_TO_SEND_MESSAGE);
            notification.send((ServerPlayer) sender);
            TarkovCraftCore.LOGGER.warn(MailSystem.MARKER, "Received invalid message recipient from player {}", sender);
            return;
        }
        MailSystem.sendMessage(target, MailSource.player(sender), this.message());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(FriendlyByteBuf buf, C2S_MailSendMessage msg) {
        buf.writeUUID(msg.target);
        buf.writeNbt(Codecs.serializeNbtCompound(MailMessage.CODEC, msg.message));
    }

    private static C2S_MailSendMessage decode(FriendlyByteBuf buf) {
        UUID target = buf.readUUID();
        MailMessage msg = Codecs.deserializeNbtCompound(MailMessage.CODEC, buf.readNbt());
        return new C2S_MailSendMessage(target, msg);
    }
}
