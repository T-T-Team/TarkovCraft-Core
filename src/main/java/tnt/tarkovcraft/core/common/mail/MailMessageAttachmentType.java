package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

public record MailMessageAttachmentType<A extends MailMessageAttachment>(ResourceLocation identifier, MapCodec<A> codec) {

    public static final Codec<MailMessageAttachment> ID_CODEC = TarkovCraftRegistries.MAIL_MESSAGE_ATTACHMENT.byNameCodec().dispatch(MailMessageAttachment::getType, MailMessageAttachmentType::codec);
}
