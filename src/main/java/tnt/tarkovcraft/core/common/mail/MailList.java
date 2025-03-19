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
            Codec.BOOL.fieldOf("pin").forGetter(t -> t.pinned)
    ).apply(instance, MailList::new));

    private final MailSource source;
    private final List<MailMessage> messages;
    private boolean pinned;

    private MailList(MailSource source, List<MailMessage> messages, boolean pinned) {
        this.source = source;
        this.messages = new ArrayList<>(messages);
        this.pinned = pinned;
    }

    public MailList(MailSource source) {
        this(source, new ArrayList<>(), false);
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isPinned() {
        return pinned;
    }

    public String getMessageCount() {
        return this.messages.size() > 99 ? "[99+]" : String.format("[%d]", this.messages.size());
    }

    public void receive(MailMessage message) {
        this.send(message);
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
