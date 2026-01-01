package tnt.tarkovcraft.core.common.particle;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public record SimpleDecalParticleOptions(ParticleType<SimpleDecalParticleOptions> type, Direction attachDirection) implements ParticleOptions {

    public SimpleDecalParticleOptions(Supplier<ParticleType<SimpleDecalParticleOptions>> type, Direction attachDirection) {
        this(type.get(), attachDirection);
    }

    public static MapCodec<SimpleDecalParticleOptions> codec(ParticleType<SimpleDecalParticleOptions> particleType) {
        return Direction.CODEC.xmap(direction -> new SimpleDecalParticleOptions(particleType, direction), options -> options.attachDirection)
                .fieldOf("attachDirection");
    }

    public static StreamCodec<ByteBuf, SimpleDecalParticleOptions> streamCodec(ParticleType<SimpleDecalParticleOptions> particleType) {
        return Direction.STREAM_CODEC.map(direction -> new SimpleDecalParticleOptions(particleType, direction), options -> options.attachDirection);
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
