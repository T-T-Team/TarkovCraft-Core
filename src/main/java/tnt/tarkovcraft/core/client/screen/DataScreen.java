package tnt.tarkovcraft.core.client.screen;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import tnt.tarkovcraft.core.network.Synchronizable;

public interface DataScreen {

    default void onAttachmentDataReceived(Entity entity, AttachmentType<?> attachmentType, Synchronizable<?> data) {}
}
