package tnt.tarkovcraft.core.common.mail;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;

public final class MailSystem {

    public static boolean isEnabled() {
        return TarkovCraftCore.getConfig().enableMailSystem;
    }

    public static void sendMessage(Player player, MailSource source, MailMessage message) {
        Level level = player.level();
        if (level.isClientSide())
            return;
        if (!isEnabled()) {
            TarkovCraftCore.LOGGER.warn("Attempted to send mail to player {} while having disabled mail system! No message will be sent", player);
            return;
        }
        if (message.isExpired()) {
            TarkovCraftCore.LOGGER.warn("Attempted to send expired message to {}", player);
            return;
        }
        player.getData(BaseDataAttachments.MAIL_LIST).addMessage(source, message);
    }
}
