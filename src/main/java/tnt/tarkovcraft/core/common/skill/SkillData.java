package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import tnt.tarkovcraft.core.network.Synchronizable;

import java.util.ArrayList;
import java.util.List;

public final class SkillData implements Synchronizable<SkillData> {

    public static final Codec<SkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDefinition.CODEC.listOf().fieldOf("skills").forGetter(t -> t.skills)
    ).apply(instance, SkillData::new));

    private IAttachmentHolder holder;
    private final List<Holder<SkillDefinition>> skills;

    public SkillData(IAttachmentHolder holder) {
        this.holder = holder;
        this.skills = new ArrayList<>();
    }

    private SkillData(List<Holder<SkillDefinition>> skills) {
        this.skills = new ArrayList<>(skills);
    }

    public void addSkill(Holder<SkillDefinition> skill) {
        this.skills.add(skill);
    }

    @Override
    public Codec<SkillData> networkCodec() {
        return CODEC;
    }
}
