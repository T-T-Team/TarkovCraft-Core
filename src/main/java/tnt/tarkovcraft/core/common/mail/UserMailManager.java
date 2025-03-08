package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;

public final class UserMailManager {

    public static final Codec<UserMailManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MailList.CODEC.listOf().fieldOf("chat").forGetter(t -> new ArrayList<>(t.messages.values()))
    ).apply(instance, UserMailManager::resolve));

    private final Map<MailSource, MailList> messages;

    private static UserMailManager resolve(List<MailList> chats) {
        UserMailManager result = new UserMailManager();
        for (MailList chat : chats) {
            result.messages.put(chat.getSource(), chat);
        }
        return result;
    }

    public UserMailManager() {
        this.messages = new HashMap<>();
    }

    public void addMessage(MailSource source, MailMessage message) {
        this.messages.computeIfAbsent(source, MailList::new).send(message);
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
}
