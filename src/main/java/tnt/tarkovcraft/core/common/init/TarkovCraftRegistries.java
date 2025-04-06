package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifierType;
import tnt.tarkovcraft.core.common.data.filter.ItemStackFilterType;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.mail.MailMessageAttachmentType;

public class TarkovCraftRegistries {

    // Global utilities
    public static final Registry<Attribute> ATTRIBUTE = new RegistryBuilder<>(Keys.ATTRIBUTE).create();
    public static final Registry<AttributeModifierType<?>> ATTRIBUTE_MODIFIER = new RegistryBuilder<>(Keys.ATTRIBUTE_MODIFIER).create();
    public static final Registry<ItemStackFilterType<?>> ITEMSTACK_FILTER = new RegistryBuilder<>(Keys.ITEMSTACK_FILTER).create();
    public static final Registry<NumberProviderType<?>> NUMBER_PROVIDER = new RegistryBuilder<>(Keys.NUMBER_PROVIDER).create();

    // Mail system
    public static final Registry<MailMessageAttachmentType<?>> MAIL_MESSAGE_ATTACHMENT = new RegistryBuilder<>(Keys.MAIL_MESSAGE_ATTACHMENT).create();

    public static final class Keys {

        public static final ResourceKey<Registry<Attribute>> ATTRIBUTE = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("attribute"));
        public static final ResourceKey<Registry<AttributeModifierType<?>>> ATTRIBUTE_MODIFIER = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("attribute_modifier"));
        public static final ResourceKey<Registry<ItemStackFilterType<?>>> ITEMSTACK_FILTER = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("itemstack_filter"));
        public static final ResourceKey<Registry<NumberProviderType<?>>> NUMBER_PROVIDER = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("number_provider"));
        public static final ResourceKey<Registry<MailMessageAttachmentType<?>>> MAIL_MESSAGE_ATTACHMENT = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("mail_message_attachment"));
    }
}
