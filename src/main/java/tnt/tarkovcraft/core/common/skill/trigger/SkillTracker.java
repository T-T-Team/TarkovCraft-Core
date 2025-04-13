package tnt.tarkovcraft.core.common.skill.trigger;

import net.minecraft.nbt.Tag;
import tnt.tarkovcraft.core.util.context.Context;

public interface SkillTracker {

    boolean isTriggerable(Context context);

    float trigger(Context context);

    Tag getSaveData();

    void loadSaveData(Tag tag);

    SkillTrackerType<?> getType();
}
