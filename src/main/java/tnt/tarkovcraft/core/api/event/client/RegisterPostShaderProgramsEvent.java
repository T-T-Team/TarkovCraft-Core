package tnt.tarkovcraft.core.api.event.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;

import java.util.*;

public class RegisterPostShaderProgramsEvent extends Event implements IModBusEvent {

    private final List<PostEffectShaderProgram> programs = new ArrayList<>();
    private final Set<Identifier> dynamicPipelines = new HashSet<>();

    public RegisterPostShaderProgramsEvent() {
    }

    public void register(PostEffectShaderProgram program) {
        this.programs.add(program);
    }

    public void registerMany(PostEffectShaderProgram... programs) {
        this.programs.addAll(Arrays.asList(programs));
    }

    public void registerWithDynamicPipeline(PostEffectShaderProgram program, Identifier... pipelines) {
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
