package tnt.tarkovcraft.core.api.event;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

import java.util.function.BiConsumer;

public class RegisterWeightProvidersEvent extends Event implements IModBusEvent {

    private final BiConsumer<ResourceLocation, WeightProvider> registration;

    public RegisterWeightProvidersEvent(BiConsumer<ResourceLocation, WeightProvider> registration) {
        this.registration = registration;
    }

    public void register(ResourceLocation identifier, WeightProvider provider) {
        this.registration.accept(identifier, provider);
    }
}
