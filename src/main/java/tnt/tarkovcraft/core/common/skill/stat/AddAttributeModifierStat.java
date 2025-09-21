package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.AttributeSystem;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.init.CoreSkillStats;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.util.UnitFormat;

import java.util.UUID;

public class AddAttributeModifierStat implements SkillStat {

    public static final MapCodec<AddAttributeModifierStat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CoreRegistries.ATTRIBUTE.byNameCodec().fieldOf("attribute").forGetter(t -> t.target),
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(t -> t.id),
            Codec.FLOAT.fieldOf("levelValue").forGetter(t -> t.levelValue),
            Codec.BOOL.optionalFieldOf("constant", false).forGetter(t -> t.constant),
            UnitFormat.CODEC.optionalFieldOf("displayUnitFormat", UnitFormat.IDENTITY).forGetter(t -> t.displayUnitFormat)
    ).apply(instance, AddAttributeModifierStat::new));

    private final Attribute target;
    private final UUID id;
    private final float levelValue;
    private final boolean constant;
    private final UnitFormat displayUnitFormat;

    public AddAttributeModifierStat(Attribute target, UUID id, float levelValue, boolean constant, UnitFormat displayUnitFormat) {
        this.target = target;
        this.id = id;
        this.levelValue = levelValue;
        this.constant = constant;
        this.displayUnitFormat = displayUnitFormat;
    }

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
        Component label = Component.literal(this.displayUnitFormat.format(value)).withStyle(ChatFormatting.GREEN);
        return new Object[]{label};
    }

    @Override
    public SkillStatType<?> getType() {
        return CoreSkillStats.ADD_MODIFIER.get();
    }

    private float getModifierValue(int level) {
        return this.constant ? this.levelValue : level * this.levelValue;
    }
}
