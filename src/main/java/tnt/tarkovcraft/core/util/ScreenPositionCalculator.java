package tnt.tarkovcraft.core.util;

@FunctionalInterface
public interface ScreenPositionCalculator {
    float getPosition(float from, float to, float size);
}
