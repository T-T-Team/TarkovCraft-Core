package tnt.tarkovcraft.core.util.helper;

public final class MathHelper {

    public static boolean isWithinBounds(double x, double y, double boundX, double boundY, double width, double height) {
        return x >= boundX && x <= boundX + width && y >= boundY && y <= boundY + height;
    }

    public static boolean areaOverlapsPartial(double p1x, double p1y, double p1w, double p1h, double a1x, double a1y, double a1w, double a1h) {
        if (p1x + p1w < a1x || p1x > a1x + a1w) {
            return false;
        }
        return !(p1y + p1h < a1y) && !(p1y > a1y + a1h);
    }
}
