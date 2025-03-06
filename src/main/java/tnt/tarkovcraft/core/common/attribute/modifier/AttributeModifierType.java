package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

public record AttributeModifierType<A extends AttributeModifier>(ResourceLocation identifier, MapCodec<A> codec) {

    public static final Codec<AttributeModifier> ID_CODEC = TarkovCraftRegistries.ATTRIBUTE_MODIFIER.byNameCodec().dispatch(AttributeModifier::getType, AttributeModifierType::codec);
}
