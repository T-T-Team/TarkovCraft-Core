package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.util.Mth;

public interface Scrollable {

    /**
     * Retrieves the current scroll position.
     *
     * @return the current scroll position as a double value.
     */
    double getScroll();


    /**
     * Sets the scroll position to the specified value.
     *
     * @param scroll the new scroll position as a double value.
     */
    void setScroll(double scroll);

    /**
     * Retrieves the visible size of the scrollable content area. Used to render scrollbars.
     *
     * @return the visible size as a double value, typically representing the height or width
     *         of the scrollable area depending on the implementation.
     */
    double getVisibleSize();

    /**
     * Calculates and retrieves the maximum scroll value for the scrollable content.
     * The maximum scroll value typically represents the total content size minus
     * the visible size, indicating how far the view can scroll.
     *
     * @return the maximum scroll value as a double. If there are no scrollable items
     *         or the visible size equals or exceeds total content size, returns 0.0.
     */
    double getMaxScroll();

    static double scroll(Scrollable scrollable, double amount) {
        return scroll(scrollable, amount, 4.0D);
    }

    static double scroll(Scrollable scrollable, double amount, double speed) {
        double limit = scrollable.getMaxScroll();
        if (limit <= 0.0D) {
            return scrollable.getScroll();
        }
        double scrollAmount = speed * amount;
        return Mth.clamp(scrollable.getScroll() - scrollAmount, 0.0D, limit);
    }
}
