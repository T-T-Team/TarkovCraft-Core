package tnt.tarkovcraft.core.common.mail;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface MailMessageAttachment {

    MailMessageAttachmentType<?> getType();

    boolean canOpen(MailMessage message, UUID attachmentId, Player player);

    void open(MailMessage message, UUID attachmentId, Player player);
}
