package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.attribute.modifier.AddValueModifier;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreSkillStats;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextKeys;

import java.util.UUID;

public class AddAttributeModifierStat implements SkillStat {

    public static final MapCodec<AddAttributeModifierStat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CoreRegistries.ATTRIBUTE.byNameCodec().fieldOf("attribute").forGetter(t -> t.target),
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(t -> t.id),
            Codec.FLOAT.fieldOf("levelValue").forGetter(t -> t.levelValue),
            Codec.BOOL.optionalFieldOf("constant", false).forGetter(t -> t.constant)
    ).apply(instance, AddAttributeModifierStat::new));

    private final Attribute target;
    private final UUID id;
    private final float levelValue;
    private final boolean constant;

    public AddAttributeModifierStat(Attribute target, UUID id, float levelValue, boolean constant) {
        this.target = target;
        this.id = id;
        this.levelValue = levelValue;
        this.constant = constant;
    }

    @Override
    public void clear(Context context) {
        context.get(ContextKeys.ENTITY).ifPresent(entity -> {
            if (entity.hasData(CoreDataAttachments.ENTITY_ATTRIBUTES)) {
                EntityAttributeData attributes = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
                attributes.getAttribute(this.target).removeModifier(this.id);
            }
        });
    }

    @Override
    public void apply(Context context) {
        context.get(ContextKeys.ENTITY).ifPresent(entity -> {
            int skillLevel = context.get(SkillContextKeys.SKILL).map(Skill::getLevel).orElse(0);
            if (skillLevel > 0) {
                EntityAttributeData attributes = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
                AttributeInstance instance = attributes.getAttribute(this.target);
                AttributeModifier modifier = new AddValueModifier(this.id, this.constant ? this.levelValue : skillLevel * this.levelValue);
                instance.addModifier(modifier);
            }
        });
    }

    @Override
    public SkillStatType<?> getType() {
        return CoreSkillStats.ADD_MODIFIER.get();
    }
}
