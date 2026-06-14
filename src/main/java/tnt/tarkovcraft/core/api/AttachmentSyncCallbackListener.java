package tnt.tarkovcraft.core.api;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

/**
 * Allows listening to attachment sync events. Triggered immediately after the attachment is updated on a client
 * @param <T> Attachment type
 */
public interface AttachmentSyncCallbackListener<T> {

    void onDataSynced(IAttachmentHolder holder, AttachmentType<T> attachmentType, T attachment);
}
