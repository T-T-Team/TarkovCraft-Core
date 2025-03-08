package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.mail.UserMailManager;

import java.util.function.Supplier;

public final class BaseDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TarkovCraftCore.MOD_ID);

    public static final Supplier<AttachmentType<UserMailManager>> MAIL_LIST = REGISTRY.register("mail_list", () -> AttachmentType.builder((Supplier<UserMailManager>) UserMailManager::new)
            .serialize(UserMailManager.CODEC)
            .copyOnDeath()
            .build()
    );
}
