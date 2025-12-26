package tnt.tarkovcraft.core.client.shader;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.client.pipeline.PipelineModifier;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.util.HashSet;
import java.util.Set;

public final class DynamicTransformsPipelineModifier implements PipelineModifier {

    public static final ResourceKey<PipelineModifier> KEY = ResourceKey.create(PipelineModifier.MODIFIERS_KEY, TarkovCraftCore.createIdentifier("dynamic_transforms_modifier"));
    private static final Set<Identifier> TARGET_PIPELINES = new HashSet<>();

    public static void addTargetPipeline(Identifier pipeline) {
        synchronized (TARGET_PIPELINES) {
            TARGET_PIPELINES.add(pipeline);
        }
    }

    @Override
    public RenderPipeline apply(RenderPipeline renderPipeline, Identifier identifier) {
        if (TARGET_PIPELINES.contains(renderPipeline.getLocation())) {
            return renderPipeline.toBuilder()
                    .withLocation(identifier)
                    .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
                    .build();
        }
        return renderPipeline;
    }
}
