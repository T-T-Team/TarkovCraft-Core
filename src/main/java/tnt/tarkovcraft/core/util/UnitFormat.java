package tnt.tarkovcraft.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Locale;

public record UnitFormat(int decimalPlaces, double multiplier, String suffix) {

    public static final UnitFormat IDENTITY = new UnitFormat(0, 1.0, "");
    public static final UnitFormat PERCENT = new UnitFormat(2, 100.0, "%");
    public static final Codec<UnitFormat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("decimalPlaces", 2).forGetter(UnitFormat::decimalPlaces),
            Codec.DOUBLE.optionalFieldOf("multiplier", 1.0).forGetter(UnitFormat::multiplier),
            Codec.STRING.optionalFieldOf("suffix", "").forGetter(UnitFormat::suffix)
    ).apply(instance, UnitFormat::new));

    public String format(double value) {
        if (Double.isNaN(value)) {
            return CommonLabels.NOT_AVAILABLE_SHORT.getString();
        }
        if (this.decimalPlaces == 0) {
            return String.format(Locale.ROOT, "%d%s", Math.round(value * multiplier), suffix);
        } else {
            String format = "%." + decimalPlaces + "f%s";
            return String.format(Locale.ROOT, format, value * multiplier, suffix);
        }
    }
}
