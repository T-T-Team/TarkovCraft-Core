package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;

public record AttributeModifierType<A extends AttributeModifier>(ResourceLocation identifier, MapCodec<A> codec) {

    public static final Codec<AttributeModifier> ID_CODEC = CoreRegistries.ATTRIBUTE_MODIFIER.byNameCodec().dispatch(AttributeModifier::getType, AttributeModifierType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AttributeModifierType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
