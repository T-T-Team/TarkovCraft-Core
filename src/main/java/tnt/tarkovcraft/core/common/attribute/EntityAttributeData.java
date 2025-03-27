package tnt.tarkovcraft.core.common.attribute;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.init.BaseAttributes;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EntityAttributeData implements Synchronizable {

    public static final Codec<EntityAttributeData> CODEC = Codec.unboundedMap(
            TarkovCraftRegistries.ATTRIBUTE.byNameCodec(),
            AttributeInstance.CODEC
    ).xmap(
            EntityAttributeData::new, t -> t.attributeMap
    );

    private Entity holder;
    private final Map<Attribute, AttributeInstance> attributeMap;

    public EntityAttributeData(IAttachmentHolder holder) {
        if (holder instanceof Entity) {
            this.holder = (Entity) holder;
        } else {
            throw new IllegalArgumentException("Holder must be an instance of Entity");
        }
        this.attributeMap = new HashMap<>();
    }

    private EntityAttributeData(Map<Attribute, AttributeInstance> attributeMap) {
        this.attributeMap = new HashMap<>(attributeMap);
    }

    public AttributeInstance getAttribute(Attribute attribute) {
        return this.attributeMap.computeIfAbsent(attribute, this::createInstance);
    }

    public AttributeInstance getAttribute(Supplier<Attribute> attribute) {
        return this.getAttribute(attribute.get());
    }

    public boolean hasAttribute(Attribute attribute) {
        return this.attributeMap.containsKey(attribute);
    }

    public void tick() {
        for (AttributeInstance value : this.attributeMap.values()) {
            value.update();
        }
    }

    @Override
    public CompoundTag serialize() {
        return Codecs.serialize(CODEC, this);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        EntityAttributeData data = Codecs.deserialize(tag, CODEC);
        this.attributeMap.clear();
        this.attributeMap.putAll(data.attributeMap);
        this.attributeMap.values().forEach(value -> value.setHolder(this.holder));
    }

    private AttributeInstance createInstance(Attribute attribute) {
        AttributeInstance instance = attribute.createInstance(this.holder);
        if (this.holder instanceof ServerPlayer serverPlayer) {
            instance.addListener(new SynchronizationAttributeListener(serverPlayer));
        }
        if (attribute.equals(BaseAttributes.SPRINT.get())) {
            instance.addListener(new SprintAttributeListener(instance));
        }
        // TODO event
        return instance;
    }

    private final class SprintAttributeListener implements AttributeListener {

        private final AttributeInstance instance;

        public SprintAttributeListener(AttributeInstance instance) {
            this.instance = instance;
        }

        @Override
        public void onAttributeModifierAdded(AttributeInstance attribute, AttributeModifier modifier) {
            boolean value = this.instance.booleanValue();
            if (!value) {
                EntityAttributeData.this.holder.setSprinting(false);
            }
        }
    }
}
