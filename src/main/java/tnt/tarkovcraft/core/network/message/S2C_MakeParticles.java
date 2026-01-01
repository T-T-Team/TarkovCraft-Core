package tnt.tarkovcraft.core.network.message;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.List;

public record S2C_MakeParticles(ParticleOptions particle, double x, double y, double z, boolean overrideLimiter, boolean alwaysShow, List<Vec3> directions) implements CustomPacketPayload {

    public static final ResourceLocation ID = TarkovCraftCoreNetwork.createId(S2C_MakeParticles.class);
    public static final Type<S2C_MakeParticles> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2C_MakeParticles> CODEC = StreamCodec.of(
            (buf, pkt) -> pkt.encode(buf),
            S2C_MakeParticles::decode
    );

    private void encode(RegistryFriendlyByteBuf buf) {
        ParticleTypes.STREAM_CODEC.encode(buf, this.particle);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeBoolean(this.overrideLimiter);
        buf.writeBoolean(this.alwaysShow);
        Codecs.VEC3_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, this.directions);
    }

    private static S2C_MakeParticles decode(RegistryFriendlyByteBuf buf) {
        return new S2C_MakeParticles(
                ParticleTypes.STREAM_CODEC.decode(buf),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readBoolean(),
                buf.readBoolean(),
                Codecs.VEC3_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf)
        );
    }

    public void handleMessage(IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();
        this.directions.forEach(dir -> {
            if (this.alwaysShow) {
                level.addAlwaysVisibleParticle(this.particle, this.overrideLimiter, this.x, this.y, this.z, dir.x, dir.y, dir.z);
            } else {
                level.addParticle(this.particle, this.overrideLimiter, this.x, this.y, this.z, dir.x, dir.y, dir.z);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
