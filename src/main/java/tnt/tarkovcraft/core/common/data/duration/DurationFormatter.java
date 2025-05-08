package tnt.tarkovcraft.core.common.data.duration;

public interface DurationFormatter {

    String toLocalizedString(DurationFormatSettings settings, int value);
}
