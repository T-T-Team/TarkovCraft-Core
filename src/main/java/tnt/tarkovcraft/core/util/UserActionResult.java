package tnt.tarkovcraft.core.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public record UserActionResult<T>(ResultType resultType, T value, Component message) {

    public static <T> UserActionResult<T> success(T value) {
        return new UserActionResult<>(ResultType.SUCCESS, value, CommonComponents.EMPTY);
    }

    public static UserActionResult<Void> successEmpty() {
        return success(null);
    }

    public static <T> UserActionResult<T> failure(Component message) {
        return new UserActionResult<>(ResultType.FAILURE, null, message);
    }

    public boolean isSuccess() {
        return this.resultType == ResultType.SUCCESS;
    }

    public boolean isFailure() {
        return this.resultType == ResultType.FAILURE;
    }

    public Either<T, Component> toEither() {
        return this.isSuccess() ? Either.left(this.value) : Either.right(this.message);
    }

    public enum ResultType {
        SUCCESS, FAILURE
    }
}
