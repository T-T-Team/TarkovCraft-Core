package tnt.tarkovcraft.core.util;

public final class MathHelper {

    public static boolean isWithinBounds(double x, double y, double boundX, double boundY, double width, double height) {
        return x >= boundX && x <= boundX + width && y >= boundY && y <= boundY + height;
    }

    public static boolean areaOverlapsPartial(double x, double y, double areaWidth, double areaHeight, double boundX, double boundY, double width, double height) {
        double xMax1 = x + areaWidth;
        double yMax1 = y + areaHeight;
        double xMax2 = boundX + width;
        double yMax2 = boundY + height;
        return !(xMax1 < boundX) && !(xMax2 < x) && !(yMax1 < boundY) && !(yMax2 < y);
    }
}
