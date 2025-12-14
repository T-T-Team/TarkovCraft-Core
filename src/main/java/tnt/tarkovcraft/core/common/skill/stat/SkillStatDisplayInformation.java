package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

public record SkillStatDisplayInformation(Component name, String descriptionKey, Identifier icon) {

    public static final Codec<SkillStatDisplayInformation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(SkillStatDisplayInformation::name),
            Codec.STRING.fieldOf("descriptionKey").forGetter(SkillStatDisplayInformation::descriptionKey),
            Identifier.CODEC.fieldOf("icon").forGetter(SkillStatDisplayInformation::icon)
    ).apply(instance, SkillStatDisplayInformation::new));

    public Component getDescription(SkillDefinition definition, Skill skill, Entity entity, SkillStat stat) {
        return Component.translatable(this.descriptionKey, stat.getTranslationData(definition, skill, entity)).withStyle(ChatFormatting.GRAY);
    }
}
