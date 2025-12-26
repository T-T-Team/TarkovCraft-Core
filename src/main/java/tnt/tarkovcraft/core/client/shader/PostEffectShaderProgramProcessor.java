package tnt.tarkovcraft.core.client.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModLoader;
import org.jspecify.annotations.Nullable;
import tnt.tarkovcraft.core.api.event.client.RegisterPostShaderProgramsEvent;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;

import java.util.ArrayList;
import java.util.List;

public final class PostEffectShaderProgramProcessor {

    public static final PostEffectShaderProgramProcessor INSTANCE = new PostEffectShaderProgramProcessor();

    private final List<PostEffectShaderProgram> registeredPrograms = new ArrayList<>();
    private GpuBufferSlice activeDynamicUniformBuffer;

    private PostEffectShaderProgramProcessor() {
    }

    public void init() {
        RegisterPostShaderProgramsEvent event = ModLoader.postEventWithReturn(new RegisterPostShaderProgramsEvent());
        this.registeredPrograms.addAll(event.getPrograms());
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        Entity camera = client.getCameraEntity();
        if (camera == null || !camera.isAlive() || !(camera instanceof LivingEntity entity)) {
            return;
        }
        this.registeredPrograms.forEach(program -> program.tickProgram(client, entity));
    }

    public void render(Minecraft client, CrossFrameResourcePool resourcePool) {
        for (PostEffectShaderProgram program : this.registeredPrograms) {
            if (program.active()) {
                this.processSingleShader(program, client, resourcePool);
            }
        }
    }

    public @Nullable GpuBufferSlice getActiveDynamicUniformBuffer() {
        return this.activeDynamicUniformBuffer;
    }

    private void processSingleShader(PostEffectShaderProgram program, Minecraft client, CrossFrameResourcePool resourcePool) {
        Identifier postChainId = program.postChainId();
        PostChain postChain = client.getShaderManager().getPostChain(postChainId, LevelTargetBundle.MAIN_TARGETS);
        if (postChain != null) {
            RenderSystem.pushPipelineModifier(DynamicTransformsPipelineModifier.KEY);
            this.activeDynamicUniformBuffer = program.getDynamicUniformBuffer();
            postChain.process(client.getMainRenderTarget(), resourcePool);
            RenderSystem.popPipelineModifier();
        }
        this.activeDynamicUniformBuffer = null;
    }
}
