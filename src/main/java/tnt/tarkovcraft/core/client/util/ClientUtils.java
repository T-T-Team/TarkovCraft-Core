package tnt.tarkovcraft.core.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;

import java.util.Objects;

public final class ClientUtils {

    public static RegistryAccess getClientRegistryAccess() {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener connection = mc.getConnection();
        return Objects.requireNonNull(connection).registryAccess();
    }
}
