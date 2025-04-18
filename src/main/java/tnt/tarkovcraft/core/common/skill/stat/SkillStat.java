package tnt.tarkovcraft.core.common.skill.stat;

import tnt.tarkovcraft.core.util.context.Context;

public interface SkillStat {

    void apply(Context context);

    void clear(Context context);

    Object[] getTranslationData(Context context);

    SkillStatType<?> getType();
}
