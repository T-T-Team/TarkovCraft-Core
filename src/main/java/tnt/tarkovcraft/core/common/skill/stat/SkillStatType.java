package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

import java.util.Objects;

public record SkillStatType<S extends SkillStat>(ResourceLocation identifier, MapCodec<S> codec) {

    public static final Codec<SkillStat> INSTANCE_CODEC = TarkovCraftRegistries.SKILL_STAT.byNameCodec().dispatch(SkillStat::getType, SkillStatType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SkillStatType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
