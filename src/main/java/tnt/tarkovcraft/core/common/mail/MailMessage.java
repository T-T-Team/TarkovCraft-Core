package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.util.Codecs;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
            Codecs.ZONED_DATE_TIME_CODEC.fieldOf("messageTs").forGetter(t -> t.messageTs),
            Codecs.ZONED_DATE_TIME_CODEC.optionalFieldOf("expiryDate").forGetter(t -> Optional.ofNullable(t.expiryDate))
    ).apply(instance, MailMessage::new));

    private final UUID messageId;
    private final UUID sender;
    private final Component body;
    private final Map<UUID, MailMessageAttachment> attachments;
    private final ZonedDateTime messageTs;
    private ZonedDateTime expiryDate;

    private MailMessage(UUID messageId, UUID sender, Component body, LinkedHashMap<UUID, MailMessageAttachment> attachments, ZonedDateTime messageTs, Optional<ZonedDateTime> expiryDate) {
        this.messageId = messageId;
        this.sender = sender;
        this.body = body;
        this.attachments = attachments;
        this.messageTs = messageTs;
        expiryDate.ifPresent(zdt -> this.expiryDate = zdt);
    }

    private MailMessage(UUID sender, Component body) {
        this.messageId = UUID.randomUUID();
        this.sender = sender;
        this.body = body;
        this.attachments = new LinkedHashMap<>();
        this.messageTs = ZonedDateTime.now(ZoneOffset.UTC);
        this.expiryDate = ZonedDateTime.now(ZoneOffset.UTC).plusDays(3);
    }

    public static MailMessage create(MailSource sender, Component body) {
        return new MailMessage(sender.getSourceId(), body);
    }

    public static MailMessage system(Component body) {
        return create(MailSource.SYSTEM, body);
    }

    public static MailMessage simpleChatMessage(MailSource source, String text) {
        return new MailMessage(source.getSourceId(), Component.literal(text))
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

    public void removeAttachment(UUID uuid) {
        this.attachments.remove(uuid);
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
        TarkovCraftCore.LOGGER.debug(MailSystem.MARKER, "Adding attachment {} to message {}. Content {}", attachmentId, this.messageId, attachment);
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

    public Component getContent() {
        return this.body;
    }

    public UUID getSender() {
        return sender;
    }

    public ZonedDateTime getMessageReceptionTime() {
        return messageTs;
    }

    public boolean isBlank() {
        return this.body.getString().isBlank();
    }
}
