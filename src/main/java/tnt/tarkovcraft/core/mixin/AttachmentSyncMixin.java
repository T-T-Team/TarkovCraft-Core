package tnt.tarkovcraft.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentSync;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.api.AttachmentSyncCallbackListener;

import java.util.List;

@Mixin(AttachmentSync.class)
public abstract class AttachmentSyncMixin {

    @Inject(
            method = "receiveSyncedDataAttachments",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER)
    )
    @SuppressWarnings("unchecked")
    private static <T> void tarkovCraftCore$receiveSyncedDataAttachments(AttachmentHolder holder, RegistryAccess registryAccess, List<AttachmentType<?>> types, byte[] data, CallbackInfo ci,
                                                                         @Local(name = "type") AttachmentType<?> type, @Local(name = "result") Object result, @Local(name = "syncHandler") AttachmentSyncHandler<?> syncHandler) {
        AttachmentType<T> typed = (AttachmentType<T>) type;
        T attachment = (T) result;
        if (syncHandler instanceof AttachmentSyncCallbackListener<?> callbackListener) {
            ((AttachmentSyncCallbackListener<T>) callbackListener).onDataSynced(holder, typed, attachment);
        }
    }
}
