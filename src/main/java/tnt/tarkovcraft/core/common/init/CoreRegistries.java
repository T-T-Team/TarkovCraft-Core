package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifierType;
import tnt.tarkovcraft.core.common.data.filter.ItemStackFilterType;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.mail.MailMessageAttachmentType;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.common.skill.stat.SkillStatType;
import tnt.tarkovcraft.core.common.skill.stat.condition.SkillStatConditionType;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerType;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTriggerEvent;
import tnt.tarkovcraft.core.common.skill.tracker.condition.SkillTriggerConditionType;

public final class CoreRegistries {

    // Global utilities
    public static final Registry<Attribute> ATTRIBUTE = new RegistryBuilder<>(Keys.ATTRIBUTE).create();
    public static final Registry<AttributeModifierType<?>> ATTRIBUTE_MODIFIER = new RegistryBuilder<>(Keys.ATTRIBUTE_MODIFIER).create();
    public static final Registry<ItemStackFilterType<?>> ITEMSTACK_FILTER = new RegistryBuilder<>(Keys.ITEMSTACK_FILTER).create();
    public static final Registry<NumberProviderType<?>> NUMBER_PROVIDER = new RegistryBuilder<>(Keys.NUMBER_PROVIDER).create();

    // Mail system
    public static final Registry<MailMessageAttachmentType<?>> MAIL_MESSAGE_ATTACHMENT = new RegistryBuilder<>(Keys.MAIL_MESSAGE_ATTACHMENT).create();

    // Skill system
    public static final Registry<SkillTriggerEvent> SKILL_TRIGGER_EVENT = new RegistryBuilder<>(Keys.SKILL_TRIGGER_EVENT).create();
    public static final Registry<SkillTrackerType<?, ?>> SKILL_TRIGGER_TYPE = new RegistryBuilder<>(Keys.SKILL_TRIGGER_TYPE).create();
    public static final Registry<SkillTriggerConditionType<?>> SKILL_TRIGGER_CONDITION_TYPE = new RegistryBuilder<>(Keys.SKILL_TRIGGER_CONDITION_TYPE).create();
    public static final Registry<SkillStatConditionType<?>> SKILL_STAT_CONDITION_TYPE = new RegistryBuilder<>(Keys.SKILL_STAT_CONDITION_TYPE).create();
    public static final Registry<SkillStatType<?>> SKILL_STAT = new RegistryBuilder<>(Keys.SKILL_STAT).create();

    public static final class Keys {

        public static final ResourceKey<Registry<Attribute>> ATTRIBUTE = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("attribute/attribute"));
        public static final ResourceKey<Registry<AttributeModifierType<?>>> ATTRIBUTE_MODIFIER = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("attribute/attribute_modifier"));
        public static final ResourceKey<Registry<ItemStackFilterType<?>>> ITEMSTACK_FILTER = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("util/itemstack_filter"));
        public static final ResourceKey<Registry<NumberProviderType<?>>> NUMBER_PROVIDER = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("util/number_provider"));
        public static final ResourceKey<Registry<MailMessageAttachmentType<?>>> MAIL_MESSAGE_ATTACHMENT = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("mail/message_attachment"));
        public static final ResourceKey<Registry<SkillTriggerEvent>> SKILL_TRIGGER_EVENT = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("skill/trigger_event"));
        public static final ResourceKey<Registry<SkillTrackerType<?, ?>>> SKILL_TRIGGER_TYPE = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("skill/trigger_type"));
        public static final ResourceKey<Registry<SkillTriggerConditionType<?>>> SKILL_TRIGGER_CONDITION_TYPE = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("skill/trigger_condition"));
        public static final ResourceKey<Registry<SkillStatConditionType<?>>> SKILL_STAT_CONDITION_TYPE = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("skill/stat_condition"));
        public static final ResourceKey<Registry<SkillStatType<?>>> SKILL_STAT = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("skill/stat"));
    }

    public static final class DatapackKeys {

        public static final ResourceKey<Registry<SkillDefinition>> SKILL_DEFINITION = ResourceKey.createRegistryKey(TarkovCraftCore.createResourceLocation("skill"));
    }
}
