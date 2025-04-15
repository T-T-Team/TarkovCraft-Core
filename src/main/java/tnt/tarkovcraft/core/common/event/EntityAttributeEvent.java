package tnt.tarkovcraft.core.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;

import javax.annotation.Nullable;

public abstract class EntityAttributeEvent extends Event {

    private final EntityAttributeData attributes;

    public EntityAttributeEvent(EntityAttributeData attributes) {
        this.attributes = attributes;
    }

    public EntityAttributeData getAttributes() {
        return attributes;
    }

    @Nullable
    public Entity getEntity() {
        return attributes.getHolder();
    }

    public static final class AttributeInstanceConstructing extends EntityAttributeEvent {

        private final Attribute attribute;
        private final AttributeInstance instance;

        public AttributeInstanceConstructing(EntityAttributeData attributes, Attribute attribute, AttributeInstance instance) {
            super(attributes);
            this.attribute = attribute;
            this.instance = instance;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public AttributeInstance getInstance() {
            return instance;
        }
    }
}
