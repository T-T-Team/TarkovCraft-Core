package tnt.tarkovcraft.core.util;

public enum HorizontalAlignment implements ScreenPositionCalculator {

    LEFT((from, to, size) -> from),
    CENTER((from, to, size) -> from + (from - to - size) / 2.0F),
    RIGHT((from, to, size) -> to - size);

    private final ScreenPositionCalculator delegate;

    HorizontalAlignment(ScreenPositionCalculator delegate) {
        this.delegate = delegate;
    }

    @Override
    public float getPosition(float from, float to, float size) {
        return this.delegate.getPosition(from, to, size);
    }
}
