package tnt.tarkovcraft.core.client.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;

public interface DecalParticleOptions extends ParticleOptions {
    
    Direction attachDirection();
    
    BlockPos position();
}
