package tnt.tarkovcraft.core.client.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import tnt.tarkovcraft.core.api.event.client.ClientCoreEventHooks;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PostEffectShaderProgramProcessor {

    public static final PostEffectShaderProgramProcessor INSTANCE = new PostEffectShaderProgramProcessor();

    private final List<PostEffectShaderProgram> registeredPrograms = new ArrayList<>();
    private final Set<Identifier> dynamicPipelines = new HashSet<>();
    private GpuBufferSlice activeDynamicUniformBuffer;

    private PostEffectShaderProgramProcessor() {
    }

    public void init(boolean allowCosmeticShaders) {
        var registryResult = ClientCoreEventHooks.onPostChainShaderRegister(allowCosmeticShaders);
        synchronized (INSTANCE) {
            this.registeredPrograms.addAll(registryResult.getFirst());
            this.dynamicPipelines.addAll(registryResult.getSecond());
        }
    }

    public boolean isDynamicPipeline(RenderPipeline renderPipeline) {
        return this.dynamicPipelines.contains(renderPipeline.getLocation());
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        Entity camera = client.getCameraEntity();
        if (camera == null || !camera.isAlive() || !(camera instanceof LivingEntity entity)) {
            return;
        }
        this.registeredPrograms.forEach(program -> program.tickProgram(client, entity));
    }

    public void render(Minecraft client, CrossFrameResourcePool resourcePool, DeltaTracker deltaTracker) {
        for (PostEffectShaderProgram program : this.registeredPrograms) {
            if (program.active()) {
                this.processSingleShader(program, client, resourcePool, deltaTracker);
            }
        }
    }

    public @Nullable GpuBufferSlice getActiveDynamicUniformBuffer() {
        return this.activeDynamicUniformBuffer;
    }

    private void processSingleShader(PostEffectShaderProgram program, Minecraft client, CrossFrameResourcePool resourcePool, DeltaTracker deltaTracker) {
        Identifier postChainId = program.postChainId();
        PostChain postChain = client.getShaderManager().getPostChain(postChainId, LevelTargetBundle.MAIN_TARGETS);
        if (postChain != null) {
            RenderSystem.pushPipelineModifier(DynamicTransformsPipelineModifier.KEY);
            program.onRender(deltaTracker);
            this.activeDynamicUniformBuffer = program.getDynamicUniformBuffer();
            GameRenderer gameRenderer = client.gameRenderer;
            postChain.process(gameRenderer.mainRenderTarget(), resourcePool);
            RenderSystem.popPipelineModifier();
        }
        this.activeDynamicUniformBuffer = null;
    }
}
