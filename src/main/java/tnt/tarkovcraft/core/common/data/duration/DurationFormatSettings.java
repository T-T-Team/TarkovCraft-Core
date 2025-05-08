package tnt.tarkovcraft.core.common.data.duration;

import java.util.function.Function;

public final class DurationFormatSettings {

    private Iterable<? extends Unit> units;
    private boolean includeZeroValues;
    private Function<Integer, String> numberFormat;

    public DurationFormatSettings() {
        this.units = DurationUnit.DEFAULT_FORMAT_UNITS;
        this.includeZeroValues = false;
        this.numberFormat = String::valueOf;
    }

    public void setUnits(Iterable<Unit> units) {
        this.units = units;
    }

    public void setIncludeZeroValues(boolean includeZeroValues) {
        this.includeZeroValues = includeZeroValues;
    }

    public void setNumberFormat(Function<Integer, String> numberFormat) {
        this.numberFormat = numberFormat;
    }

    public Iterable<? extends Unit> getUnits() {
        return units;
    }

    public boolean isIncludeZeroValues() {
        return includeZeroValues;
    }

    public Function<Integer, String> getNumberFormat() {
        return numberFormat;
    }
}
