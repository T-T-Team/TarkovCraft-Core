package tnt.tarkovcraft.core.api.event.client;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHint;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHintRenderer;
import tnt.tarkovcraft.core.api.client.hint.TextHint;
import tnt.tarkovcraft.core.client.hint.OnScreenHintManager;

public class RegisterOnScreenHintEvent extends Event implements IModBusEvent {

    private final OnScreenHintManager manager;

    public RegisterOnScreenHintEvent(OnScreenHintManager manager) {
        this.manager = manager;
    }

    public void registerHint(OnScreenHint hint) {
        this.manager.registerHint(hint);
    }

    public <T extends OnScreenHint> void registerHint(T hint, OnScreenHintRenderer<? super T> renderer) {
        this.manager.registerHint(hint, renderer);
    }

    public <T extends OnScreenHint & TextHint> void registerActionHint(T hint) {
        this.manager.registerActionHint(hint);
    }

    public <T extends OnScreenHint & TextHint> void registerActionHint(T hint, OnScreenHintRenderer<? super T> customTextRenderer) {
        this.manager.registerActionHint(hint, customTextRenderer);
    }
}
