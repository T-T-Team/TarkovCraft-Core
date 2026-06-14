package tnt.tarkovcraft.core.api.event.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.api.client.LabelContainer;
import tnt.tarkovcraft.core.api.client.SynchronizableScreen;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;
import tnt.tarkovcraft.core.client.hint.OnScreenHint;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ClientCoreEventHooks {

    public static void onScreenHintRegister(Consumer<OnScreenHint> consumer) {
        ModLoader.postEvent(new RegisterOnScreenHintEvent(consumer));
    }

    public static Pair<List<PostEffectShaderProgram>, Set<Identifier>> onPostChainShaderRegister(boolean cosmeticShadersEnabled) {
        RegisterPostShaderProgramsEvent event = ModLoader.postEventWithReturn(new RegisterPostShaderProgramsEvent(cosmeticShadersEnabled));
        return Pair.of(
                event.getPrograms(),
                event.getDynamicPipelines()
        );
    }

    public static void onAddCustomProfileLabels(Player player, LabelContainer container) {
        NeoForge.EVENT_BUS.post(new AddPlayerProfileLabelsEvent(player, container));
    }

    public static void onScreenSynchronization(Screen screen, SynchronizableScreen.DataSource dataSource) {
        NeoForge.EVENT_BUS.post(new ScreenSynchronizeEvent(screen, dataSource));
    }
}
