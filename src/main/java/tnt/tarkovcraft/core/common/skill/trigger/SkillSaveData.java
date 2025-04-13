package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import tnt.tarkovcraft.core.common.skill.Skill;

import java.util.List;
import java.util.UUID;

public class SkillSaveData {

    <T> T getSaveData(UUID trackerId) {
        return null;
    }

    public <T> DataResult<T> encode(DynamicOps<T> ops, T prefix, List<Skill.SkillTrackerData> trackerData) {
        return null;
    }
}
