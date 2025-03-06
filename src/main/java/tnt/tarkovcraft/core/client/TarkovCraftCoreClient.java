package tnt.tarkovcraft.core.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.event.RegisterTradeResourceRendererEvent;
import tnt.tarkovcraft.core.client.render.TradeResourceRenderManager;

@EventBusSubscriber(modid = TarkovCraftCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TarkovCraftCoreClient {

    private static final TarkovCraftCoreClient INSTANCE = new TarkovCraftCoreClient();

    public final TradeResourceRenderManager resourceRenderManager = new TradeResourceRenderManager();

    private TarkovCraftCoreClient() {}

    public void onSetup(FMLClientSetupEvent event) {
        this.dispatchParallelRegistryEvents();
    }

    private void dispatchParallelRegistryEvents() {
        ModLoader.postEvent(new RegisterTradeResourceRendererEvent());
    }

    public static TarkovCraftCoreClient getClient() {
        return INSTANCE;
    }
}
