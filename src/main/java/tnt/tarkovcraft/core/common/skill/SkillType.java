package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

import java.util.Objects;
import java.util.function.Function;

public record SkillType<S extends Skill>(ResourceLocation identifier, MapCodec<S> codec, Function<SkillType<S>, S> skillInstanceFactory) {

    public static final Codec<Skill> ID_CODEC = TarkovCraftRegistries.SKILL.byNameCodec().dispatch(Skill::getType, SkillType::codec);

    public S createInstance() {
        return this.skillInstanceFactory.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SkillType<?> skillType)) return false;
        return Objects.equals(identifier, skillType.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
