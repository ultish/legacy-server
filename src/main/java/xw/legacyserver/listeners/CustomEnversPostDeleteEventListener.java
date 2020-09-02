package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.event.EnversPostDeleteEventListenerImpl;

public class CustomEnversPostDeleteEventListener
    extends EnversPostDeleteEventListenerImpl {
    protected CustomEnversPostDeleteEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
