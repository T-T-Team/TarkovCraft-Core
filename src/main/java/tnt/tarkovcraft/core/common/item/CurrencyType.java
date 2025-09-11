package tnt.tarkovcraft.core.common.item;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record CurrencyType(ResourceLocation identifier, int stackLimit, int displayColor) {

    public CurrencyType(ResourceLocation identifier, int stackLimit) {
        this(identifier, stackLimit, 0xFF00DD00);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CurrencyType that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
