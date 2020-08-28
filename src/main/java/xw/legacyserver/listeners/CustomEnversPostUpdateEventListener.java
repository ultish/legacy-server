package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.spi.AuditConfiguration;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;

public class CustomEnversPostUpdateEventListener
    extends EnversPostUpdateEventListenerImpl {
    protected CustomEnversPostUpdateEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
