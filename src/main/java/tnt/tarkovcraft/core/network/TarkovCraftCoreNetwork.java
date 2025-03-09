package tnt.tarkovcraft.core.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;
import tnt.tarkovcraft.core.network.message.mail.C2S_MailCreateChat;
import tnt.tarkovcraft.core.network.message.mail.C2S_MailSendMessage;

public final class TarkovCraftCoreNetwork {

    public static final int VERSION = 1;
    public static final String NETWORK_ID = "TarkovCraftCoreNetwork@" + VERSION;

    public static void onRegistration(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registry = event.registrar(NETWORK_ID).executesOn(HandlerThread.MAIN);

        // c2s
        registry.playToServer(C2S_MailSendMessage.TYPE, C2S_MailSendMessage.CODEC, C2S_MailSendMessage::handleMessage);
        registry.playToServer(C2S_MailCreateChat.TYPE, C2S_MailCreateChat.CODEC, C2S_MailCreateChat::handleMessage);

        // s2c
        registry.playToClient(S2C_SendDataAttachments.TYPE, S2C_SendDataAttachments.CODEC, S2C_SendDataAttachments::handleMessage);
    }
}
