package tnt.tarkovcraft.core.api.event;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

import java.util.function.BiConsumer;

public class RegisterWeightProvidersEvent extends Event implements IModBusEvent {

    private final BiConsumer<Identifier, WeightProvider> registration;

    public RegisterWeightProvidersEvent(BiConsumer<Identifier, WeightProvider> registration) {
        this.registration = registration;
    }

    public void register(Identifier identifier, WeightProvider provider) {
        this.registration.accept(identifier, provider);
    }
}
