package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.data.number.*;

import java.util.function.Supplier;

public final class CoreNumberProviders {

    public static final DeferredRegister<NumberProviderType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.NUMBER_PROVIDER, TarkovCraftCore.MOD_ID);

    public static final Supplier<NumberProviderType<ConstantNumberProvider>> CONSTANT = REGISTRY.register("constant", key -> new NumberProviderType<>(key, ConstantNumberProvider.CODEC));
    public static final Supplier<NumberProviderType<RangedNumberProvider>> RANGED = REGISTRY.register("ranged", key -> new NumberProviderType<>(key, RangedNumberProvider.CODEC));
    public static final Supplier<NumberProviderType<DurationNumberProvider>> DURATION = REGISTRY.register("duration", key -> new NumberProviderType<>(key, DurationNumberProvider.CODEC));
    public static final Supplier<NumberProviderType<ConfigurationNumberProvider>> CONFIG = REGISTRY.register("config", key -> new NumberProviderType<>(key, ConfigurationNumberProvider.CODEC));
}
