package tnt.tarkovcraft.core.common.event;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import tnt.tarkovcraft.core.common.mail.MailManager;
import tnt.tarkovcraft.core.common.mail.MailMessage;
import tnt.tarkovcraft.core.common.mail.MailSource;

public abstract class MailEvent extends Event {

    private final MailMessage message;
    private final MailSource source;
    private final MailSource destination;

    public MailEvent(MailMessage message, MailSource source, MailSource destination) {
        this.message = message;
        this.source = source;
        this.destination = destination;
    }

    public MailMessage getMessage() {
        return message;
    }

    public MailSource getSource() {
        return source;
    }

    public MailSource getDestination() {
        return destination;
    }

    public static final class MailSendingEvent extends MailEvent implements ICancellableEvent {

        private final Level level;

        public MailSendingEvent(Level level, MailMessage message, MailSource source, MailSource destination) {
            super(message, source, destination);
            this.level = level;
        }

        public Level getLevel() {
            return level;
        }
    }

    public static final class OnMailReceiveAttempt extends MailEvent {

        private final Level level;
        private final MailManager mailManager;
        private final boolean isBlocked;

        public OnMailReceiveAttempt(MailMessage message, MailSource source, MailSource destination, MailManager manager, Level level, boolean blocked) {
            super(message, source, destination);
            this.mailManager = manager;
            this.level = level;
            this.isBlocked = blocked;
        }

        public boolean isBlocked() {
            return isBlocked;
        }

        public MailManager getMailManager() {
            return mailManager;
        }

        public Level getLevel() {
            return level;
        }
    }
}
