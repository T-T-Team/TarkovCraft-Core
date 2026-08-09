package tnt.tarkovcraft.core.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.modifier.AddValueModifier;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.attribute.modifier.MultiplyValueAttributeModifier;
import tnt.tarkovcraft.core.common.attribute.modifier.SetValueAttributeModifier;
import tnt.tarkovcraft.core.common.data.number.*;
import tnt.tarkovcraft.core.common.pose.EntityPose;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.common.skill.progression.ConstantSkillProgressionStrategy;
import tnt.tarkovcraft.core.common.skill.progression.IncrementalSkillProgressionStrategy;
import tnt.tarkovcraft.core.common.skill.progression.SkillProgressionStrategy;
import tnt.tarkovcraft.core.common.skill.bonus.AddAttributeModifierBonus;
import tnt.tarkovcraft.core.common.skill.bonus.SkillBonus;
import tnt.tarkovcraft.core.common.skill.bonus.condition.IsMaxSkillLevelStatCondition;
import tnt.tarkovcraft.core.common.skill.bonus.condition.SkillStatCondition;
import tnt.tarkovcraft.core.common.skill.trigger.configuration.OverweightFactorSkillTriggerConfiguration;
import tnt.tarkovcraft.core.common.skill.trigger.configuration.SimpleSkillTriggerConfiguration;
import tnt.tarkovcraft.core.common.skill.trigger.configuration.SkillTriggerConfiguration;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrigger;
import tnt.tarkovcraft.core.common.skill.trigger.condition.*;
import tnt.tarkovcraft.core.common.statistic.DisplayStatistic;
import tnt.tarkovcraft.core.common.statistic.Statistic;

public final class CoreRegistries {

    // Global utilities
    public static final Registry<Attribute> ATTRIBUTE = new RegistryBuilder<>(Keys.ATTRIBUTE).create();
    public static final Registry<MapCodec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER = new RegistryBuilder<>(Keys.ATTRIBUTE_MODIFIER).create();
    public static final Registry<MapCodec<? extends NumberProvider>> NUMBER_PROVIDER = new RegistryBuilder<>(Keys.NUMBER_PROVIDER).create();
    public static final Registry<Statistic> STATISTICS = new RegistryBuilder<>(Keys.STATISTICS).sync(true).create();
    public static final Registry<EntityPose.Type<?>> ENTITY_POSE = new RegistryBuilder<>(Keys.ENTITY_POSE).sync(true).create();

    // Skill system
    public static final Registry<MapCodec<? extends SkillProgressionStrategy>> SKILL_PROGRESSION_STRATEGY = new RegistryBuilder<>(Keys.SKILL_PROGRESSION_STRATEGY).create();
    public static final Registry<SkillTrigger> SKILL_TRIGGER_EVENT = new RegistryBuilder<>(Keys.SKILL_TRIGGER_EVENT).create();
    public static final Registry<MapCodec<? extends SkillTriggerConfiguration>> SKILL_TRIGGER_TYPE = new RegistryBuilder<>(Keys.SKILL_TRIGGER_TYPE).create();
    public static final Registry<MapCodec<? extends SkillTriggerCondition>> SKILL_TRIGGER_CONDITION_TYPE = new RegistryBuilder<>(Keys.SKILL_TRIGGER_CONDITION_TYPE).create();
    public static final Registry<MapCodec<? extends SkillStatCondition>> SKILL_STAT_CONDITION_TYPE = new RegistryBuilder<>(Keys.SKILL_STAT_CONDITION_TYPE).create();
    public static final Registry<MapCodec<? extends SkillBonus>> SKILL_STAT = new RegistryBuilder<>(Keys.SKILL_STAT).create();

    public static void registerAttributeModifiers(RegisterEvent.RegisterHelper<MapCodec<? extends AttributeModifier>> helper) {
        registerObject(helper, "set", SetValueAttributeModifier.CODEC);
        registerObject(helper, "add", AddValueModifier.CODEC);
        registerObject(helper, "multiply", MultiplyValueAttributeModifier.CODEC);
    }

    public static void registerNumberProviders(RegisterEvent.RegisterHelper<MapCodec<? extends NumberProvider>> helper) {
        registerObject(helper, "constant", ConstantNumberProvider.CODEC);
        registerObject(helper, "ranged", RangedNumberProvider.CODEC);
        registerObject(helper, "duration", DurationNumberProvider.CODEC);
        registerObject(helper, "config", ConfigurationNumberProvider.CODEC);
    }

    public static void registerSkillProgressionStrategies(RegisterEvent.RegisterHelper<MapCodec<? extends SkillProgressionStrategy>> helper) {
        registerObject(helper, "constant", ConstantSkillProgressionStrategy.CODEC);
        registerObject(helper, "incremental", IncrementalSkillProgressionStrategy.CODEC);
    }

    public static void registerSkillTriggerTypes(RegisterEvent.RegisterHelper<MapCodec<? extends SkillTriggerConfiguration>> helper) {
        registerObject(helper, "simple", SimpleSkillTriggerConfiguration.CODEC);
        registerObject(helper, "overweight_factor", OverweightFactorSkillTriggerConfiguration.CODEC);
    }

    public static void registerSkillTriggerConditionTypes(RegisterEvent.RegisterHelper<MapCodec<? extends SkillTriggerCondition>> helper) {
        registerObject(helper, "not", NotSkillTriggerCondition.CODEC);
        registerObject(helper, "sprinting", IsSprintingSkillTriggerCondition.CODEC);
        registerObject(helper, "configurable", ConfigToggleSkillTriggerCondition.CODEC);
        registerObject(helper, "overweight", IsOverweightSkillTriggerCondition.CODEC);
    }

    public static void registerSkillStatConditionTypes(RegisterEvent.RegisterHelper<MapCodec<? extends SkillStatCondition>> helper) {
        registerObject(helper, "is_max_level", IsMaxSkillLevelStatCondition.CODEC);
    }

    public static void registerSkillStats(RegisterEvent.RegisterHelper<MapCodec<? extends SkillBonus>> helper) {
        registerObject(helper, "add_attribute_modifier", AddAttributeModifierBonus.CODEC);
    }

    private static <T> void registerObject(RegisterEvent.RegisterHelper<T> helper, String name, T object) {
        helper.register(Identifier.fromNamespaceAndPath(TarkovCraftCore.MOD_ID, name), object);
    }

    public static final class Keys {

        public static final ResourceKey<Registry<Attribute>> ATTRIBUTE = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("attribute/attribute"));
        public static final ResourceKey<Registry<MapCodec<? extends AttributeModifier>>> ATTRIBUTE_MODIFIER = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("attribute/attribute_modifier"));
        public static final ResourceKey<Registry<MapCodec<? extends NumberProvider>>> NUMBER_PROVIDER = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("util/number_provider"));
        public static final ResourceKey<Registry<Statistic>> STATISTICS = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("util/statistics"));
        public static final ResourceKey<Registry<MapCodec<? extends SkillProgressionStrategy>>> SKILL_PROGRESSION_STRATEGY = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("skill/progression_strategy"));
        public static final ResourceKey<Registry<SkillTrigger>> SKILL_TRIGGER_EVENT = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("skill/trigger_event"));
        public static final ResourceKey<Registry<MapCodec<? extends SkillTriggerConfiguration>>> SKILL_TRIGGER_TYPE = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("skill/trigger_type"));
        public static final ResourceKey<Registry<MapCodec<? extends SkillTriggerCondition>>> SKILL_TRIGGER_CONDITION_TYPE = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("skill/trigger_condition"));
        public static final ResourceKey<Registry<MapCodec<? extends SkillStatCondition>>> SKILL_STAT_CONDITION_TYPE = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("skill/stat_condition"));
        public static final ResourceKey<Registry<MapCodec<? extends SkillBonus>>> SKILL_STAT = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("skill/stat"));
        public static final ResourceKey<Registry<EntityPose.Type<?>>> ENTITY_POSE = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("entity_pose"));
    }

    public static final class DatapackKeys {

        public static final ResourceKey<Registry<SkillDefinition>> SKILL_DEFINITION = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("skill"));
        public static final ResourceKey<Registry<DisplayStatistic>> DISPLAY_STATISTIC = ResourceKey.createRegistryKey(TarkovCraftCore.createIdentifier("statistic"));
    }
}
