package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.event.EnversPostUpdateEventListenerImpl;

public class CustomEnversPostUpdateEventListener
    extends EnversPostUpdateEventListenerImpl {
    protected CustomEnversPostUpdateEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
