package tnt.tarkovcraft.core.common.data.duration;

import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public enum DurationFormats implements DurationFormatter {

    LONG_NAME(DurationFormats::formatFullNameDuration),
    SHORT_NAME(DurationFormats::formatUnitNameDuration),
    TIME(DurationFormats::formatTimeDuration);

    private final DurationFormatter formatter;

    DurationFormats(DurationFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String toLocalizedString(DurationFormatSettings settings, int value) {
        return this.formatter.toLocalizedString(settings, value);
    }

    public static String formatFullNameDuration(DurationFormatSettings settings, int value) {
        return format(settings, value, (unit, unitValue) -> " " + unit.getLocalizedName(unitValue).getString(), UnaryOperator.identity());
    }

    public static String formatUnitNameDuration(DurationFormatSettings settings, int value) {
        return format(settings, value, (unit, unitValue) -> unitValue + unit.getShortName().getString(), UnaryOperator.identity());
    }

    public static String formatTimeDuration(DurationFormatSettings settings, int value) {
        settings.setNumberFormat(val -> StringUtils.leftPad(String.valueOf(val), 2, "0"));
        return format(settings, value, (unit, unitValue) -> " " + unitValue, out -> out.replaceAll("\\s+", ":"));
    }

    public static String format(DurationFormatSettings settings, int value, BiFunction<Unit, String, String> output, UnaryOperator<String> post) {
        StringBuilder builder = new StringBuilder();
        for (Unit unit : settings.getUnits()) {
            int unitValue = value / unit.value();
            if (unitValue > 0 || settings.isIncludeZeroValues()) {
                String strValue = settings.getNumberFormat().apply(unitValue);
                builder.append(output.apply(unit, strValue));
            }
            value %= unit.value();
        }
        String out = builder.toString().trim();
        return post.apply(out);
    }
}
