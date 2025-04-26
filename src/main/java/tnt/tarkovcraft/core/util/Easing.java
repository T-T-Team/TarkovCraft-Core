package tnt.tarkovcraft.core.util;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.util.Mth;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * <a href="https://easings.net/#">Source - easings.net</a>
 */
public enum Easing implements UnaryOperator<Float> {

    // TODO add more easings
    LINEAR(FloatUnaryOperator.identity()),
    EASE_IN_SINE(f -> 1.0F - Mth.cos((float)(f * Math.PI) / 2.0F)),
    EASE_OUT_SINE(f -> Mth.sin((float)(f * Math.PI) / 2.0F)),
    EASE_IN_OUT_SINE(f -> -(Mth.cos((float)(Math.PI * f) - 1.0F) / 2.0F));

    private final FloatUnaryOperator easing;

    Easing(FloatUnaryOperator easing) {
        this.easing = easing;
    }

    @Override
    public Float apply(Float aFloat) {
        return this.ease(aFloat);
    }

    public float ease(float in) {
        return this.easing.apply(in);
    }

    public static float ease(float in, Function<Float, Float> easingFunction) {
        return easingFunction.apply(in);
    }
}
