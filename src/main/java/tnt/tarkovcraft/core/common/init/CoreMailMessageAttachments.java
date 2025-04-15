package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.mail.MailMessageAttachmentType;
import tnt.tarkovcraft.core.common.mail.MailMessageItemAttachment;

import java.util.function.Supplier;

public final class CoreMailMessageAttachments {

    public static final DeferredRegister<MailMessageAttachmentType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.MAIL_MESSAGE_ATTACHMENT, TarkovCraftCore.MOD_ID);

    public static final Supplier<MailMessageAttachmentType<MailMessageItemAttachment>> ITEM = REGISTRY.register("item", key -> new MailMessageAttachmentType<>(key, MailMessageItemAttachment.CODEC));
}
