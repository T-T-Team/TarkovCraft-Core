package tnt.tarkovcraft.core.common.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.util.context.OperationContext;

import java.util.*;
import java.util.function.Consumer;

public final class AttributeInstance {

    public static final Codec<AttributeInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TarkovCraftRegistries.ATTRIBUTE.byNameCodec().fieldOf("attribute").forGetter(AttributeInstance::getAttribute),
            Codec.unboundedMap(
                    UUIDUtil.STRING_CODEC,
                    AttributeModifier.CODEC
            ).fieldOf("modifiers").forGetter(t -> t.modifiers)
    ).apply(instance, AttributeInstance::new));

    private final Attribute attribute;
    private final Map<UUID, AttributeModifier> modifiers;
    private final List<AttributeListener> listeners;
    private double value;
    private boolean changed;

    AttributeInstance(Attribute attribute) {
        this.attribute = attribute;
        this.modifiers = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.value = this.attribute.getBaseValue();
        this.changed = true;
    }

    AttributeInstance(Attribute attribute, Map<UUID, AttributeModifier> modifiers) {
        this.attribute = attribute;
        this.modifiers = new HashMap<>(modifiers);
        this.listeners = new ArrayList<>();
        this.value = this.attribute.getBaseValue();
        this.changed = true;
    }

    public void update(OperationContext context) {
        Iterator<AttributeModifier> iterator = this.modifiers.values().iterator();
        while (iterator.hasNext()) {
            AttributeModifier modifier = iterator.next();
            boolean shouldRemoveModifier = modifier.onCancellationTick(context);
            if (shouldRemoveModifier) {
                iterator.remove();
                this.invokeEvent(t -> t.onAttributeModifierRemoved(this.attribute, modifier));
                this.setChanged();
            }
        }
    }

    public void addModifier(AttributeModifier modifier) {
        this.modifiers.put(modifier.identifier(), modifier);
        this.invokeEvent(t -> t.onAttributeModifierAdded(this.attribute, modifier));
        this.setChanged();
    }

    public void removeModifier(AttributeModifier modifier) {
        this.removeModifier(modifier.identifier());
    }

    public void removeModifier(UUID identifier) {
        AttributeModifier modifier = this.modifiers.remove(identifier);
        if (modifier != null) {
            this.invokeEvent(t -> t.onAttributeModifierRemoved(this.attribute, modifier));
            this.setChanged();
        }
    }

    public void removeModifiers() {
        List<AttributeModifier> modifiers = new ArrayList<>(this.modifiers.values());
        this.modifiers.clear();
        modifiers.forEach(mod -> this.invokeEvent(t -> t.onAttributeModifierRemoved(this.attribute, mod)));
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

    public int getActiveListenerCount() {
        return this.listeners.size();
    }

    public Map<UUID, AttributeModifier> listModifiers() {
        return this.modifiers;
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

    public void setChanged() {
        if (!this.changed) {
            this.changed = true;
            this.invokeEvent(t -> t.onAttributeSetChanged(this.attribute));
        }
    }

    public void invokeEvent(Consumer<AttributeListener> event) {
        this.listeners.forEach(event);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    private void refreshValue() {
        double result = this.attribute.getBaseValue();
        List<AttributeModifier> modifierList = new ArrayList<>(this.modifiers.values());
        modifierList.sort(Comparator.comparingInt(AttributeModifier::ordering));
        for (AttributeModifier modifier : modifierList) {
            result = modifier.applyModifierOn(result);
        }
        if (this.value != result) {
            double oldValue = this.value;
            this.value = result;
            this.invokeEvent(t -> t.onAttributeValueChanged(this.attribute, oldValue));
        }
    }
}
