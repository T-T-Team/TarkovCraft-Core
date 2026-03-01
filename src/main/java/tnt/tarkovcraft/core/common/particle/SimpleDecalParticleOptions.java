package tnt.tarkovcraft.core.common.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public record SimpleDecalParticleOptions(ParticleType<SimpleDecalParticleOptions> type, Direction attachDirection, BlockPos position) implements ParticleOptions {

    public SimpleDecalParticleOptions(Supplier<ParticleType<SimpleDecalParticleOptions>> type, Direction attachDirection, BlockPos position) {
        this(type.get(), attachDirection, position);
    }

    public static MapCodec<SimpleDecalParticleOptions> codec(ParticleType<SimpleDecalParticleOptions> particleType) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Direction.CODEC.fieldOf("attachDirection").forGetter(SimpleDecalParticleOptions::attachDirection),
                BlockPos.CODEC.fieldOf("position").forGetter(SimpleDecalParticleOptions::position)
        ).apply(instance, (direction, blockPos) -> new SimpleDecalParticleOptions(particleType, direction, blockPos)));
    }

    public static StreamCodec<ByteBuf, SimpleDecalParticleOptions> streamCodec(ParticleType<SimpleDecalParticleOptions> particleType) {
        return StreamCodec.composite(
                Direction.STREAM_CODEC, SimpleDecalParticleOptions::attachDirection,
                BlockPos.STREAM_CODEC, SimpleDecalParticleOptions::position,
                (direction, blockPos) -> new SimpleDecalParticleOptions(particleType, direction, blockPos)
        );
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
