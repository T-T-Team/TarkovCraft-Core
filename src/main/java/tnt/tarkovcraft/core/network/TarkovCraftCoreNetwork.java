package tnt.tarkovcraft.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;
import tnt.tarkovcraft.core.network.message.mail.C2S_MailBlockUser;
import tnt.tarkovcraft.core.network.message.mail.C2S_MailCreateChat;
import tnt.tarkovcraft.core.network.message.mail.C2S_MailDeleteChat;
import tnt.tarkovcraft.core.network.message.mail.C2S_MailSendMessage;

import java.util.Locale;

public final class TarkovCraftCoreNetwork {

    public static final int VERSION = 1;
    public static final String NETWORK_ID = "TarkovCraftCoreNetwork@" + VERSION;

    public static ResourceLocation createId(Class<? extends CustomPacketPayload> type) {
        String name = type.getSimpleName().toLowerCase(Locale.ROOT);
        return TarkovCraftCore.createResourceLocation("net/" + name);
    }

    public static void onRegistration(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registry = event.registrar(NETWORK_ID).executesOn(HandlerThread.MAIN);

        registerMailHandlers(registry);

        registry.playToClient(S2C_SendDataAttachments.TYPE, S2C_SendDataAttachments.CODEC, S2C_SendDataAttachments::handleMessage);
    }

    private static void registerMailHandlers(PayloadRegistrar registry) {
        registry.playToServer(C2S_MailSendMessage.TYPE, C2S_MailSendMessage.CODEC, C2S_MailSendMessage::handleMessage);
        registry.playToServer(C2S_MailCreateChat.TYPE, C2S_MailCreateChat.CODEC, C2S_MailCreateChat::handleMessage);
        registry.playToServer(C2S_MailDeleteChat.TYPE, C2S_MailDeleteChat.CODEC, C2S_MailDeleteChat::handleMessage);
        registry.playToServer(C2S_MailBlockUser.TYPE, C2S_MailBlockUser.CODEC, C2S_MailBlockUser::handleMessage);
    }
}
