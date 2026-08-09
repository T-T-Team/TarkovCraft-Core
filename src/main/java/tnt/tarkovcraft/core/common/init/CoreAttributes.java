package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;

public final class CoreAttributes {

    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(CoreRegistries.Keys.ATTRIBUTE, TarkovCraftCore.MOD_ID);

    public static final Holder<Attribute> PHYSICAL_SKILL_GROUP_MULTIPLIER = REGISTRY.register("physical_skill_group_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> COMBAT_SKILL_GROUP_MULTIPLIER = REGISTRY.register("combat_skill_group_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> PRACTICAL_SKILL_GROUP_MULTIPLIER = REGISTRY.register("practical_skill_group_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> MENTAL_SKILL_GROUP_MULTIPLIER = REGISTRY.register("mental_skill_group_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> SPECIAL_SKILL_GROUP_MULTIPLIER = REGISTRY.register("special_skill_group_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> MISC_SKILL_GROUP_MULTIPLIER = REGISTRY.register("misc_skill_group_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> MEMORY_FORGET_TIME_MULTIPLIER = REGISTRY.register("memory_forget_time_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> MEMORY_FORGET_AMOUNT_MULTIPLIER = REGISTRY.register("memory_forget_amount_multiplier", Attribute::createMultiplier);
    public static final Holder<Attribute> WEIGHT_LIMIT = REGISTRY.register("weight_limit", location -> Attribute.create(location, 20000));
    public static final Holder<Attribute> WEIGHT_EFFECT_FACTOR = REGISTRY.register("weight_effect_factor", Attribute::createMultiplier);
    public static final Holder<Attribute> VISION = REGISTRY.register("vision", Attribute::createMultiplier);
    public static final Holder<Attribute> HEARING = REGISTRY.register("hearing", Attribute::createMultiplier);
    public static final Holder<Attribute> HEARING_DISTORTION = REGISTRY.register("hearing_distortion", Attribute::createMultiplier);
}
