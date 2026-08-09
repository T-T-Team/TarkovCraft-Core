package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.bonus.SkillBonusDefinition;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerDefinition;

import java.util.List;
import java.util.function.UnaryOperator;

public record SkillDefinition(Component displayName, SkillCategory category, SkillConfiguration configuration, List<SkillTriggerDefinition> triggers, List<SkillBonusDefinition> bonuses) {

    public static final Codec<SkillDefinition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("display_name").forGetter(SkillDefinition::displayName),
            SkillCategory.CODEC.optionalFieldOf("category", SkillCategory.MISC).forGetter(SkillDefinition::category),
            SkillConfiguration.CODEC.fieldOf("configuration").forGetter(SkillDefinition::configuration),
            SkillTriggerDefinition.CODEC.listOf().fieldOf("triggers").forGetter(SkillDefinition::triggers),
            SkillBonusDefinition.CODEC.listOf().fieldOf("bonuses").forGetter(SkillDefinition::bonuses)
    ).apply(instance, SkillDefinition::new));
    public static final Codec<Holder<SkillDefinition>> CODEC = RegistryFixedCodec.create(CoreRegistries.DatapackKeys.SKILL_DEFINITION);

    public static Identifier getIcon(Holder<SkillDefinition> holder) {
        Identifier skillIdentifier = holder.getKey().identifier();
        return skillIdentifier.withPath(pth -> "textures/icons/skill/" + pth + ".png");
    }

    public Skill instance(RegistryAccess access) {
        Registry<SkillDefinition> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.SKILL_DEFINITION);
        Holder<SkillDefinition> reference = registry.wrapAsHolder(this);
        return new Skill(reference);
    }

    public Component getFormattedName(UnaryOperator<Style> style) {
        return this.displayName.copy().withStyle(style);
    }
}
