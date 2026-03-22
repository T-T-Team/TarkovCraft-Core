#version 330

#moj_import <dynamictransforms.glsl>

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform BlindnessConfig {
    float BlurScale;
    vec3 OverlayColor;
};

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec2 sampleStep = oneTexel * vec2(1.0, 0.0);
    int actualRadius = 40;
    vec4 blurred = vec4(0.0);
    for (int i = -actualRadius; i <= actualRadius; ++i)
    {
        blurred += texture(InSampler, texCoord + sampleStep * float(i));
    }
    float sampleCount = float(actualRadius * 2 + 1);
    vec4 blurColor = blurred / sampleCount;
    vec4 original = texture(InSampler, texCoord);
    vec4 blurredColor = mix(original, blurColor, ColorModulator.a);
    fragColor = vec4(mix(blurredColor.rgb, OverlayColor, ColorModulator.a), blurredColor.a);
}