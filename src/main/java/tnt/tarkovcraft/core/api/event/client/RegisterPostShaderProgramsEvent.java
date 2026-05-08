package tnt.tarkovcraft.core.api.event.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;

import java.util.*;

public class RegisterPostShaderProgramsEvent extends Event implements IModBusEvent {

    private final List<PostEffectShaderProgram> programs = new ArrayList<>();
    private final Set<Identifier> dynamicPipelines = new HashSet<>();

    private final boolean allowCosmeticShaderPrograms;

    public RegisterPostShaderProgramsEvent(boolean allowCosmeticShaderPrograms) {
        this.allowCosmeticShaderPrograms = allowCosmeticShaderPrograms;
    }

    public void register(PostEffectShaderProgram program) {
        if (!this.allowCosmeticShaderPrograms && program.getShaderType().isCosmetic()) {
            TarkovCraftCore.LOGGER.debug("Skipping registration of cosmetic shader program '{}'", program.postChainId());
            return;
        }
        this.programs.add(program);
    }

    public void registerMany(PostEffectShaderProgram... programs) {
        for (PostEffectShaderProgram program : programs) {
            this.register(program);
        }
    }

    public void registerWithDynamicPipeline(PostEffectShaderProgram program, Identifier... pipelines) {
        if (!this.allowCosmeticShaderPrograms && program.getShaderType().isCosmetic()) {
            TarkovCraftCore.LOGGER.debug("Skipping registration of cosmetic pipeline and shader program '{}'", program.postChainId());
            return;
        }
        this.programs.add(program);
        this.dynamicPipelines.addAll(Arrays.asList(pipelines));
    }

    public void registerDynamicPipeline(Identifier identifier) {
        this.dynamicPipelines.add(identifier);
    }

    @ApiStatus.Internal
    public List<PostEffectShaderProgram> getPrograms() {
        return ImmutableList.copyOf(this.programs);
    }

    @ApiStatus.Internal
    public Set<Identifier> getDynamicPipelines() {
        return Set.copyOf(this.dynamicPipelines);
    }
}
