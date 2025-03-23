package tnt.tarkovcraft.core.common.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.context.OperationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AttributeData implements Synchronizable {

    public static final Codec<AttributeData> CODEC = Codec.unboundedMap(
            TarkovCraftRegistries.ATTRIBUTE.byNameCodec(),
            AttributeInstance.CODEC
    ).xmap(
            AttributeData::new, t -> t.attributeMap
    );

    private final Map<Attribute, AttributeInstance> attributeMap;

    public AttributeData() {
        this.attributeMap = new HashMap<>();
    }

    private AttributeData(Map<Attribute, AttributeInstance> attributeMap) {
        this.attributeMap = new HashMap<>(attributeMap);
    }

    public AttributeInstance getAttribute(Attribute attribute) {
        return this.attributeMap.computeIfAbsent(attribute, Attribute::createInstance);
    }

    public AttributeInstance getAttribute(Supplier<Attribute> attribute) {
        return this.getAttribute(attribute.get());
    }

    public boolean hasAttribute(Attribute attribute) {
        return this.attributeMap.containsKey(attribute);
    }

    public void tick(OperationContext context) {
        for (AttributeInstance value : this.attributeMap.values()) {
            value.update(context);
        }
    }

    @Override
    public CompoundTag serialize() {
        return Codecs.serialize(CODEC, this);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        AttributeData data = Codecs.deserialize(tag, CODEC);
        this.attributeMap.clear();
        this.attributeMap.putAll(data.attributeMap);
    }
}
