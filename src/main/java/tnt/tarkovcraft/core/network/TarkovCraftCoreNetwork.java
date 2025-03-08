package tnt.tarkovcraft.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.function.BiConsumer;

public final class TarkovCraftCoreNetwork {

    public static final int VERSION = 1;
    public static final String NETWORK_ID = "TarkovCraftCoreNetwork@" + VERSION;

    public static void onRegistration(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registry = event.registrar(NETWORK_ID);

        // c2s

        // s2c
        registry.playToClient(S2C_SendDataAttachments.TYPE, S2C_SendDataAttachments.CODEC, handleMainThread(S2C_SendDataAttachments::handleMessage));
    }

    public static <T extends CustomPacketPayload> IPayloadHandler<T> handleMainThread(BiConsumer<T, IPayloadContext> ctxConsumer) {
        return (payload, context) -> context.enqueueWork(() -> ctxConsumer.accept(payload, context));
    }
}
