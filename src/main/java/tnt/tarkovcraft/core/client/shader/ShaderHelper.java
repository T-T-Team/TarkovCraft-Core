package tnt.tarkovcraft.core.client.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.Identifier;
import org.joml.*;

public final class ShaderHelper {

    public static final Vector4fc COLOR_MODULATOR = new Vector4f();
    public static final Vector3fc MODEL_OFFSET = new Vector3f();
    public static final Matrix4fc EMPTY_MATRIX = new Matrix4f();

    public static Vector4fc alphaModulator(float strength) {
        return new Vector4f(0.0F, 0.0F, 0.0F, strength);
    }

    public static GpuBufferSlice scaleTransform(float strength) {
        return RenderSystem.getDynamicUniforms().writeTransform(
                RenderSystem.getModelViewMatrix(),
                alphaModulator(strength),
                MODEL_OFFSET,
                EMPTY_MATRIX
        );
    }

    public static Identifier getPostChainPipeline(Identifier postChain, int pipelineIndex) {
        return postChain.withPath(path -> path + "/" + pipelineIndex);
    }
}
