package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MailList implements Comparable<MailList> {

    public static final Codec<MailList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MailSource.CODEC.fieldOf("src").forGetter(t -> t.source),
            MailMessage.CODEC.listOf().fieldOf("msg").forGetter(t -> t.messages),
            Codec.BOOL.fieldOf("pin").forGetter(t -> t.pinned),
            Codec.INT.fieldOf("count").forGetter(t -> t.messageCount)
    ).apply(instance, MailList::new));

    private final MailSource source;
    private final List<MailMessage> messages;
    private boolean pinned;
    private int messageCount;

    private MailList(MailSource source, List<MailMessage> messages, boolean pinned, int messageCount) {
        this.source = source;
        this.messages = new ArrayList<>(messages);
        this.pinned = pinned;
        this.messageCount = messageCount;
    }

    public MailList(MailSource source) {
        this(source, new ArrayList<>(), false, 0);
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void markAsRead() {
        this.messageCount = 0;
    }

    public String getMessageCount() {
        return this.messageCount > 99 ? "[99+]" : String.format("[%d]", this.messageCount);
    }

    public void receive(MailMessage message) {
        this.send(message);
        this.messageCount++;
    }

    public void send(MailMessage message) {
        this.messages.add(message);
    }

    public void delete(MailMessage message) {
        this.messages.remove(message);
    }

    public MailSource getSource() {
        return source;
    }

    public List<MailMessage> listMessages() {
        return messages;
    }

    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    public ZonedDateTime getLastMessageTime() {
        return this.messages.isEmpty() ? null : this.messages.getLast().getMessageReceptionTime();
    }

    @Override
    public int compareTo(@NotNull MailList o) {
        if (this.pinned != o.pinned) {
            return this.pinned ? -1 : 1;
        }
        return Comparator.nullsLast(ZonedDateTime::compareTo).compare(this.getLastMessageTime(), o.getLastMessageTime());
    }
}
