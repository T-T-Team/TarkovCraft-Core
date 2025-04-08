package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.*;

public final class MailManager implements Synchronizable {

    public static final Codec<MailManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MailList.CODEC.listOf().fieldOf("chat").forGetter(t -> new ArrayList<>(t.messages.values())),
            UUIDUtil.STRING_CODEC.listOf().fieldOf("blocked").forGetter(t -> new ArrayList<>(t.blockedIds))
    ).apply(instance, MailManager::resolve));

    private final Map<MailSource, MailList> messages;
    private final Set<UUID> blockedIds;

    private static MailManager resolve(List<MailList> chats, List<UUID> blockedIds) {
        MailManager result = new MailManager();
        for (MailList chat : chats) {
            result.messages.put(chat.getSource(), chat);
        }
        result.blockedIds.addAll(blockedIds);
        return result;
    }

    public MailManager() {
        this.messages = new HashMap<>();
        this.blockedIds = new HashSet<>();
    }

    public void sendMessage(MailSource source, MailMessage message) {
        this.getChat(source).send(message);
    }

    public void block(UUID id) {
        this.blockedIds.add(id);
    }

    public void unblock(UUID id) {
        this.blockedIds.remove(id);
    }

    public boolean isBlocked(MailSource source) {
        return !source.isSystemChat() && this.blockedIds.contains(source.getSourceId());
    }

    public void receiveMessage(MailSource source, MailMessage message) {
        if (!this.isBlocked(source)) {
            this.getChat(source).receive(message);
        }
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

    public MailList deleteChat(MailSource source) {
        return this.messages.remove(source);
    }

    public MailSource getSender(UUID id) {
        for (MailSource src : this.messages.keySet()) {
            if (src.getSourceId().equals(id)) {
                return src;
            }
        }
        return null;
    }

    public boolean hasChat(MailSource source) {
        return this.messages.containsKey(source);
    }

    public boolean hasAttachments(MailSource source) {
        if (!this.messages.containsKey(source)) {
            return false;
        }
        MailList chat = this.getChat(source);
        return chat.hasAttachments();
    }

    @Override
    public CompoundTag serialize(HolderLookup.Provider provider) {
        return Codecs.serialize(CODEC, this);
    }

    @Override
    public void deserialize(CompoundTag tag, HolderLookup.Provider provider) {
        MailManager resolved = Codecs.deserialize(CODEC, tag);
        this.messages.clear();
        this.blockedIds.clear();
        this.messages.putAll(resolved.messages);
        this.blockedIds.addAll(resolved.blockedIds);
    }
}
