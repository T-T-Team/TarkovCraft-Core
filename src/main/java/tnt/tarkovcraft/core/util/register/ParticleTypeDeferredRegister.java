package tnt.tarkovcraft.core.util.register;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ParticleTypeDeferredRegister extends DeferredRegister<ParticleType<?>> {

    private ParticleTypeDeferredRegister(String namespace) {
        super(Registries.PARTICLE_TYPE, namespace);
    }

    public static ParticleTypeDeferredRegister create(String namespace) {
        return new ParticleTypeDeferredRegister(namespace);
    }

    public <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> registerParticleType(String id, Function<ParticleType<T>, MapCodec<T>> codec, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec, boolean overrideLimiter) {
        return registerParticleType(id, () -> new ParticleType<T>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return codec.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec.apply(this);
            }
        });
    }

    public DeferredHolder<ParticleType<?>, SimpleParticleType> registerSimpleParticleType(String id) {
        return this.registerSimpleParticleType(id, false);
    }

    public <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> registerParticleType(String id, Supplier<? extends ParticleType<T>> particle) {
        return this.register(id, particle);
    }

    public DeferredHolder<ParticleType<?>, SimpleParticleType> registerSimpleParticleType(String id, boolean overrideLimiter) {
        return this.register(id, () -> new SimpleParticleType(overrideLimiter));
    }
}
