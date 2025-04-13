package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerEvent;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;
import tnt.tarkovcraft.core.util.context.ContextKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SkillData implements Synchronizable<SkillData> {

    public static final Codec<SkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Skill.CODEC.listOf().fieldOf("skills").xmap(SkillData::asSkillMap, map -> new ArrayList<>(map.values())).forGetter(t -> t.skillMap)
    ).apply(instance, SkillData::new));

    private Entity holder;
    private final Map<SkillDefinition, Skill> skillMap;

    public SkillData(IAttachmentHolder holder) {
        if (!(holder instanceof Entity entity))
            throw new IllegalArgumentException("Holder must be an instance of Entity");
        this.holder = entity;
        this.skillMap = new HashMap<>();
    }

    private SkillData(Map<SkillDefinition, Skill> map) {
        this.skillMap = new HashMap<>(map);
    }

    public boolean trigger(SkillTriggerEvent event, SkillDefinition definition, float multiplier, Context reader) {
        Skill instance = this.getSkill(definition);
        Context ctx = ContextImpl.builder()
                .addProperty(SkillContextKeys.EVENT, event)
                .addProperty(SkillContextKeys.DEFINITION, definition)
                .addProperty(SkillContextKeys.SKILL, instance)
                .addProperty(SkillContextKeys.SKILL_GAIN_MULTIPLIER, multiplier)
                .addProperty(ContextKeys.ENTITY, this.holder)
                .addProperty(ContextKeys.LEVEL, this.holder.level())
                .addMissingFromSource(reader)
                .build();
        float triggerAmount = instance.trigger(ctx);
        if (triggerAmount > 0) {
            instance.addExperience(triggerAmount, () -> {}); // TODO trigger stat reload
            return true;
        }
        return false;
    }

    public Skill getSkill(SkillDefinition skill) {
        return this.skillMap.computeIfAbsent(skill, this::createInstance);
    }

    @Override
    public Codec<SkillData> networkCodec() {
        return CODEC;
    }

    private Skill createInstance(SkillDefinition definition) {
        return definition.instance(); // TODO apply stats
    }

    private static Map<SkillDefinition, Skill> asSkillMap(List<Skill> list) {
        return list.stream().collect(Collectors.toMap(skill -> skill.getDefinition().value(), Function.identity()));
    }
}
