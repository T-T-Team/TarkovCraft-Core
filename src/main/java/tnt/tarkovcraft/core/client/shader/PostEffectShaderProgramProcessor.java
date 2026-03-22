package tnt.tarkovcraft.core.client.shader;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.event.client.RegisterPostShaderProgramsEvent;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;

import java.util.*;

public final class PostEffectShaderProgramProcessor {

    public static final Marker MARKER = MarkerManager.getMarker("Shaders");
    public static final PostEffectShaderProgramProcessor INSTANCE = new PostEffectShaderProgramProcessor();
    private final List<PostEffectShaderProgram> registeredShaders = new ArrayList<>();
    private final Set<PostEffectShaderProgram> pendingActivation = new HashSet<>();
    private final Set<ResourceLocation> activeShaderIds = new HashSet<>();
    private final Set<ShaderInstanceHolder> activeShaders = new HashSet<>();

    private PostEffectShaderProgramProcessor() {}

    public void init() {
        RegisterPostShaderProgramsEvent event = ModLoader.postEventWithReturn(new RegisterPostShaderProgramsEvent());
        synchronized (INSTANCE) {
            this.registeredShaders.addAll(event.getPrograms());
        }
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        Entity cameraEntity = client.getCameraEntity();
        if (
                client.level == null ||
                        cameraEntity == null ||
                        !cameraEntity.isAlive() ||
                        !(cameraEntity instanceof LivingEntity entity)
        ) {
            return;
        }
        this.registeredShaders.forEach(program -> {
            program.tickProgram(client, entity);
            if (!this.activeShaderIds.contains(program.postChainId()) && program.active()) {
                TarkovCraftCore.LOGGER.debug(MARKER, "Activating shader {}", program.postChainId());
                this.pendingActivation.add(program);
            }
        });
    }

    public void render(float delta) {
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        this.loadPendingShaders();
        Iterator<ShaderInstanceHolder> iterator = this.activeShaders.iterator();
        while (iterator.hasNext()) {
            ShaderInstanceHolder shader = iterator.next();
            if (!shader.canRender()) {
                TarkovCraftCore.LOGGER.debug(MARKER, "Disabling shader {}", shader);
                shader.close();
                this.activeShaderIds.remove(shader.program.postChainId());
                iterator.remove();
                return;
            }
            shader.render(delta);
        }
    }

    public void resize(int width, int height) {
        this.activeShaders.forEach(shader -> shader.resize(width, height));
    }

    private void loadPendingShaders() {
        Minecraft minecraft = Minecraft.getInstance();
        Iterator<PostEffectShaderProgram> iterator = this.pendingActivation.iterator();
        while (iterator.hasNext()) {
            PostEffectShaderProgram program = iterator.next();
            ResourceLocation shaderLocation = program.postChainId().withPath(id -> "shaders/post/" + id + ".json");
            TarkovCraftCore.LOGGER.debug(MARKER, "Loading post effect shader {}", shaderLocation);
            try {
                PostChain postChain = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), minecraft.getMainRenderTarget(), shaderLocation);
                Window window = minecraft.getWindow();
                ShaderInstanceHolder shader = new ShaderInstanceHolder(program, postChain);
                shader.resize(window);
                this.activeShaders.add(shader);
                this.activeShaderIds.add(program.postChainId());
            } catch (Exception e) {
                TarkovCraftCore.LOGGER.error(MARKER, "Failed to load post effect shader {}", shaderLocation, e);
            }
            iterator.remove();
        }
    }

    private record ShaderInstanceHolder(PostEffectShaderProgram program, PostChain postChain) {

        boolean canRender() {
            return this.program.active();
        }

        void render(float delta) {
            this.program.onRender(delta, this.postChain::setUniform);
            this.postChain.process(delta);
        }

        void resize(Window window) {
            this.resize(window.getWidth(), window.getHeight());
        }

        void resize(int width, int height) {
            this.postChain.resize(width, height);
        }

        void close() {
            this.postChain.close();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof PostEffectShaderProgram shaderProgram) {
                return Objects.equals(this.program.postChainId(), shaderProgram.postChainId());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(program.postChainId());
        }

        @Override
        public @NotNull String toString() {
            return this.program.postChainId().toString();
        }
    }
}
