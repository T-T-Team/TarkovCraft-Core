package tnt.tarkovcraft.core.api.client;

import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.TarkovCraftCore;

public interface SynchronizableScreen {

    DataSource STATISTICS = new DataSource(TarkovCraftCore.createIdentifier("statistics"));
    DataSource SKILLS = new DataSource(TarkovCraftCore.createIdentifier("skills"));

    /**
     * Triggers when any compatible data source is synchronized to a client player
     * @param source Which trigger source caused this sync
     */
    void sync(DataSource source);

    record DataSource(ResourceLocation identifier) {
    }
}
