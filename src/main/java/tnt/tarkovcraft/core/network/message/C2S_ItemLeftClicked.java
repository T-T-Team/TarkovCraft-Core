package tnt.tarkovcraft.core.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.common.item.LeftClickListener;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;

public record C2S_ItemLeftClicked(InteractionHand hand) implements CustomPacketPayload {

    public static final ResourceLocation PACKET_ID = TarkovCraftCoreNetwork.createId(C2S_ItemLeftClicked.class);
    public static final Type<C2S_ItemLeftClicked> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<ByteBuf, C2S_ItemLeftClicked> CODEC = StreamCodec.of(
            (buffer, value) -> buffer.writeBoolean(value.hand == InteractionHand.MAIN_HAND),
            buffer -> new C2S_ItemLeftClicked(buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
    );

    @Override
    public Type<C2S_ItemLeftClicked> type() {
        return TYPE;
    }

    public void handleMessage(IPayloadContext context) {
        Player player = context.player();
        ItemStack itemStack = player.getItemInHand(this.hand);
        if (itemStack.getItem() instanceof LeftClickListener listener) {
            listener.onLeftClick(player, player.level(), itemStack, null);
        }
    }
}
