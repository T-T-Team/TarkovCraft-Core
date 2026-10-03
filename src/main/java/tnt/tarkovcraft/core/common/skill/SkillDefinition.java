package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.skill.bonus.SkillBonusDefinition;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerDefinition;
import tnt.tarkovcraft.core.server.packs.resources.StackCollection;
import tnt.tarkovcraft.core.util.helper.Validator;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class SkillDefinition {

    public static final Codec<SkillDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SkillDefinition::enabled),
            ComponentSerialization.CODEC.optionalFieldOf("display_name").forGetter(t -> Optional.ofNullable(t.displayName)),
            SkillCategory.CODEC.optionalFieldOf("category").forGetter(t -> Optional.ofNullable(t.category)),
            SkillConfiguration.CODEC.optionalFieldOf("configuration").forGetter(t -> Optional.ofNullable(t.configuration)),
            StackCollection.codec(SkillTriggerDefinition.CODEC).optionalFieldOf("triggers", StackCollection.empty()).forGetter(t -> t.triggers),
            StackCollection.codec(SkillBonusDefinition.CODEC).optionalFieldOf("bonuses", StackCollection.empty()).forGetter(t -> t.bonuses)
    ).apply(instance, SkillDefinition::new));

    private final boolean enabled;
    private final Component displayName;
    private final SkillCategory category;
    private final SkillConfiguration configuration;
    private final StackCollection<SkillTriggerDefinition> triggers;
    private final StackCollection<SkillBonusDefinition> bonuses;

    private SkillDefinition(boolean enabled, Optional<Component> displayName, Optional<SkillCategory> category, Optional<SkillConfiguration> configuration, StackCollection<SkillTriggerDefinition> triggers, StackCollection<SkillBonusDefinition> bonuses) {
        this(enabled, displayName.orElse(null), category.orElse(null), configuration.orElse(null), triggers, bonuses);
    }

    private SkillDefinition(boolean enabled, Component displayName, SkillCategory category, SkillConfiguration configuration, StackCollection<SkillTriggerDefinition> triggers, StackCollection<SkillBonusDefinition> bonuses) {
        this.enabled = enabled;
        this.displayName = displayName;
        this.category = category;
        this.configuration = configuration;
        this.triggers = triggers;
        this.bonuses = bonuses;
    }

    public void applyBonuses(Skill skill, Entity entity) {
        for (SkillBonusDefinition bonus : this.bonuses) {
            if (bonus.isAvailable(this, skill, entity)) {
                bonus.apply(this, skill, entity);
            }
        }
    }

    public void clearBonuses(Skill skill, Entity entity) {
        for (SkillBonusDefinition bonus : this.bonuses) {
            bonus.clear(this, skill, entity);
        }
    }

    public boolean enabled() {
        return this.enabled;
    }

    public Component displayName() {
        return this.displayName;
    }

    public SkillCategory category() {
        return this.category;
    }

    public SkillConfiguration configuration() {
        return this.configuration;
    }

    public List<SkillTriggerDefinition> triggers() {
        return this.triggers.values();
    }

    public List<SkillBonusDefinition> bonuses() {
        return this.bonuses.values();
    }

    public static Identifier getIcon(Identifier skillId) {
        return skillId.withPath(pth -> "textures/icons/skill/" + pth + ".png");
    }

    public Skill instance(Identifier identifier) {
        return Skill.fromDefinition(identifier, this);
    }

    public Component getFormattedName(UnaryOperator<Style> style) {
        return this.displayName.copy().withStyle(style);
    }

    public static SkillDefinition merge(SkillDefinition main, SkillDefinition override) {
        boolean enabled = override.enabled();
        Component displayName = override.displayName != null ? override.displayName : main.displayName;
        SkillCategory category = override.category != null ? override.category : main.category;
        SkillConfiguration configuration = override.configuration != null ? override.configuration : main.configuration;
        StackCollection<SkillTriggerDefinition> triggers = main.triggers.merge(override.triggers);
        StackCollection<SkillBonusDefinition> bonuses = main.bonuses.merge(override.bonuses);
        return new SkillDefinition(enabled, displayName, category, configuration, triggers, bonuses);
    }

    public static void validate(SkillDefinition definition) {
        Validator validator = Validator.getDefault();
        validator.requireNonNull(definition.displayName, "displayName");
        validator.requireNonNull(definition.category, "category");
        validator.requireNonNull(definition.configuration, "configuration");
        validator.requireNonEmpty(definition.triggers, "triggers");
        validator.requireNonEmpty(definition.bonuses, "bonuses");
    }
}
