package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

import java.util.Objects;

public record MailMessageAttachmentType<A extends MailMessageAttachment>(ResourceLocation identifier, MapCodec<A> codec) {

    public static final Codec<MailMessageAttachment> ID_CODEC = TarkovCraftRegistries.MAIL_MESSAGE_ATTACHMENT.byNameCodec().dispatch(MailMessageAttachment::getType, MailMessageAttachmentType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MailMessageAttachmentType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
