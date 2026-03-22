package tnt.tarkovcraft.core.common.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.util.EventHandler;

import java.util.*;

public final class AttributeInstance {

    public static final Codec<AttributeInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreRegistries.ATTRIBUTE.byNameCodec().fieldOf("attribute").forGetter(AttributeInstance::getAttribute),
            Codec.unboundedMap(
                    ResourceLocation.CODEC,
                    AttributeModifier.CODEC
            ).fieldOf("modifiers").forGetter(t -> t.modifiers)
    ).apply(instance, AttributeInstance::new));

    private Entity holder;
    private final Attribute attribute;
    private final Map<ResourceLocation, AttributeModifier> modifiers;
    private final EventHandler<AttributeListener> eventHandler;
    private double value;
    private boolean changed;

    AttributeInstance(Attribute attribute, Entity holder) {
        this.attribute = attribute;
        this.holder = holder;
        this.modifiers = new HashMap<>();
        this.eventHandler = EventHandler.create();
        this.value = this.attribute.getBaseValue();
        this.changed = true;
    }

    AttributeInstance(Attribute attribute, Map<ResourceLocation, AttributeModifier> modifiers) {
        this.attribute = attribute;
        this.modifiers = new HashMap<>(modifiers);
        this.eventHandler = EventHandler.create();
        this.value = this.attribute.getBaseValue();
        this.changed = true;
    }

    public void setHolder(Entity holder) {
        this.holder = holder;
    }

    public Entity getHolder() {
        return holder;
    }

    public void addModifier(AttributeModifier modifier) {
        this.modifiers.put(modifier.identifier(), modifier);
        this.eventHandler.dispatch(t -> t.onAttributeModifierAdded(this, modifier));
        this.setChanged();
    }

    public void removeModifier(AttributeModifier modifier) {
        this.removeModifier(modifier.identifier());
    }

    public void removeModifier(ResourceLocation identifier) {
        AttributeModifier modifier = this.modifiers.remove(identifier);
        if (modifier != null) {
            this.eventHandler.dispatch(t -> t.onAttributeModifierRemoved(this, modifier));
            this.setChanged();
        }
    }

    public void removeModifiers() {
        List<AttributeModifier> modifiers = new ArrayList<>(this.modifiers.values());
        this.modifiers.clear();
        modifiers.forEach(mod -> this.eventHandler.dispatch(t -> t.onAttributeModifierRemoved(this, mod)));
        this.setChanged();
    }

    public boolean hasModifier(ResourceLocation identifier) {
        return this.modifiers.containsKey(identifier);
    }

    public boolean hasModifier(AttributeModifier modifier) {
        return this.hasModifier(modifier.identifier());
    }

    public void addListener(AttributeListener listener) {
        this.eventHandler.subscribe(listener);
    }

    public void removeListener(AttributeListener listener) {
        this.eventHandler.unsubscribe(listener);
    }

    public int getActiveListenerCount() {
        return this.eventHandler.subscriberCount();
    }

    public Map<ResourceLocation, AttributeModifier> listModifiers() {
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
            this.eventHandler.dispatch(t -> t.onAttributeSetChanged(this));
        }
    }

    public Attribute getAttribute() {
        return attribute;
    }

    private void refreshValue() {
        double result = this.attribute.getBaseValue();
        List<AttributeModifier> modifierList = new ArrayList<>(this.modifiers.values());
        modifierList.sort(Comparator.comparingInt(AttributeModifier::ordering));
        for (AttributeModifier modifier : modifierList) {
            result = modifier.calculateValue(this, result);
        }
        if (this.value != result) {
            double oldValue = this.value;
            this.value = result;
            this.eventHandler.dispatch(t -> t.onAttributeValueChanged(this, oldValue));
        }
    }
}
