package tnt.tarkovcraft.core.util;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public record UserActionResult<T>(T value, Component message) {

    public static <T> UserActionResult<T> success(T value) {
        return new UserActionResult<>(value, CommonComponents.EMPTY);
    }

    public static <T> UserActionResult<T> failure(Component message) {
        return new UserActionResult<>(null, message);
    }

    public boolean isSuccess() {
        return this.value != null;
    }
}
