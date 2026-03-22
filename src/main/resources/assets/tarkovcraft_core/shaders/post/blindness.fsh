#version 150

uniform sampler2D DiffuseSampler;
uniform float Scale;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec2 sampleStep = oneTexel * vec2(1.0, 0.0);
    int actualRadius = 40;
    vec4 blurred = vec4(0.0);
    for (int i = -actualRadius; i <= actualRadius; ++i)
    {
        blurred += texture(DiffuseSampler, texCoord + sampleStep * float(i));
    }
    float sampleCount = float(actualRadius * 2 + 1);
    vec4 blurColor = blurred / sampleCount;
    vec4 original = texture(DiffuseSampler, texCoord);
    vec4 blurredColor = mix(original, blurColor, Scale);
    fragColor = vec4(mix(blurredColor.rgb, OverlayColor, Scale), blurredColor.a);
}