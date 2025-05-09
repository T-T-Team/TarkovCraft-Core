package tnt.tarkovcraft.core.common.data.duration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DurationUnit(String sign, int unitValue) implements TemporalUnit, Unit {

    private static final Map<String, DurationUnit> units = new HashMap<>();
    public static final Codec<DurationUnit> CODEC = Codec.STRING.comapFlatMap(sign -> {
        DurationUnit unit = units.get(sign);
        return unit != null ? DataResult.success(unit) : DataResult.error(() -> "Unknown unit: " + sign);
    }, DurationUnit::sign);

    public static final DurationUnit TICK = new DurationUnit("t", 1);
    public static final DurationUnit SECONDS = new DurationUnit("s", 20);
    public static final DurationUnit MINUTES = new DurationUnit("m", 1_200);
    public static final DurationUnit MINECRAFT_DAYS = new DurationUnit("md", 24_000);
    public static final DurationUnit HOURS = new DurationUnit("h", 72_000);
    public static final DurationUnit DAYS = new DurationUnit("d", 1_728_000);

    public static final List<DurationUnit> DEFAULT_FORMAT_UNITS = Arrays.asList(DAYS, HOURS, MINUTES, SECONDS);

    public DurationUnit(String sign, int unitValue) {
        this.sign = sign;
        this.unitValue = unitValue;
        units.put(sign, this);
    }

    @Override
    public Duration getDuration() {
        return Duration.ofMillis(this.unitValue() * 50L);
    }

    @Override
    public Component getShortName() {
        return Component.translatable("duration.unit." + this.sign + ".short");
    }

    @Override
    public int value() {
        return this.unitValue;
    }

    @Override
    public Component getLocalizedName(String value) {
        return Component.translatable("duration.unit." + this.sign, value);
    }

    @Override
    public boolean isDurationEstimated() {
        return this.isDateBased();
    }

    @Override
    public boolean isDateBased() {
        return this.unitValue() >= DAYS.unitValue();
    }

    @Override
    public boolean isTimeBased() {
        return !this.isDateBased();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R) temporal.plus(amount, this);
    }

    @Override
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return temporal1Inclusive.until(temporal2Exclusive, this);
    }

    public static DurationUnit getBySign(String sign) {
        DurationUnit unit = units.get(sign);
        if (unit == null) {
            throw new IllegalArgumentException("Unknown sign: " + sign);
        }
        return unit;
    }
}
