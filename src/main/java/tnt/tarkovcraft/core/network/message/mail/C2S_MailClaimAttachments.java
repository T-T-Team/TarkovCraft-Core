package tnt.tarkovcraft.core.network.message.mail;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.mail.*;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;

import java.util.*;

public record C2S_MailClaimAttachments(UUID chatId) implements CustomPacketPayload {

    public static final ResourceLocation ID = TarkovCraftCoreNetwork.createId(C2S_MailClaimAttachments.class);
    public static final Type<C2S_MailClaimAttachments> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, C2S_MailClaimAttachments> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, C2S_MailClaimAttachments::chatId,
            C2S_MailClaimAttachments::new
    );

    public void handleMessage(IPayloadContext context) {
        Player player = context.player();
        MailManager mailManager = player.getData(CoreDataAttachments.MAIL_MANAGER);
        MailSource source = mailManager.getSender(this.chatId());
        if (source != null) {
            MailList chat = mailManager.getChat(source);
            List<MailMessage> messages = chat.listMessages();
            Iterator<MailMessage> iterator = messages.iterator();
            boolean claiming = true;
            while (iterator.hasNext() && claiming) {
                MailMessage message = iterator.next();
                if (message.isExpired()) {
                    TarkovCraftCore.LOGGER.debug(MailSystem.MARKER, "Removing expired message {} from player {}", message.getMessageId(), player.getName().getString());
                    iterator.remove();
                }
                Map<UUID, MailMessageAttachment> attachments = message.listAttachments();
                Set<UUID> claimedAttachments = new HashSet<>();
                for (Map.Entry<UUID, MailMessageAttachment> entry : attachments.entrySet()) {
                    UUID attachmentId = entry.getKey();
                    MailMessageAttachment attachment = entry.getValue();
                    if (attachment.isClaimable(message, attachmentId, player)) {
                        TarkovCraftCore.LOGGER.debug(MailSystem.MARKER, "User {} is claiming attachment {} with content {}", player.getName().getString(), attachmentId, attachment);
                        boolean canClaimNext = attachment.claim(message, attachmentId, player);
                        claimedAttachments.add(attachmentId);
                        if (!canClaimNext) {
                            claiming = false;
                            break;
                        }
                    }
                }
                claimedAttachments.forEach(message::removeAttachment);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
