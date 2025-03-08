package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.mail.MailManager;

import java.util.function.Supplier;

public final class BaseDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TarkovCraftCore.MOD_ID);

    public static final Supplier<AttachmentType<MailManager>> MAIL_MANAGER = REGISTRY.register("mail_list", () -> AttachmentType.builder(MailManager::new)
            .serialize(MailManager.CODEC)
            .copyOnDeath()
            .build()
    );
}
