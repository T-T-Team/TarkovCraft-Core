package tnt.tarkovcraft.core.common.attribute;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public final class Attribute {

    private final Identifier identifier;
    private final double baseValue;

    private Attribute(Identifier identifier, double baseValue) {
        this.identifier = identifier;
        this.baseValue = baseValue;
    }

    public AttributeInstance createInstance(Entity holder) {
        return new AttributeInstance(this, holder);
    }

    public static Attribute create(Identifier identifier, double baseValue) {
        return new Attribute(identifier, baseValue);
    }

    public static Attribute createBool(Identifier identifier, boolean value) {
        return create(identifier, value ? 1 : 0);
    }

    public static Attribute trueBool(Identifier identifier) {
        return createBool(identifier, true);
    }

    public static Attribute falseBool(Identifier identifier) {
        return createBool(identifier, false);
    }

    public static Attribute create(Identifier identifier) {
        return create(identifier, 0);
    }

    public Identifier identifier() {
        return identifier;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public Component getDisplayName() {
        return Component.translatable(this.identifier.toLanguageKey("attribute"));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Attribute attribute)) return false;
        return Objects.equals(identifier, attribute.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "identifier=" + identifier +
                ", baseValue=" + baseValue +
                '}';
    }
}
