package tnt.tarkovcraft.core.common.data.duration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Duration(DurationUnit unit, double value) implements TickValue {

    public static final Pattern PATTERN = Pattern.compile("(?<value>-?\\d+)(?<unit>[a-zA-Z]+)");
    public static final Codec<Duration> STRING_CODEC = Codec.STRING.comapFlatMap(expr -> {
        try {
            return DataResult.success(Duration.parse(expr));
        } catch (Exception e) {
            return DataResult.error(() -> "Failed to parse duration '" + expr + "': " + e.getMessage());
        }
    }, duration -> duration.tickValue() + DurationUnit.TICK.sign());

    public static Duration ticks(int ticks) {
        return new Duration(DurationUnit.TICK, ticks);
    }

    public static Duration seconds(int seconds) {
        return new Duration(DurationUnit.SECONDS, seconds);
    }

    public static Duration minutes(int minutes) {
        return new Duration(DurationUnit.MINUTES, minutes);
    }

    public static Duration hours(int hours) {
        return new Duration(DurationUnit.HOURS, hours);
    }

    public static Duration days(int days) {
        return new Duration(DurationUnit.DAYS, days);
    }

    public static Duration convertFromTicks(int ticks, DurationUnit outputUnit) {
        int unitValue = outputUnit.unitValue();
        return new Duration(outputUnit, ticks / (double) unitValue);
    }

    public Duration using(DurationUnit unit) {
        int tickValue = this.tickValue();
        return convertFromTicks(tickValue, unit);
    }

    public Duration add(Duration duration) {
        return convertFromTicks(this.tickValue() + duration.tickValue(), this.unit());
    }

    public Duration addMany(Collection<Duration> durations) {
        int totalTickValue = this.tickValue() + durations.stream().mapToInt(Duration::tickValue).sum();
        return convertFromTicks(totalTickValue, this.unit());
    }

    public Component format(DurationFormatSettings settings, DurationFormatter formatter) {
        return Component.literal(formatter.toLocalizedString(settings, this.tickValue()));
    }

    public Component format(DurationFormatter formatter) {
        return format(new DurationFormatSettings(), formatter);
    }

    public Component format() {
        return format(DurationFormats.LONG_NAME);
    }

    public static Component format(int ticks, DurationFormatSettings settings, DurationFormatter formatter) {
        return Duration.ticks(ticks).format(settings, formatter);
    }

    public static Component format(int ticks, DurationFormatter formatter) {
        return format(ticks, new DurationFormatSettings(), formatter);
    }

    public static Component format(int ticks) {
        return format(ticks, DurationFormats.LONG_NAME);
    }

    public String toDurationString() {
        return value() + unit().sign();
    }

    public static Duration parse(String inputString) {
        Matcher matcher = PATTERN.matcher(inputString);
        int result = 0;
        while (matcher.find()) {
            String value = matcher.group("value");
            String unit = matcher.group("unit");
            int unitValue = Integer.parseInt(value);
            DurationUnit durationUnit = DurationUnit.getBySign(unit);
            result += new Duration(durationUnit, unitValue).tickValue();
        }
        if (result <= 0) {
            throw new IllegalArgumentException("Invalid duration format or value: " + inputString);
        }
        return Duration.ticks(result);
    }

    @Override
    public String toString() {
        return String.format("Duration[v=%f,u=%s,t=%d]", this.value(), this.unit(), this.tickValue());
    }

    @Override
    public int tickValue() {
        int unitValue = this.unit().unitValue();
        return Mth.floor(unitValue * this.value());
    }
}
