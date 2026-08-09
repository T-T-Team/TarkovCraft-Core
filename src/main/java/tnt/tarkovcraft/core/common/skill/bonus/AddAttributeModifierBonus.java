package tnt.tarkovcraft.core.common.skill.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.AttributeSystem;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.util.NumberFormatter;

public record AddAttributeModifierBonus(Attribute target, Identifier id, float levelValue, boolean constant, NumberFormatter formatter) implements SkillBonus {

    public static final MapCodec<AddAttributeModifierBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CoreRegistries.ATTRIBUTE.byNameCodec().fieldOf("attribute").forGetter(t -> t.target),
            Identifier.CODEC.fieldOf("id").forGetter(t -> t.id),
            Codec.FLOAT.fieldOf("per_level_value").forGetter(t -> t.levelValue),
            Codec.BOOL.optionalFieldOf("constant", false).forGetter(t -> t.constant),
            NumberFormatter.CODEC.optionalFieldOf("formatter", NumberFormatter.IDENTITY).forGetter(t -> t.formatter)
    ).apply(instance, AddAttributeModifierBonus::new));

    @Override
    public void clear(SkillDefinition definition, Skill skill, Entity entity) {
        AttributeSystem.removeModifier(entity, this.target, this.id);
    }

    @Override
    public void apply(SkillDefinition definition, Skill skill, Entity entity) {
        int level = skill.getLevel();
        AttributeModifier modifier = AttributeModifier.add(this.id, this.getModifierValue(level));
        AttributeSystem.addModifier(entity, this.target, modifier, true);
    }

    @Override
    public Object[] getTranslationData(SkillDefinition definition, Skill skill, Entity entity) {
        int level = skill.getLevel();
        float value = this.getModifierValue(level);
        Component label = Component.literal(this.formatter.formatValue(value)).withStyle(ChatFormatting.GREEN);
        return new Object[]{label};
    }

    @Override
    public MapCodec<? extends SkillBonus> codec() {
        return CODEC;
    }

    private float getModifierValue(int level) {
        return this.constant ? this.levelValue : level * this.levelValue;
    }
}
