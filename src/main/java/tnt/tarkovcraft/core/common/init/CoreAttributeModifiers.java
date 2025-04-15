package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.modifier.*;

import java.util.function.Supplier;

public final class CoreAttributeModifiers {

    public static final DeferredRegister<AttributeModifierType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.ATTRIBUTE_MODIFIER, TarkovCraftCore.MOD_ID);

    // Value types
    public static final Supplier<AttributeModifierType<SetValueAttributeModifier>> SET_VALUE = REGISTRY.register("set", k -> new AttributeModifierType<>(k, SetValueAttributeModifier.CODEC));
    public static final Supplier<AttributeModifierType<AddValueModifier>> ADD_VALUE = REGISTRY.register("add", k -> new AttributeModifierType<>(k, AddValueModifier.CODEC));
    public static final Supplier<AttributeModifierType<MultiplyValueAttributeModifier>> MUL_VALUE = REGISTRY.register("multiply", k -> new AttributeModifierType<>(k, MultiplyValueAttributeModifier.CODEC));

    // Extension types
    public static final Supplier<AttributeModifierType<ExpiringAttributeModifier>> EXPIRING = REGISTRY.register("expiring", k -> new AttributeModifierType<>(k, ExpiringAttributeModifier.CODEC));
}
