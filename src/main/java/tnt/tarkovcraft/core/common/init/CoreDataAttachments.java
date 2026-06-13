package tnt.tarkovcraft.core.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.pose.EntityPose;
import tnt.tarkovcraft.core.common.pose.NoEntityPose;
import tnt.tarkovcraft.core.common.skill.SkillData;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.common.util.OwnerAttachmentSyncHandler;

import java.util.function.Supplier;

public final class CoreDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TarkovCraftCore.MOD_ID);

    public static final Supplier<AttachmentType<EntityAttributeData>> ENTITY_ATTRIBUTES = REGISTRY.register("entity_attributes", () -> AttachmentType.builder(EntityAttributeData::new)
            .serialize(new EntityAttributeData.Serializer())
            .sync(new EntityAttributeData.SyncHandler())
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<SkillData>> SKILL = REGISTRY.register("skill", () -> AttachmentType.builder(SkillData::new)
            .serialize(new SkillData.Serializer())
            .sync(new SkillData.SyncHandler())
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<StatisticTracker>> STATISTICS = REGISTRY.register("statistics", () -> AttachmentType.builder(StatisticTracker::new)
            .serialize(StatisticTracker.CODEC)
            .copyOnDeath()
            .sync(new OwnerAttachmentSyncHandler<>(StatisticTracker.STREAM_CODEC))
            .build()
    );
    public static final Supplier<AttachmentType<Integer>> WEIGHT = REGISTRY.register("weight", () -> AttachmentType.builder(() -> 0)
            .serialize(Codec.INT)
            .copyOnDeath()
            .sync(new OwnerAttachmentSyncHandler<>(ByteBufCodecs.INT))
            .build()
    );
    public static final Supplier<AttachmentType<EntityPose>> ENTITY_POSE = REGISTRY.register("entity_pose", () -> AttachmentType.builder(NoEntityPose::instance)
            .serialize(EntityPose.Type.CODEC)
            .sync(ByteBufCodecs.fromCodecWithRegistries(EntityPose.Type.CODEC))
            .build()
    );
}
