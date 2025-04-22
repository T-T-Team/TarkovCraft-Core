package tnt.tarkovcraft.core.util;

public enum VerticalAlignment implements ScreenPositionCalculator {

    TOP((from, to, size) -> from),
    CENTER((from, to, size) -> from + (from - to - size) / 2.0F),
    BOTTOM((from, to, size) -> to - size);

    private final ScreenPositionCalculator delegate;

    VerticalAlignment(ScreenPositionCalculator delegate) {
        this.delegate = delegate;
    }

    @Override
    public float getPosition(float from, float to, float size) {
        return this.delegate.getPosition(from, to, size);
    }
}
