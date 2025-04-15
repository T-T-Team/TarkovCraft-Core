package tnt.tarkovcraft.core.common.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.common.init.CoreMailMessageAttachments;
import tnt.tarkovcraft.core.util.helper.PlayerHelper;

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
    public boolean isClaimable(MailMessage message, UUID attachmentId, Player player) {
        return !this.claimed && !this.itemStack.isEmpty();
    }

    @Override
    public boolean claim(MailMessage message, UUID attachmentId, Player player) {
        if (this.claimed) {
            return false;
        }
        PlayerHelper.giveItem(this.itemStack.copy(), player);
        this.claimed = true;
        return true;
    }

    @Override
    public String toString() {
        return this.getType().identifier() + "[item=" + this.itemStack + ", claimed=" + this.claimed + "]";
    }

    @Override
    public MailMessageAttachmentType<?> getType() {
        return CoreMailMessageAttachments.ITEM.get();
    }
}
