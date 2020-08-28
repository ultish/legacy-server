package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.spi.AuditConfiguration;
import org.hibernate.envers.event.spi.EnversPostDeleteEventListenerImpl;

public class CustomEnversPostDeleteEventListener
    extends EnversPostDeleteEventListenerImpl {
    protected CustomEnversPostDeleteEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
