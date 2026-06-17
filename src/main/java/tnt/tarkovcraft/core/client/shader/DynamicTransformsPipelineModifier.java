package tnt.tarkovcraft.core.client.shader;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.client.pipeline.PipelineModifier;
import tnt.tarkovcraft.core.TarkovCraftCore;

public final class DynamicTransformsPipelineModifier implements PipelineModifier {

    public static final ResourceKey<PipelineModifier> KEY = ResourceKey.create(PipelineModifier.MODIFIERS_KEY, TarkovCraftCore.createIdentifier("dynamic_transforms_modifier"));

    @Override
    public RenderPipeline apply(RenderPipeline renderPipeline, Identifier identifier) {
        if (PostEffectShaderProgramProcessor.INSTANCE.isDynamicPipeline(renderPipeline)) {
            return renderPipeline.toBuilder()
                    .withLocation(identifier)
                    .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
                    .build();
        }
        return renderPipeline;
    }
}
