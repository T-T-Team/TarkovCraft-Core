package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.data.PartialAttachmentTypeSerializer;
import tnt.tarkovcraft.core.common.energy.ArmStamina;
import tnt.tarkovcraft.core.common.energy.MovementStamina;
import tnt.tarkovcraft.core.common.mail.MailManager;
import tnt.tarkovcraft.core.common.skill.SkillData;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;

import java.util.function.Supplier;

public final class CoreDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TarkovCraftCore.MOD_ID);

    public static final Supplier<AttachmentType<MailManager>> MAIL_MANAGER = REGISTRY.register("mail_list", () -> AttachmentType.builder(MailManager::new)
            .serialize(PartialAttachmentTypeSerializer.withCodec(MailManager.CODEC))
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<EntityAttributeData>> ENTITY_ATTRIBUTES = REGISTRY.register("entity_attributes", () -> AttachmentType.builder(EntityAttributeData::new)
            .serialize(PartialAttachmentTypeSerializer.withCodecAndHolder(EntityAttributeData.CODEC, EntityAttributeData::setHolder))
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<MovementStamina>> MOVEMENT_STAMINA = REGISTRY.register("move_stamina", () -> AttachmentType.builder(MovementStamina::new)
            .serialize(MovementStamina.CODEC)
            .build()
    );
    public static final Supplier<AttachmentType<ArmStamina>> ARM_STAMINA = REGISTRY.register("arm_stamina", () -> AttachmentType.builder(ArmStamina::new)
            .serialize(ArmStamina.CODEC)
            .build()
    );
    public static final Supplier<AttachmentType<SkillData>> SKILL = REGISTRY.register("skill", () -> AttachmentType.builder(SkillData::new)
            .serialize(PartialAttachmentTypeSerializer.withCodecAndHolder(SkillData.CODEC, SkillData::setHolder))
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<StatisticTracker>> STATISTICS = REGISTRY.register("statistics", () -> AttachmentType.builder(StatisticTracker::new)
            .serialize(PartialAttachmentTypeSerializer.withCodec(StatisticTracker.CODEC))
            .copyOnDeath()
            .build()
    );
}
