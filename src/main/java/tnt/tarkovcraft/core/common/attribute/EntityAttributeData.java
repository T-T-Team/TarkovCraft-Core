package tnt.tarkovcraft.core.common.attribute;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.common.event.EntityAttributeEvent;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.network.Synchronizable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EntityAttributeData implements Synchronizable<EntityAttributeData> {

    public static final Codec<EntityAttributeData> CODEC = Codec.unboundedMap(
            CoreRegistries.ATTRIBUTE.byNameCodec(),
            AttributeInstance.CODEC
    ).xmap(
            EntityAttributeData::new, t -> t.attributeMap
    );

    private Entity holder;
    private final Map<Attribute, AttributeInstance> attributeMap;

    public EntityAttributeData(IAttachmentHolder holder) {
        this.attributeMap = new HashMap<>();
        this.setHolder(holder);
    }

    private EntityAttributeData(Map<Attribute, AttributeInstance> attributeMap) {
        this.attributeMap = new HashMap<>(attributeMap);
        this.attributeMap.values().forEach(this::addAttributeListeners);
    }

    public AttributeInstance getAttribute(Attribute attribute) {
        return this.attributeMap.computeIfAbsent(attribute, this::createInstance);
    }

    public AttributeInstance getAttribute(Holder<Attribute> reference) {
        return this.getAttribute(reference.value());
    }

    public AttributeInstance getAttribute(Supplier<Attribute> attribute) {
        return this.getAttribute(attribute.get());
    }

    public boolean hasAttribute(Attribute attribute) {
        return this.attributeMap.containsKey(attribute);
    }

    public void update() {
        for (AttributeInstance value : this.attributeMap.values()) {
            value.update();
        }
    }

    public void setHolder(IAttachmentHolder holder) {
        if (holder instanceof Entity) {
            this.holder = (Entity) holder;
            this.addAttributeListeners(this.attributeMap.values());
        } else {
            throw new IllegalArgumentException("Holder must be an instance of Entity");
        }
    }

    public Entity getHolder() {
        return this.holder;
    }

    @Override
    public Codec<EntityAttributeData> networkCodec() {
        return CODEC;
    }

    private AttributeInstance createInstance(Attribute attribute) {
        AttributeInstance instance = attribute.createInstance(this.holder);
        this.addAttributeListeners(instance);
        return instance;
    }

    private void addAttributeListeners(Collection<AttributeInstance> collection) {
        collection.forEach(this::addAttributeListeners);
    }

    private void addAttributeListeners(AttributeInstance instance) {
        if (this.holder == null)
            return;
        if (this.holder instanceof ServerPlayer serverPlayer) {
            instance.addListener(new SynchronizationAttributeListener(serverPlayer));
        }
        NeoForge.EVENT_BUS.post(new EntityAttributeEvent.AttributeInstanceConstructing(this, instance.getAttribute(), instance));
    }
}
