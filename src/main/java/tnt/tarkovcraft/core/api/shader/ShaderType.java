package tnt.tarkovcraft.core.api.shader;

public enum ShaderType {

    // Game shaders are always enabled
    GAME,
    // Cosmetic shaders can be disabled via config
    COSMETIC;

    public boolean isCoreShader() {
        return this == GAME;
    }

    public boolean isCosmetic() {
        return this == COSMETIC;
    }
}
