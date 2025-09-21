package tnt.tarkovcraft.core.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.data.CallbackAttachmentSerializer;
import tnt.tarkovcraft.core.common.mail.MailManager;
import tnt.tarkovcraft.core.common.skill.SkillData;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.common.util.OwnerAttachmentSyncHandler;

import java.util.function.Supplier;

public final class CoreDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TarkovCraftCore.MOD_ID);

    public static final Supplier<AttachmentType<MailManager>> MAIL_MANAGER = REGISTRY.register("mail_list", () -> AttachmentType.builder(MailManager::new)
            .serialize(MailManager.MAP_CODEC)
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<EntityAttributeData>> ENTITY_ATTRIBUTES = REGISTRY.register("entity_attributes", () -> AttachmentType.builder(EntityAttributeData::new)
            .serialize(CallbackAttachmentSerializer.create(EntityAttributeData.MAP_CODEC, EntityAttributeData::setHolder))
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<SkillData>> SKILL = REGISTRY.register("skill", () -> AttachmentType.builder(SkillData::new)
            .serialize(CallbackAttachmentSerializer.create(SkillData.MAP_CODEC, SkillData::setHolder))
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<StatisticTracker>> STATISTICS = REGISTRY.register("statistics", () -> AttachmentType.builder(StatisticTracker::new)
            .serialize(StatisticTracker.MAP_CODEC)
            .copyOnDeath()
            .sync(new OwnerAttachmentSyncHandler<>(StatisticTracker.STREAM_CODEC))
            .build()
    );
    public static final Supplier<AttachmentType<Integer>> WEIGHT = REGISTRY.register("weight", () -> AttachmentType.builder(() -> 0)
            .serialize(Codec.INT.fieldOf("value"))
            .copyOnDeath()
            .sync(new OwnerAttachmentSyncHandler<>(ByteBufCodecs.INT))
            .build()
    );
}
