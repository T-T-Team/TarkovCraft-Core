package tnt.tarkovcraft.core.client.notification;

import tnt.tarkovcraft.core.common.Notification;

import java.util.ArrayDeque;
import java.util.Deque;

public class NotificationChannel {

    public static final NotificationChannel MAIN = new NotificationChannel();
    private final Deque<ClientNotification> notifications = new ArrayDeque<>();

    private NotificationChannel() {}

    public void add(Notification notification) {
        this.notifications.push(new ClientNotification(notification));
    }

    public void update() {
        this.notifications.removeIf(ClientNotification::tickNotification);
    }

    public Deque<ClientNotification> getNotifications() {
        return this.notifications;
    }

    public void clearAllNotifications() {
        this.notifications.clear();
    }
}
