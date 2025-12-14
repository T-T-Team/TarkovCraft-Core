package tnt.tarkovcraft.core.common.item;

import net.minecraft.resources.Identifier;

import java.util.Objects;

public record CurrencyType(Identifier identifier, int stackLimit, int displayColor) {

    public CurrencyType(Identifier identifier, int stackLimit) {
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
