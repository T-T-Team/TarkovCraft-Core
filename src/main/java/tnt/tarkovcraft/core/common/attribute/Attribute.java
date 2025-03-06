package tnt.tarkovcraft.core.common.attribute;

import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.util.context.OperationContext;

import java.util.*;
import java.util.function.Consumer;

public final class Attribute {

    private final ResourceLocation identifier;
    private final double baseValue;
    private double value;
    private boolean changed = true; // require recalculation for first get
    private final Map<UUID, AttributeModifier> modifiers;
    private final List<AttributeListener> listeners;

    private Attribute(ResourceLocation identifier, double baseValue) {
        this.identifier = identifier;
        this.baseValue = baseValue;
        this.value = baseValue;
        this.modifiers = new HashMap<>();
        this.listeners = new ArrayList<>();
    }

    public static Attribute create(ResourceLocation identifier, double baseValue) {
        return new Attribute(identifier, baseValue);
    }

    public static Attribute createBool(ResourceLocation identifier, boolean value) {
        return create(identifier, value ? 1 : 0);
    }

    public static Attribute create(ResourceLocation identifier) {
        return create(identifier, 0);
    }

    public void update(OperationContext context) {
        Iterator<AttributeModifier> iterator = this.modifiers.values().iterator();
        while (iterator.hasNext()) {
            AttributeModifier modifier = iterator.next();
            boolean shouldRemoveModifier = modifier.onCancellationTick(context);
            if (shouldRemoveModifier) {
                iterator.remove();
                this.invokeEvent(t -> t.onAttributeModifierRemoved(this, modifier));
                this.setChanged();
            }
        }
    }

    public void addModifier(AttributeModifier modifier) {
        this.modifiers.put(modifier.identifier(), modifier);
        this.invokeEvent(t -> t.onAttributeModifierAdded(this, modifier));
        this.setChanged();
    }

    public void removeModifier(AttributeModifier modifier) {
        this.removeModifier(modifier.identifier());
    }

    public void removeModifier(UUID identifier) {
        AttributeModifier modifier = this.modifiers.remove(identifier);
        if (modifier != null) {
            this.invokeEvent(t -> t.onAttributeModifierRemoved(this, modifier));
            this.setChanged();
        }
    }

    public void removeModifiers() {
        List<AttributeModifier> modifiers = new ArrayList<>(this.modifiers.values());
        this.modifiers.clear();
        modifiers.forEach(mod -> this.invokeEvent(t -> t.onAttributeModifierRemoved(this, mod)));
        this.setChanged();
    }

    public boolean hasModifier(UUID identifier) {
        return this.modifiers.containsKey(identifier);
    }

    public boolean hasModifier(AttributeModifier modifier) {
        return this.hasModifier(modifier.identifier());
    }

    public void addListener(AttributeListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(AttributeListener listener) {
        this.listeners.remove(listener);
    }

    public ResourceLocation identifier() {
        return identifier;
    }

    public double value() {
        if (this.changed) {
            this.refreshValue();
            this.changed = false;
        }
        return this.value;
    }

    public float floatValue() {
        return (float) this.value();
    }

    public int intValue() {
        return (int) this.value();
    }

    public boolean booleanValue() {
        return this.value() != 0;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setChanged() {
        if (!this.changed) {
            this.changed = true;
            this.invokeEvent(t -> t.onAttributeSetChanged(this));
        }
    }

    public void invokeEvent(Consumer<AttributeListener> event) {
        this.listeners.forEach(event);
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
                ", value=" + value +
                ", modifiers=" + modifiers.size() +
                '}';
    }

    private void refreshValue() {
        double result = this.getBaseValue();
        List<AttributeModifier> modifierList = new ArrayList<>(this.modifiers.values());
        modifierList.sort(Comparator.comparingInt(AttributeModifier::ordering));
        for (AttributeModifier modifier : modifierList) {
            result = modifier.applyModifierOn(result);
        }
        if (this.value != result) {
            double oldValue = this.value;
            this.value = result;
            this.invokeEvent(t -> t.onAttributeValueChanged(this, oldValue));
        }
    }
}
