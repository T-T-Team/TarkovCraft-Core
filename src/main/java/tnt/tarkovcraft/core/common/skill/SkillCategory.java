package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.init.CoreAttributes;

public enum SkillCategory implements StringRepresentable {

    PHYSICAL("physical", CoreAttributes.PHYSICAL_SKILL_GROUP_MULTIPLIER),
    COMBAT("combat", CoreAttributes.COMBAT_SKILL_GROUP_MULTIPLIER),
    PRACTICAL("practical", CoreAttributes.PRACTICAL_SKILL_GROUP_MULTIPLIER),
    MENTAL("mental", CoreAttributes.MENTAL_SKILL_GROUP_MULTIPLIER),
    SPECIAL("special", CoreAttributes.SPECIAL_SKILL_GROUP_MULTIPLIER),
    MISC("misc", CoreAttributes.MISC_SKILL_GROUP_MULTIPLIER);

    public static final Codec<SkillCategory> CODEC = StringRepresentable.fromEnum(SkillCategory::values);

    private final String serializedName;
    private final Holder<Attribute> groupAttribute;

    SkillCategory(String serializedName, Holder<Attribute> groupAttribute) {
        this.serializedName = serializedName;
        this.groupAttribute = groupAttribute;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public Holder<Attribute> getGroupAttribute() {
        return groupAttribute;
    }
}
