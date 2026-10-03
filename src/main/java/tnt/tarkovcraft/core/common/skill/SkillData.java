package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import tnt.tarkovcraft.core.api.AttachmentSyncCallbackListener;
import tnt.tarkovcraft.core.api.client.SynchronizableScreen;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.common.Notification;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrigger;
import tnt.tarkovcraft.core.common.util.OwnerAttachmentSyncHandler;
import tnt.tarkovcraft.core.server.packs.resources.IdResource;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SkillData {

    public static final Codec<SkillData> CODEC = Codec.unboundedMap(Identifier.CODEC, Skill.CODEC)
            .xmap(SkillData::new, t -> t.skillMap);
    public static final MapCodec<SkillData> MAP_CODEC = CODEC.fieldOf("skills");

    private Entity holder;
    private final Map<Identifier, Skill> skillMap;

    public SkillData(IAttachmentHolder holder) {
        this.skillMap = new HashMap<>();
        this.setHolder(holder);
    }

    private SkillData(Map<Identifier, Skill> map) {
        this.skillMap = new HashMap<>(map);
        this.skillMap.values().forEach(skill -> skill.setLevelChangeListener(this::onLevelChange));
    }

    public void setHolder(IAttachmentHolder holder) {
        if (!(holder instanceof Entity entity))
            throw new IllegalArgumentException("Holder must be an instance of Entity");
        this.holder = entity;
    }

    public List<Skill> listAllSkills() {
        Collection<Identifier> ids = SkillSystem.listAvailableSkills();
        return ids.stream()
                .map(this::getSkill)
                .toList();
    }

    public boolean trigger(SkillTrigger event, Identifier skillId, float multiplier, Entity triggerSource) {
        Skill instance = this.getSkill(skillId);
        SkillContext context = new SkillContext(event, instance, multiplier, triggerSource);
        float triggerAmount = instance.trigger(context);
        if (triggerAmount > 0) {
            EntityAttributeData attributes = triggerSource.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
            SkillDefinition definition = instance.getDefinition();
            float experience = triggerAmount * this.getGroupLevelMultiplier(attributes, definition.category());
            long gameTime = triggerSource.level().getGameTime();
            // TODO change memory handling
            instance.updateMemory(gameTime, attributes, triggerSource.getRandom());
            this.addExperience(instance, experience);
            return true;
        }
        return false;
    }

    public void addExperience(Identifier skill, float experience) {
        Skill instance = this.getSkill(skill);
        this.addExperience(instance, experience);
    }

    public void addExperience(Skill instance, float experience) {
        instance.addExperience(experience);
    }

    public Skill getSkill(Identifier identifier) {
        return this.skillMap.computeIfAbsent(identifier, this::createInstance);
    }

    private void applyBonuses() {
        for (Skill skill : this.skillMap.values()) {
            if (this.holder != null) {
                skill.applyBonuses(this.holder);
            }
        }
    }

    private Skill createInstance(Identifier identifier) {
        var resourceOptional = SkillSystem.getDefinition(identifier);
        var definition = resourceOptional.map(IdResource::element)
                        .orElseThrow();
        Skill skill = definition.instance(identifier);
        skill.setLevelChangeListener(this::onLevelChange);
        if (this.holder != null) {
            skill.applyBonuses(this.holder);
        }
        return skill;
    }

    private float getGroupLevelMultiplier(EntityAttributeData data, SkillCategory category) {
        Holder<Attribute> attribute = category.getGroupAttribute();
        float value = data.getAttribute(attribute).floatValue();
        return Math.max(0.0F, value);
    }

    private void onLevelChange(Skill skill, int currentLevel, int previousLevel) {
        if (this.holder instanceof ServerPlayer player) {
            if (previousLevel < skill.getLevel()) {
                SkillDefinition definition = skill.getDefinition();
                Notification notification = Notification.success(Component.translatable("label.tarkovcraft_core.skill.level_up", definition.displayName(), skill.getLevel()));
                notification.setIcon(SkillDefinition.getIcon(skill.getIdentifier()));
                notification.send(player);
            }
            this.applyBonuses();

            SkillSystem.synchronize(player);
        }
    }

    public static final class Serializer implements IAttachmentSerializer<SkillData> {

        @Override
        public SkillData read(IAttachmentHolder holder, ValueInput input) {
            SkillData attachment = input.read("skills", CODEC)
                    .orElseThrow(() -> new IllegalStateException("Failed to deserialize data attachment"));
            attachment.setHolder(holder);
            return attachment;
        }

        @Override
        public boolean write(SkillData attachment, ValueOutput output) {
            output.store("skills", CODEC, attachment);
            return true;
        }
    }

    public static final class SyncHandler extends OwnerAttachmentSyncHandler<SkillData> implements AttachmentSyncCallbackListener<SkillData> {

        private static final StreamCodec<RegistryFriendlyByteBuf, SkillData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

        public SyncHandler() {
            super(STREAM_CODEC);
        }

        @Override
        public void write(RegistryFriendlyByteBuf buf, SkillData attachment, boolean initialSync) {
            attachment.applyBonuses();
            super.write(buf, attachment, initialSync);
        }

        @Override
        public void onDataSynced(IAttachmentHolder holder, AttachmentType<SkillData> attachmentType, SkillData attachment) {
            TarkovCraftCoreClient.synchronizeCurrentScreen(SynchronizableScreen.SKILLS);
        }
    }
}
