package tnt.tarkovcraft.core.common.mail;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.util.UUID;

public final class MailSystem {

    public static boolean isEnabled() {
        return TarkovCraftCore.getConfig().enableMailSystem;
    }

    public static void sendMessage(Player player, MailMessage message) {
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
        // TODO actually send the message
    }

    public static void sendMessage(Level level, UUID target, MailMessage message) {
        if (level.isClientSide())
            return;
        MinecraftServer server = level.getServer();
        ServerPlayer player = server.getPlayerList().getPlayer(target);
        if (player == null) {
            TarkovCraftCore.LOGGER.warn("Attempted to send mail message to unknown playerId {}", target);
            return;
        }
        sendMessage(player, message);
    }
}
