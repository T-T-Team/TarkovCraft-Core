package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.common.init.BaseMailMessageAttachments;

import java.util.UUID;

public class MailMessageItemAttachment implements MailMessageAttachment {

    public static final MapCodec<MailMessageItemAttachment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(t -> t.itemStack),
            Codec.BOOL.optionalFieldOf("claimed", false).forGetter(t -> t.claimed)
    ).apply(instance, MailMessageItemAttachment::new));

    private final ItemStack itemStack;
    private boolean claimed;

    private MailMessageItemAttachment(ItemStack itemStack, boolean claimed) {
        this.itemStack = itemStack;
        this.claimed = claimed;
    }

    public static MailMessageItemAttachment item(ItemStack itemStack) {
        return new MailMessageItemAttachment(itemStack, false);
    }

    @Override
    public boolean canOpen(MailMessage message, UUID attachmentId, Player player) {
        return !this.claimed && !this.itemStack.isEmpty();
    }

    @Override
    public void open(MailMessage message, UUID attachmentId, Player player) {
        // TODO open claim menu
    }

    @Override
    public MailMessageAttachmentType<?> getType() {
        return BaseMailMessageAttachments.ITEM.get();
    }
}
