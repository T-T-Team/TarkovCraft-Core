package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.*;

public final class MailManager implements Synchronizable {

    public static final Codec<MailManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MailList.CODEC.listOf().fieldOf("chat").forGetter(t -> new ArrayList<>(t.messages.values()))
    ).apply(instance, MailManager::resolve));

    private final Map<MailSource, MailList> messages;

    private static MailManager resolve(List<MailList> chats) {
        MailManager result = new MailManager();
        for (MailList chat : chats) {
            result.messages.put(chat.getSource(), chat);
        }
        return result;
    }

    public MailManager() {
        this.messages = new HashMap<>();
    }

    public void sendMessage(MailSource source, MailMessage message) {
        this.getChat(source).send(message);
    }

    public void receiveMessage(MailSource source, MailMessage message) {
        this.getChat(source).receive(message);
    }

    public void removeMessage(MailSource source, MailMessage message) {
        if (this.messages.containsKey(source)) {
            MailList mailList = this.messages.get(source);
            mailList.delete(message);
        }
    }

    public Collection<MailList> listChats() {
        return this.messages.values();
    }

    public MailList getChat(MailSource source) {
        return this.messages.computeIfAbsent(source, MailList::new);
    }

    public boolean hasChat(MailSource source) {
        return this.messages.containsKey(source);
    }

    @Override
    public CompoundTag serialize() {
        return Codecs.serialize(CODEC, this);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        MailManager resolved = Codecs.deserialize(tag, CODEC);
        this.messages.clear();
        this.messages.putAll(resolved.messages);
    }
}
