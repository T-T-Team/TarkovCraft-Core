package tnt.tarkovcraft.core.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleLimit;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import tnt.tarkovcraft.core.common.data.duration.TickValue;

import java.util.Optional;

public abstract class DecalParticle extends SingleQuadParticle {

    public static final ParticleLimit DECAL_INSTANCE_LIMIT = new ParticleLimit(128);
    public static final float MIN_LAYER_OFFSET = 0.005F;
    protected final Direction attachedDirection;
    protected final BlockPos position;
    protected final Vec3 initialPosition;
    protected float fadeOutStart = 0.2F;

    public DecalParticle(ClientLevel level, DecalParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
        this.attachedDirection = options.attachDirection();
        this.position = options.position();
        this.initialPosition = new Vec3(x, y, z);
        this.offsetWithNormal(MIN_LAYER_OFFSET + this.random.nextFloat() * 0.01F);

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.hasPhysics = false;
    }

    @Override
    public final void tick() {
        super.tick();
        float lifetimeAmount = 1.0F - (this.age / (float) this.lifetime);
        this.updateColor(lifetimeAmount);
        if (lifetimeAmount <= this.fadeOutStart) {
            this.setAlpha(lifetimeAmount / this.fadeOutStart);
            this.offsetWithNormal(MIN_LAYER_OFFSET * lifetimeAmount);
        }
        if (this.hasAttachedBlockPhysics() && this.age % 20 == 0) {
            BlockState state = this.level.getBlockState(this.position);
            if (state.isAir()) {
                this.handleAttachedBlockRemoved(state);
            }
        }
    }

    @Override
    public final void extract(QuadParticleRenderState reusedState, Camera camera, float partialTick) {
        Quaternionf rotation = new Quaternionf();
        switch (this.attachedDirection) {
            case UP -> rotation.rotateX(-Mth.PI / 2.0F);
            case DOWN -> rotation.rotateX(Mth.PI / 2.0F);
            case NORTH -> rotation.rotateY(Mth.PI);
            case WEST -> rotation.rotateY(-Mth.PI / 2.0F);
            case EAST -> rotation.rotateY(Mth.PI / 2.0F);
        }
        if (this.roll != 0.0F) {
            rotation.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));
        }
        this.extractRotatedQuad(reusedState, camera, rotation, partialTick);
    }

    @Override
    public Optional<ParticleLimit> getParticleLimit() {
        return Optional.of(DECAL_INSTANCE_LIMIT);
    }

    public final void setFadeOutStartTime(float fadeOutStart) {
        this.fadeOutStart = fadeOutStart;
    }

    public final void setLifetime(TickValue duration) {
        this.setLifetime(duration.tickValue());
    }

    public final void setRoll(float roll) {
        this.roll = roll;
        this.oRoll = roll;
    }

    @Override
    protected Layer getLayer() {
        return this.fadeOutStart > 0 ? Layer.TRANSLUCENT : Layer.OPAQUE;
    }

    protected void updateColor(float lifetimeLeft) {

    }

    protected boolean hasAttachedBlockPhysics() {
        return true;
    }

    protected void handleAttachedBlockRemoved(BlockState state) {
        this.remove();
    }

    private void offsetWithNormal(float amount) {
        Vec3 normal = this.attachedDirection.getUnitVec3().scale(amount);
        this.setPos(this.initialPosition.x + normal.x, this.initialPosition.y + normal.y, this.initialPosition.z + normal.z);
    }
}
