package tnt.tarkovcraft.core.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Duration(DurationUnit unit, double value) implements TickValue {

    public static final Pattern PATTERN = Pattern.compile("(?<value>-?\\d+)(?<unit>[a-zA-Z]+)");
    public static final Codec<Duration> STRING_CODEC = Codec.STRING.comapFlatMap(expr -> {
        Matcher matcher = PATTERN.matcher(expr);
        if (!matcher.matches())
            return DataResult.error(() -> "Invalid duration format: " + expr);
        String value = matcher.group("value");
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Invalid number format: " + e);
        }
        String unit = matcher.group("unit");
        DurationUnit durationUnit;
        try {
            durationUnit = DurationUnit.getBySign(unit);
        } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Invalid duration unit: " + e);
        }
        return DataResult.success(new Duration(durationUnit, intValue));
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

    public Component format() {
        int value = this.tickValue();
        return format(value);
    }

    public static Component format(int ticks) {
        MutableComponent component = Component.literal("");
        int days = ticks / DurationUnit.DAYS.unitValue();
        ticks %= DurationUnit.DAYS.unitValue();
        if (days > 0)
            component.append(DurationUnit.DAYS.getLocalizedName(days));
        int hours = ticks / DurationUnit.HOURS.unitValue();
        ticks %= DurationUnit.HOURS.unitValue();
        if (hours > 0)
            component.append(DurationUnit.HOURS.getLocalizedName(hours));
        int minutes = ticks / DurationUnit.MINUTES.unitValue();
        ticks %= DurationUnit.MINUTES.unitValue();
        if (minutes > 0)
            component.append(DurationUnit.MINUTES.getLocalizedName(minutes));
        int seconds = ticks / DurationUnit.SECONDS.unitValue();
        if (seconds > 0)
            component.append(DurationUnit.SECONDS.getLocalizedName(seconds));
        return component;
    }

    public String toDurationString() {
        return value() + unit().sign();
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
