package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import tnt.tarkovcraft.core.common.Notification;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.skill.stat.SkillStat;
import tnt.tarkovcraft.core.common.skill.stat.SkillStatDefinition;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTriggerEvent;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;
import tnt.tarkovcraft.core.util.context.ContextKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SkillData implements Synchronizable<SkillData> {

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
    }

    public void setHolder(IAttachmentHolder holder) {
        if (!(holder instanceof Entity entity))
            throw new IllegalArgumentException("Holder must be an instance of Entity");
        this.holder = entity;
    }

    public boolean trigger(SkillTriggerEvent event, SkillDefinition definition, float multiplier, Entity triggerSource, Context reader) {
        Skill instance = this.getSkill(definition);
        Context ctx = ContextImpl.builder()
                .addProperty(SkillContextKeys.EVENT, event)
                .addProperty(SkillContextKeys.DEFINITION, definition)
                .addProperty(SkillContextKeys.SKILL, instance)
                .addProperty(SkillContextKeys.SKILL_GAIN_MULTIPLIER, multiplier)
                .addProperty(ContextKeys.ENTITY, triggerSource)
                .addProperty(ContextKeys.LEVEL, triggerSource.level())
                .addMissingFromSource(reader)
                .build();
        float triggerAmount = instance.trigger(ctx);
        if (triggerAmount > 0) {
            EntityAttributeData attributes = triggerSource.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
            float experience = triggerAmount * this.getGroupLevelMultiplier(attributes, definition.getGroupLevelingModifiers());
            long gameTime = triggerSource.level().getGameTime();
            instance.updateMemory(gameTime, attributes, prevLevel -> this.onLevelChange(prevLevel, instance));
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
        instance.addExperience(experience, (previousLevel) -> this.onLevelChange(previousLevel, instance));
    }

    public Skill getSkill(SkillDefinition skill) {
        return this.skillMap.computeIfAbsent(skill, this::createInstance);
    }

    @Override
    public Codec<SkillData> networkCodec() {
        return CODEC;
    }

    @Override
    public void preSyncPrepare() {
        this.applyStats();
    }

    public void reloadStats() {
        for (Map.Entry<SkillDefinition, Skill> entry : this.skillMap.entrySet()) {
            SkillDefinition definition = entry.getKey();
            Skill instance = entry.getValue();
            List<SkillStatDefinition> stats = definition.getStats();
            Context context = ContextImpl.of(
                    ContextKeys.LEVEL, this.holder.level(),
                    ContextKeys.ENTITY, this.holder,
                    SkillContextKeys.DEFINITION, definition,
                    SkillContextKeys.SKILL, instance
            );
            stats.forEach(statDef -> statDef.stat().clear(context));
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
        Context context = ContextImpl.of(
                ContextKeys.LEVEL, this.holder.level(),
                ContextKeys.ENTITY, this.holder,
                SkillContextKeys.DEFINITION, definition,
                SkillContextKeys.SKILL, skill
        );
        for (SkillStatDefinition statDefinition : definition.getStats()) {
            if (statDefinition.isAvailable(context)) {
                SkillStat stat = statDefinition.stat();
                stat.apply(context);
            }
        }
    }

    private Skill createInstance(SkillDefinition definition) {
        Skill instance = definition.instance(this.getRegistryAccess());
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
            return getClientRegistryAccess();
        } else {
            return this.holder.registryAccess();
        }
    }

    private float getGroupLevelMultiplier(EntityAttributeData data, List<Holder<Attribute>> group) {
        float multiplier = 1.0F;
        for (Holder<Attribute> holder : group) {
            float attributeValue = data.getAttribute(holder).floatValue();
            multiplier *= attributeValue;
        }
        return Math.max(0.0F, multiplier);
    }

    public void onLevelChange(int prevLevel, Skill skill) {
        if (this.holder instanceof ServerPlayer player) {
            if (prevLevel < skill.getLevel()) {
                Holder<SkillDefinition> definitionHolder = skill.getDefinition();
                SkillDefinition definition = definitionHolder.value();
                Notification notification = Notification.info(Component.translatable("label.tarkovcraft_core.skill.level_up", definition.getName(), skill.getLevel()));
                notification.setIcon(SkillDefinition.getIcon(definitionHolder));
                notification.send(player);
            }
            this.applyStats();

            PacketDistributor.sendToPlayer(player, new S2C_SendDataAttachments(player, CoreDataAttachments.SKILL.get()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static RegistryAccess getClientRegistryAccess() {
        return Minecraft.getInstance().getConnection().registryAccess();
    }
}
