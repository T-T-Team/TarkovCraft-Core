package tnt.tarkovcraft.core.api.event.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;

import java.util.*;

public class RegisterPostShaderProgramsEvent extends Event implements IModBusEvent {

    private final List<PostEffectShaderProgram> programs = new ArrayList<>();

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

    @ApiStatus.Internal
    public List<PostEffectShaderProgram> getPrograms() {
        return ImmutableList.copyOf(this.programs);
    }
}
