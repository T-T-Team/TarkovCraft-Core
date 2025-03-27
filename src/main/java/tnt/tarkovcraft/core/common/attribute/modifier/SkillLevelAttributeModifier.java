package tnt.tarkovcraft.core.common.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.BaseAttributeModifiers;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.SkillType;

import java.util.UUID;

public class SkillLevelAttributeModifier extends AttributeModifier {

    public static final MapCodec<SkillLevelAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(AttributeModifier::identifier),
            TarkovCraftRegistries.SKILL.byNameCodec().fieldOf("skill").forGetter(t -> t.skill),
            Codec.FLOAT.fieldOf("factor").forGetter(t -> t.factor),
            AttributeModifier.CODEC.fieldOf("value").forGetter(t -> t.value)
    ).apply(instance, SkillLevelAttributeModifier::new));

    private final SkillType<?> skill;
    private final float factor;
    private final AttributeModifier value;

    public SkillLevelAttributeModifier(UUID identifier, SkillType<?> skill, float factor, AttributeModifier value) {
        super(identifier);
        this.skill = skill;
        this.factor = factor;
        this.value = value;
    }

    @Override
    public double applyModifierOn(double value, Entity holder) {
        return this.value.applyModifierOn(value * this.factor, holder);
    }

    @Override
    public boolean onCancellationTick(Entity holder) {
        return this.value.onCancellationTick(holder);
    }

    @Override
    public int ordering() {
        return this.value.ordering();
    }

    @Override
    public AttributeModifierType<?> getType() {
        return BaseAttributeModifiers.SKILL.get();
    }

    @Override
    public String toString() {
        return String.format("SkillModifier=[skill=%s, factor=%f, value=%s]", this.skill.identifier(), this.factor, this.value);
    }
}
