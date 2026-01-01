package tnt.tarkovcraft.core.network.message;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;

import java.util.List;

public record S2C_MakeParticles(ParticleOptions particle, double x, double y, double z, boolean overrideLimiter, boolean alwaysShow, List<Vec3> directions) implements CustomPacketPayload {

    public static final Identifier ID = TarkovCraftCoreNetwork.createId(S2C_MakeParticles.class);
    public static final Type<S2C_MakeParticles> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2C_MakeParticles> CODEC = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC, S2C_MakeParticles::particle,
            ByteBufCodecs.DOUBLE, S2C_MakeParticles::x,
            ByteBufCodecs.DOUBLE, S2C_MakeParticles::y,
            ByteBufCodecs.DOUBLE, S2C_MakeParticles::z,
            ByteBufCodecs.BOOL, S2C_MakeParticles::overrideLimiter,
            ByteBufCodecs.BOOL, S2C_MakeParticles::alwaysShow,
            Vec3.STREAM_CODEC.apply(ByteBufCodecs.list()), S2C_MakeParticles::directions,
            S2C_MakeParticles::new
    );

    public void handleMessage(IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();
        this.directions.forEach(dir -> level.addParticle(this.particle, this.overrideLimiter, this.alwaysShow, this.x, this.y, this.z, dir.x, dir.y, dir.z));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
