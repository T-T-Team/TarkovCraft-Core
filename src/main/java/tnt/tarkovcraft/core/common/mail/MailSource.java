package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.util.CommonLabels;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MailSource {

    public static final Codec<MailSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("sourceId").forGetter(t -> t.sourceId),
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(t -> Optional.ofNullable(t.icon)),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(t -> t.name),
            Codec.BOOL.fieldOf("systemChat").forGetter(t -> t.systemChat)
    ).apply(instance, MailSource::new));
    public static final MailSource SYSTEM = createSystem(MailMessage.SYSTEM_ID, TarkovCraftCore.createResourceLocation("textures/mail/system.png"));
    private final UUID sourceId;
    private final Component name;
    private final ResourceLocation icon;
    private final boolean systemChat;

    private MailSource(UUID sourceId, Optional<ResourceLocation> icon, Component name, boolean systemChat) {
        this.sourceId = sourceId;
        this.icon = icon.orElse(null);
        this.name = name;
        this.systemChat = systemChat;
    }

    public static MailSource createSystem(UUID sourceId, @Nullable ResourceLocation icon) {
        return new MailSource(sourceId, Optional.ofNullable(icon), CommonLabels.SYSTEM, true);
    }

    public static MailSource player(Player player) {
        return new MailSource(player.getUUID(), Optional.empty(), player.getDisplayName(), false);
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public Component getName() {
        return name;
    }

    public boolean isSystemChat() {
        return systemChat;
    }

    public boolean isChatAllowed() {
        return !this.systemChat; // TODO global config toggle
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MailSource source)) return false;
        return Objects.equals(sourceId, source.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sourceId);
    }
}
