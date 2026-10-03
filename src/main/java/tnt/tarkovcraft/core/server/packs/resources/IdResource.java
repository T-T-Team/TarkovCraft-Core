package tnt.tarkovcraft.core.server.packs.resources;

import net.minecraft.resources.Identifier;

import java.util.Objects;

public record IdResource<T>(Identifier identifier, T element) {

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IdResource<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
