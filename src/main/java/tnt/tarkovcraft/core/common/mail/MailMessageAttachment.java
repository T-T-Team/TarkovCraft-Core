package tnt.tarkovcraft.core.common.mail;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface MailMessageAttachment {

    MailMessageAttachmentType<?> getType();

    boolean isClaimable(MailMessage message, UUID attachmentId, Player player);

    /**
     * Called when user claims the attachment
     * @param message Message to which this attachment is attached to
     * @param attachmentId Unique ID of this attachment
     * @param player Player who claims this attachment
     * @return Whether this attachment was claimed successfully. Returning {@code false} stops all following
     * attachments from being claimed
     */
    boolean claim(MailMessage message, UUID attachmentId, Player player);
}
