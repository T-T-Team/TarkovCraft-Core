package tnt.tarkovcraft.core.common.mail;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.TarkovCraftCoreConfig;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

public final class MailSystem {

    public static boolean isEnabled() {
        return TarkovCraftCore.getConfig().enableMailSystem;
    }

    public static boolean isCommandAllowed(CommandSourceStack stack) {
        TarkovCraftCoreConfig config = TarkovCraftCore.getConfig();
        if (!config.enableMailSystem)
            return false;
        ServerPlayer player = stack.getPlayer();
        return player == null || config.allowMailPlayerMessages;
    }

    public static void sendMessage(Player target, MailSource source, MailMessage message) {
        Level level = target.level();
        if (level.isClientSide() || message.isBlank())
            return;
        if (!isEnabled()) {
            TarkovCraftCore.LOGGER.warn("Attempted to send mail to player {} while having disabled mail system! No message will be sent", target);
            return;
        }
        if (!TarkovCraftCore.getConfig().allowMailPlayerMessages && !source.isSystemChat()) {
            TarkovCraftCore.LOGGER.warn("Player {} tried to send chat message to {} while chat messages were disabled!", source.getName().getString(), target.getDisplayName().getString());
            return;
        }
        if (message.isExpired()) {
            TarkovCraftCore.LOGGER.warn("Attempted to send expired message to {}", target);
            return;
        }
        if (source.is(target)) {
            TarkovCraftCore.LOGGER.warn("Player {} attempted to send message to themselves. Ignoring request", target);
            return;
        }
        // add the message to the source player
        if (!source.isSystemChat()) {
            ServerPlayer sender = level.getServer().getPlayerList().getPlayer(source.getSourceId());
            if (sender == null) {
                TarkovCraftCore.LOGGER.error("Failed to send message due to missing sender playerID {}", source.getSourceId());
                return;
            }
            MailSource targetChat = MailSource.player(target);
            sender.getData(BaseDataAttachments.MAIL_MANAGER).sendMessage(targetChat, message);
            PacketDistributor.sendToPlayer(sender, new S2C_SendDataAttachments(sender, BaseDataAttachments.MAIL_MANAGER.get()));
        }
        // and send the message to the target
        MailManager targetManager = target.getData(BaseDataAttachments.MAIL_MANAGER);
        if (!targetManager.isBlocked(source)) {
            targetManager.receiveMessage(source, message);
            PacketDistributor.sendToPlayer((ServerPlayer) target, new S2C_SendDataAttachments(target, BaseDataAttachments.MAIL_MANAGER.get()));
        }
    }
}
