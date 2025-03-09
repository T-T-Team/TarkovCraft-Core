package tnt.tarkovcraft.core.client.screen.widget;

public interface Scrollable {

    double getScroll();

    void setScroll(double scroll);

    double getVisibleSize();

    double getMaxScroll();

    static double scroll(Scrollable scrollable, double amount) {
        return scrollable.getScroll() + amount; // TODO implement
    }
}
