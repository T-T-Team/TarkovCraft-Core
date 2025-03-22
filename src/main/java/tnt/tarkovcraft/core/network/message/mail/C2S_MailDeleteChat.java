package tnt.tarkovcraft.core.network.message.mail;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.mail.MailList;
import tnt.tarkovcraft.core.common.mail.MailManager;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.UUID;

public record C2S_MailDeleteChat(UUID id) implements CustomPacketPayload {

    public static final ResourceLocation ID = TarkovCraftCoreNetwork.createId(C2S_MailDeleteChat.class);
    public static final Type<C2S_MailDeleteChat> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, C2S_MailDeleteChat> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, C2S_MailDeleteChat::id,
            C2S_MailDeleteChat::new
    );

    public void handleMessage(IPayloadContext ctx) {
        Player player = ctx.player();
        MailManager mailManager = player.getData(BaseDataAttachments.MAIL_MANAGER);
        MailSource source = mailManager.getSender(this.id());
        if (source == null) {
            TarkovCraftCore.LOGGER.error("Couldn't find chat for ID {}", this.id());
            return;
        }
        MailList chat = mailManager.deleteChat(source);
        if (chat != null) {
            PacketDistributor.sendToPlayer((ServerPlayer) player, new S2C_SendDataAttachments(player, BaseDataAttachments.MAIL_MANAGER.get()));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
