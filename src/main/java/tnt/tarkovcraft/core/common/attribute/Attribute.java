package tnt.tarkovcraft.core.common.attribute;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class Attribute {

    private final ResourceLocation identifier;
    private final double baseValue;

    private Attribute(ResourceLocation identifier, double baseValue) {
        this.identifier = identifier;
        this.baseValue = baseValue;
    }

    public AttributeInstance createInstance() {
        return new AttributeInstance(this);
    }

    public static Attribute create(ResourceLocation identifier, double baseValue) {
        return new Attribute(identifier, baseValue);
    }

    public static Attribute createBool(ResourceLocation identifier, boolean value) {
        return create(identifier, value ? 1 : 0);
    }

    public static Attribute trueBool(ResourceLocation identifier) {
        return createBool(identifier, true);
    }

    public static Attribute falseBool(ResourceLocation identifier) {
        return createBool(identifier, false);
    }

    public static Attribute create(ResourceLocation identifier) {
        return create(identifier, 0);
    }

    public ResourceLocation identifier() {
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
