package tnt.tarkovcraft.core.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.data.CallbackAttachmentSerializer;
import tnt.tarkovcraft.core.common.skill.SkillData;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.common.util.OwnerAttachmentSyncHandler;

import java.util.function.Supplier;

public final class CoreDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TarkovCraftCore.MOD_ID);

    public static final Supplier<AttachmentType<EntityAttributeData>> ENTITY_ATTRIBUTES = REGISTRY.register("entity_attributes", () -> AttachmentType.builder(EntityAttributeData::new)
            .serialize(CallbackAttachmentSerializer.create(EntityAttributeData.CODEC, EntityAttributeData::setHolder))
            .sync(new EntityAttributeData.SyncHandler())
            .copyOnDeath()
            .build()
    );
    public static final Supplier<AttachmentType<SkillData>> SKILL = REGISTRY.register("skill", () -> AttachmentType.builder(SkillData::new)
            .serialize(CallbackAttachmentSerializer.create(SkillData.CODEC, SkillData::setHolder))
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
}
