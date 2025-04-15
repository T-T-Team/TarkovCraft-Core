package tnt.tarkovcraft.core.network.message.mail;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.mail.MailManager;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.UUID;

public record C2S_MailBlockUser(UUID id, boolean block) implements CustomPacketPayload {

    public static final ResourceLocation PACKET_ID = TarkovCraftCoreNetwork.createId(C2S_MailBlockUser.class);
    public static final Type<C2S_MailBlockUser> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<FriendlyByteBuf, C2S_MailBlockUser> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, C2S_MailBlockUser::id,
            ByteBufCodecs.BOOL, C2S_MailBlockUser::block,
            C2S_MailBlockUser::new
    );

    public void handleMessage(IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        MailManager manager = player.getData(CoreDataAttachments.MAIL_MANAGER);
        if (this.block()) {
            manager.block(this.id());
        } else {
            manager.unblock(this.id());
        }
        PacketDistributor.sendToPlayer(player, new S2C_SendDataAttachments(player, CoreDataAttachments.MAIL_MANAGER.get()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
