package tnt.tarkovcraft.core.api.event.client;

import com.google.common.collect.ImmutableList;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;

import java.util.*;

public class RegisterPostShaderProgramsEvent extends Event implements IModBusEvent {

    private final List<PostEffectShaderProgram> programs = new ArrayList<>();

    public RegisterPostShaderProgramsEvent() {
    }

    public void register(PostEffectShaderProgram program) {
        this.programs.add(program);
    }

    public void registerMany(PostEffectShaderProgram... programs) {
        this.programs.addAll(Arrays.asList(programs));
    }

    @ApiStatus.Internal
    public List<PostEffectShaderProgram> getPrograms() {
        return ImmutableList.copyOf(this.programs);
    }
}
