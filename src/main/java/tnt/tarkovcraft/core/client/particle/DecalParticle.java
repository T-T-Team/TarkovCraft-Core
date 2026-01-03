package tnt.tarkovcraft.core.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import tnt.tarkovcraft.core.common.data.duration.TickValue;

public abstract class DecalParticle extends TextureSheetParticle {

    public static final float MIN_LAYER_OFFSET = 0.005F;
    protected final Vec3 attachedPosition;
    protected final Direction attachedDirection;
    protected float fadeOutStart = 0.2F;

    public DecalParticle(ClientLevel level, Direction attachedDirection, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.attachedDirection = attachedDirection;
        this.attachedPosition = new Vec3(x, y, z);
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
    }

    @Override
    public final void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Quaternionf rotation = new Quaternionf();
        switch (this.attachedDirection) {
            case UP -> rotation.rotateX(-Mth.PI / 2.0F);
            case DOWN -> rotation.rotateX(Mth.PI / 2.0F);
            case NORTH -> rotation.rotateY(Mth.PI);
            case WEST -> rotation.rotateY(-Mth.PI / 2.0F);
            case EAST -> rotation.rotateY(Mth.PI / 2.0F);
        }
        if (this.roll != 0.0F) {
            rotation.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }
        this.renderRotatedQuad(buffer, camera, rotation, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.fadeOutStart > 0.0F ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT : ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    protected void updateColor(float lifetimeLeft) {

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

    private void offsetWithNormal(float amount) {
        Vec3i normal = this.attachedDirection.getNormal();
        Vec3 scaledNormal = new Vec3(normal.getX(), normal.getY(), normal.getZ()).scale(amount);
        this.setPos(this.attachedPosition.x + scaledNormal.x, this.attachedPosition.y + scaledNormal.y, this.attachedPosition.z + scaledNormal.z);
    }
}
