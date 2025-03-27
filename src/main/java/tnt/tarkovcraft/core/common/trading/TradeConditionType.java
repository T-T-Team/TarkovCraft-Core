package tnt.tarkovcraft.core.common.trading;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record TradeConditionType<TC extends TradeCondition>(ResourceLocation identifier, MapCodec<TC> codec) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TradeConditionType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
