package tnt.tarkovcraft.core.common.mail;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.TarkovCraftCoreConfig;
import tnt.tarkovcraft.core.api.event.MailEvent;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

public final class MailSystem {

    public static final Marker MARKER = MarkerManager.getMarker("MailSystem");
    public static final Component FAILED_TO_SEND_MESSAGE = Component.translatable("label.tarkovcraft_core.mail.failed_to_send_message");

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
            TarkovCraftCore.LOGGER.warn(MARKER, "Attempted to send mail to player {} while having disabled mail system! No message will be sent", target);
            return;
        }
        if (!TarkovCraftCore.getConfig().allowMailPlayerMessages && !source.isSystemChat()) {
            TarkovCraftCore.LOGGER.warn(MARKER, "Player {} tried to send chat message to {} while chat messages were disabled!", source.getName().getString(), target.getDisplayName().getString());
            return;
        }
        if (message.isExpired()) {
            TarkovCraftCore.LOGGER.warn(MARKER, "Attempted to send expired message to {}", target);
            return;
        }
        if (source.is(target)) {
            TarkovCraftCore.LOGGER.warn(MARKER, "Player {} attempted to send message to themselves. Ignoring request", target);
            return;
        }
        MailSource targetChat = MailSource.player(target);
        if (canSendMessage(level, message, source, targetChat)) {
            // add the message to the source player
            if (!source.isSystemChat()) {
                ServerPlayer sender = level.getServer().getPlayerList().getPlayer(source.getSourceId());
                if (sender == null) {
                    TarkovCraftCore.LOGGER.error(MARKER, "Failed to send message due to missing sender playerID {}", source.getSourceId());
                    return;
                }
                sender.getData(CoreDataAttachments.MAIL_MANAGER).sendMessage(targetChat, message);
                PacketDistributor.sendToPlayer(sender, new S2C_SendDataAttachments(sender, CoreDataAttachments.MAIL_MANAGER.get()));
            }
            // and send the message to the target
            MailManager targetManager = target.getData(CoreDataAttachments.MAIL_MANAGER);
            boolean isBlocked = targetManager.isBlocked(source);
            NeoForge.EVENT_BUS.post(new MailEvent.OnMailReceiveAttempt(message, source, targetChat, targetManager, level, isBlocked));
            if (!isBlocked) {
                targetManager.receiveMessage(source, message);
                PacketDistributor.sendToPlayer((ServerPlayer) target, new S2C_SendDataAttachments(target, CoreDataAttachments.MAIL_MANAGER.get()));
            }
        }
    }

    public static boolean canSendMessage(Level level, MailMessage message, MailSource source, MailSource destination) {
        MailEvent.MailSendingEvent event = NeoForge.EVENT_BUS.post(new MailEvent.MailSendingEvent(level, message, source, destination));
        return !event.isCanceled();
    }
}
