package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.util.context.Context;

public class NotSkillTriggerCondition implements SkillTriggerCondition {

    public static final MapCodec<NotSkillTriggerCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SkillTriggerConditionType.INSTANCE_CODEC.fieldOf("value").forGetter(t -> t.child)
    ).apply(instance, NotSkillTriggerCondition::new));

    private final SkillTriggerCondition child;

    public NotSkillTriggerCondition(SkillTriggerCondition child) {
        this.child = child;
    }

    @Override
    public boolean isTriggerable(Context context) {
        return this.child.isTriggerable(context);
    }

    @Override
    public Component getDescription() {
        return Component.translatable("skill.condition.not", this.child.getDescription());
    }

    @Override
    public SkillTriggerConditionType<?> getType() {
        return CoreSkillTriggerConditions.NOT.get();
    }
}
