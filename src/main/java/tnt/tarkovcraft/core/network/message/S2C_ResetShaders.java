package tnt.tarkovcraft.core.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.client.shader.PostEffectShaderProgramProcessor;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;

public record S2C_ResetShaders() implements CustomPacketPayload {

    public static final Identifier ID = TarkovCraftCoreNetwork.createId(S2C_ResetShaders.class);
    public static final Type<S2C_ResetShaders> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, S2C_ResetShaders> CODEC = StreamCodec.unit(new S2C_ResetShaders());

    @Override
    public Type<S2C_ResetShaders> type() {
        return TYPE;
    }

    public void handleMessage(IPayloadContext context) {
        PostEffectShaderProgramProcessor.INSTANCE.resetShaders();
    }
}
