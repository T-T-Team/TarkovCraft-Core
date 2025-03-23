package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;

import java.util.function.Supplier;

public final class BaseAttributes {

    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.ATTRIBUTE, TarkovCraftCore.MOD_ID);

    public static final Supplier<Attribute> SPRINT = REGISTRY.register("sprint", Attribute::trueBool);
}
