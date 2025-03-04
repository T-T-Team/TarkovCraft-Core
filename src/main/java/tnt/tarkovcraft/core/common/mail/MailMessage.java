package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import tnt.tarkovcraft.core.util.Codecs;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

public final class MailMessage {

    public static final UUID SYSTEM_ID = Util.NIL_UUID;
    public static final Codec<MailMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("messageId").forGetter(t -> t.messageId),
            UUIDUtil.STRING_CODEC.fieldOf("sender").forGetter(t -> t.sender),
            ComponentSerialization.CODEC.fieldOf("body").forGetter(t -> t.body),
            Codec.unboundedMap(
                    UUIDUtil.STRING_CODEC,
                    MailMessageAttachmentType.ID_CODEC
            ).fieldOf("attachments").xmap(
                    LinkedHashMap::new,
                    Function.identity()
            ).forGetter(t -> (LinkedHashMap<UUID, MailMessageAttachment>) t.attachments),
            Codecs.ZONED_DATE_TIME_CODEC.optionalFieldOf("expiryDate").forGetter(t -> Optional.ofNullable(t.expiryDate))
    ).apply(instance, MailMessage::new));

    private final UUID messageId;
    private final UUID sender;
    private final Component body;
    private final Map<UUID, MailMessageAttachment> attachments;
    private ZonedDateTime expiryDate;

    private MailMessage(UUID messageId, UUID sender, Component body, LinkedHashMap<UUID, MailMessageAttachment> attachments, Optional<ZonedDateTime> expiryDate) {
        this.messageId = messageId;
        this.sender = sender;
        this.body = body;
        this.attachments = attachments;
        expiryDate.ifPresent(zdt -> this.expiryDate = zdt);
    }

    private MailMessage(UUID sender, Component body) {
        this.messageId = UUID.randomUUID();
        this.sender = sender;
        this.body = body;
        this.attachments = new LinkedHashMap<>();
        this.expiryDate = ZonedDateTime.now(ZoneOffset.UTC).plusDays(3);
    }

    public static MailMessage create(UUID sender, Component body) {
        return new MailMessage(sender, body);
    }

    public static MailMessage system(Component body) {
        return create(SYSTEM_ID, body);
    }

    public static MailMessage simpleChatMessage(UUID sender, String text) {
        return new MailMessage(sender, Component.literal(text))
                .doNotExpire();
    }

    public boolean hasAttachments() {
        return !this.attachments.isEmpty();
    }

    public Map<UUID, MailMessageAttachment> listAttachments() {
        return this.attachments;
    }

    public Optional<MailMessageAttachment> getAttachment(UUID uuid) {
        return Optional.ofNullable(this.attachments.get(uuid));
    }

    public MailMessage setExpiryDate(ZonedDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public MailMessage doNotExpire() {
        return this.setExpiryDate(null);
    }

    public MailMessage attachment(MailMessageAttachment attachment) {
        UUID attachmentId = UUID.randomUUID();
        this.attachments.put(attachmentId, attachment);
        return this;
    }

    public boolean isExpired() {
        if (this.expiryDate == null)
            return false;
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        return now.isAfter(this.expiryDate);
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getSender() {
        return sender;
    }
}
