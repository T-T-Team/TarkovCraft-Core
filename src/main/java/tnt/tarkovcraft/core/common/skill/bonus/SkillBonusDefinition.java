package tnt.tarkovcraft.core.common.skill.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.common.skill.bonus.condition.SkillStatCondition;

import java.util.Collections;
import java.util.List;

public record SkillBonusDefinition(String name, int minLevel, int maxLevel, List<SkillStatCondition> conditions, SkillBonus bonus) {

    public static final Codec<SkillBonusDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(SkillBonusDefinition::name),
            NumberProvider.NON_NEGATIVE_INT.optionalFieldOf("min_level", 1).forGetter(SkillBonusDefinition::minLevel),
            NumberProvider.POSITIVE_INT.optionalFieldOf("max_level", Integer.MAX_VALUE).forGetter(SkillBonusDefinition::maxLevel),
            SkillStatCondition.CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(SkillBonusDefinition::conditions),
            SkillBonus.CODEC.fieldOf("bonus").forGetter(SkillBonusDefinition::bonus)
    ).apply(instance, SkillBonusDefinition::new));

    public boolean isAvailable(SkillDefinition definition, Skill skill, Entity entity) {
        int level = skill.getLevel();
        if (level < this.minLevel || level > this.maxLevel) {
            return false;
        }
        return this.conditions.stream()
                .allMatch(c -> c.canApply(definition, skill, entity));
    }

    public String getBaseLanguageKey(Holder<SkillDefinition> holder) {
        Identifier id = holder.getKey().identifier();
        return id.toLanguageKey("skill", "bonus." + this.name);
    }

    public MutableComponent getDisplayName(Holder<SkillDefinition> holder) {
        return Component.translatable(this.getBaseLanguageKey(holder));
    }

    public MutableComponent getContextualDescription(Holder<SkillDefinition> holder, Skill skill, LivingEntity entity) {
        Object[] params = this.bonus.getTranslationData(holder.value(), skill, entity);
        return Component.translatable(this.getBaseLanguageKey(holder) + ".description", params);
    }

    public Identifier getIcon(Holder<SkillDefinition> holder) {
        Identifier id = holder.getKey().identifier();
        return id.withPath("textures/icons/skill/bonus/" + this.name + ".png");
    }
}
