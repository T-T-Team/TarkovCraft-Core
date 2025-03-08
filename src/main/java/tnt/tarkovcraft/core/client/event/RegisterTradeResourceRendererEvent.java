package tnt.tarkovcraft.core.client.event;

import net.neoforged.fml.event.IModBusEvent;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.client.render.TradeResourceRenderManager;

public class RegisterTradeResourceRendererEvent extends AbstractClientRegistryEvent<TradeResourceRenderManager> implements IModBusEvent {

    public RegisterTradeResourceRendererEvent() {
        super(TarkovCraftCoreClient.RESOURCE_RENDER_MANAGER, TradeResourceRenderManager::getLock);
    }
}
