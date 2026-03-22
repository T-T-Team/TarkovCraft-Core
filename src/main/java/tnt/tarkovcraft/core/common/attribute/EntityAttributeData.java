package tnt.tarkovcraft.core.common.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;
import tnt.tarkovcraft.core.api.event.EntityAttributeEvent;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.util.OwnerAttachmentSyncHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EntityAttributeData {

    public static final MapCodec<EntityAttributeData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(CoreRegistries.ATTRIBUTE.byNameCodec(), AttributeInstance.CODEC).optionalFieldOf("attribute_map", Collections.emptyMap()).forGetter(t -> t.attributeMap)
    ).apply(instance, EntityAttributeData::new));

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

    public static final class SyncHandler extends OwnerAttachmentSyncHandler<EntityAttributeData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, EntityAttributeData> CODEC = ByteBufCodecs.fromCodecWithRegistries(MAP_CODEC.codec());

        public SyncHandler() {
            super(CODEC);
        }

        @Override
        public @Nullable EntityAttributeData read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable EntityAttributeData previousValue) {
            EntityAttributeData data = super.read(holder, buf, previousValue);
            data.setHolder(holder);
            return data;
        }
    }
}
