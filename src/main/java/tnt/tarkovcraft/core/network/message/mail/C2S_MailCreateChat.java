package tnt.tarkovcraft.core.network.message.mail;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.mail.MailManager;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.common.mail.MailSystem;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.UUID;

public record C2S_MailCreateChat(UUID target) implements CustomPacketPayload {

    public static final ResourceLocation ID = TarkovCraftCoreNetwork.createId(C2S_MailCreateChat.class);
    public static final Type<C2S_MailCreateChat> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, C2S_MailCreateChat> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            C2S_MailCreateChat::target,
            C2S_MailCreateChat::new
    );

    public void handleMessage(IPayloadContext ctx) {
        Player player = ctx.player();
        MailManager mailManager = player.getData(BaseDataAttachments.MAIL_MANAGER);
        MinecraftServer server = player.getServer();
        ServerPlayer targetPlayer = server.getPlayerList().getPlayer(this.target());
        if (targetPlayer == null) {
            TarkovCraftCore.LOGGER.warn(MailSystem.MARKER, "Player {} is not online, cannot open new chat", this.target());
            return;
        }
        MailSource source = MailSource.player(targetPlayer);
        MailManager targetMailManager = targetPlayer.getData(BaseDataAttachments.MAIL_MANAGER);
        if (!targetMailManager.isBlocked(source) && !mailManager.hasChat(source)) {
            mailManager.getChat(source);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new S2C_SendDataAttachments(player, BaseDataAttachments.MAIL_MANAGER.get()));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
