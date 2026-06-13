package tnt.tarkovcraft.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.network.message.C2S_ItemLeftClicked;
import tnt.tarkovcraft.core.network.message.S2C_MakeParticles;
import tnt.tarkovcraft.core.network.message.notification.S2C_SendNotification;

import java.util.Locale;

public final class TarkovCraftCoreNetwork {

    public static final int VERSION = 1;
    public static final String NETWORK_ID = "TarkovCraftCoreNetwork@" + VERSION;

    public static ResourceLocation createId(Class<? extends CustomPacketPayload> type) {
        String name = type.getSimpleName().toLowerCase(Locale.ROOT);
        return TarkovCraftCore.createIdentifier("net/" + name);
    }

    public static void onRegistration(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registry = event.registrar(NETWORK_ID).executesOn(HandlerThread.MAIN);

        registry.playToClient(S2C_MakeParticles.TYPE, S2C_MakeParticles.CODEC, S2C_MakeParticles::handleMessage);
        registry.playToClient(S2C_SendNotification.TYPE, S2C_SendNotification.CODEC, S2C_SendNotification::handleMessage);

        registry.playToServer(C2S_ItemLeftClicked.TYPE, C2S_ItemLeftClicked.CODEC, C2S_ItemLeftClicked::handleMessage);
    }
}
