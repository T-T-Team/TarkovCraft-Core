package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;
import tnt.tarkovcraft.core.api.AttachmentSyncCallbackListener;
import tnt.tarkovcraft.core.api.client.SynchronizableScreen;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.client.util.ClientUtils;
import tnt.tarkovcraft.core.common.Notification;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.skill.bonus.SkillBonus;
import tnt.tarkovcraft.core.common.skill.bonus.SkillBonusDefinition;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrigger;
import tnt.tarkovcraft.core.common.util.OwnerAttachmentSyncHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SkillData {

    public static final Codec<SkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Skill.CODEC.listOf().fieldOf("skills").xmap(SkillData::asSkillMap, map -> new ArrayList<>(map.values())).forGetter(t -> t.skillMap)
    ).apply(instance, SkillData::new));

    private Entity holder;
    private final Map<SkillDefinition, Skill> skillMap;

    public SkillData(IAttachmentHolder holder) {
        this.skillMap = new HashMap<>();
        this.setHolder(holder);
    }

    private SkillData(Map<SkillDefinition, Skill> map) {
        this.skillMap = new HashMap<>(map);
        this.skillMap.values().forEach(skill -> skill.setLevelChangeListener(this::onLevelChange));
    }

    public void setHolder(IAttachmentHolder holder) {
        if (!(holder instanceof Entity entity))
            throw new IllegalArgumentException("Holder must be an instance of Entity");
        this.holder = entity;
    }

    public boolean trigger(SkillTrigger event, SkillDefinition definition, float multiplier, Entity triggerSource) {
        Skill instance = this.getSkill(definition);
        SkillContext context = new SkillContext(event, definition, instance, multiplier, triggerSource);
        float triggerAmount = instance.trigger(context);
        if (triggerAmount > 0) {
            EntityAttributeData attributes = triggerSource.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
            float experience = triggerAmount * this.getGroupLevelMultiplier(attributes, definition.category());
            long gameTime = triggerSource.level().getGameTime();
            // TODO change memory handling
            instance.updateMemory(gameTime, attributes);
            this.addExperience(instance, experience);
            return true;
        }
        return false;
    }

    public void addExperience(SkillDefinition definition, float experience) {
        Skill instance = this.getSkill(definition);
        this.addExperience(instance, experience);
    }

    public void addExperience(Skill instance, float experience) {
        instance.addExperience(experience);
    }

    public Skill getSkill(SkillDefinition skill) {
        return this.skillMap.computeIfAbsent(skill, this::createInstance);
    }

    public void reloadStats() {
        for (Map.Entry<SkillDefinition, Skill> entry : this.skillMap.entrySet()) {
            SkillDefinition definition = entry.getKey();
            Skill instance = entry.getValue();
            List<SkillBonusDefinition> bonuses = definition.bonuses();
            bonuses.forEach(bonusDef -> bonusDef.bonus().clear(definition, instance, this.holder));
            applyStats(definition, instance);
        }
    }

    private void applyStats() {
        for (Map.Entry<SkillDefinition, Skill> entry : this.skillMap.entrySet()) {
            SkillDefinition definition = entry.getKey();
            Skill instance = entry.getValue();
            this.applyStats(definition, instance);
        }
    }

    private void applyStats(SkillDefinition definition, Skill skill) {
        for (SkillBonusDefinition bonus : definition.bonuses()) {
            if (bonus.isAvailable(definition, skill, this.holder)) {
                SkillBonus stat = bonus.bonus();
                stat.apply(definition, skill, this.holder);
            }
        }
    }

    private Skill createInstance(SkillDefinition definition) {
        Skill instance = definition.instance(this.getRegistryAccess());
        instance.setLevelChangeListener(this::onLevelChange);
        if (this.holder != null) {
            this.applyStats(definition, instance);
        }
        return instance;
    }

    private static Map<SkillDefinition, Skill> asSkillMap(List<Skill> list) {
        return list.stream().collect(Collectors.toMap(skill -> skill.getDefinition().value(), Function.identity()));
    }

    private RegistryAccess getRegistryAccess() {
        if (this.holder == null || this.holder.level().isClientSide()) {
            return ClientUtils.getClientRegistryAccess();
        } else {
            return this.holder.registryAccess();
        }
    }

    private float getGroupLevelMultiplier(EntityAttributeData data, SkillCategory category) {
        Holder<Attribute> attribute = category.getGroupAttribute();
        float value = data.getAttribute(attribute).floatValue();
        return Math.max(0.0F, value);
    }

    private void onLevelChange(Skill skill, int currentLevel, int previousLevel) {
        if (this.holder instanceof ServerPlayer player) {
            if (previousLevel < skill.getLevel()) {
                Holder<SkillDefinition> definitionHolder = skill.getDefinition();
                SkillDefinition definition = definitionHolder.value();
                Notification notification = Notification.success(Component.translatable("label.tarkovcraft_core.skill.level_up", definition.displayName(), skill.getLevel()));
                notification.setIcon(SkillDefinition.getIcon(definitionHolder));
                notification.send(player);
            }
            this.applyStats();

            SkillSystem.synchronize(player);
        }
    }

    public static final class Serializer implements IAttachmentSerializer<CompoundTag, SkillData> {

        @Override
        public SkillData read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
            RegistryOps<Tag> context = provider.createSerializationContext(NbtOps.INSTANCE);
            DataResult<SkillData> result = CODEC.parse(context, compoundTag);
            SkillData attachment = result.getOrThrow();
            attachment.setHolder(iAttachmentHolder);
            return attachment;
        }

        @Override
        public @Nullable CompoundTag write(SkillData skillData, HolderLookup.Provider provider) {
            RegistryOps<Tag> context = provider.createSerializationContext(NbtOps.INSTANCE);
            DataResult<Tag> result = CODEC.encodeStart(context, skillData);
            Tag tag = result.getOrThrow();
            return (CompoundTag) tag;
        }
    }

    public static final class SyncHandler extends OwnerAttachmentSyncHandler<SkillData> implements AttachmentSyncCallbackListener<SkillData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, SkillData> CODEC = ByteBufCodecs.fromCodecWithRegistries(SkillData.CODEC);

        public SyncHandler() {
            super(CODEC);
        }

        @Override
        public void write(RegistryFriendlyByteBuf buf, SkillData attachment, boolean initialSync) {
            attachment.applyStats();
            super.write(buf, attachment, initialSync);
        }

        @Override
        public void onDataSynced(IAttachmentHolder holder, AttachmentType<SkillData> attachmentType, SkillData attachment) {
            TarkovCraftCoreClient.synchronizeCurrentScreen(SynchronizableScreen.SKILLS);
        }
    }
}
