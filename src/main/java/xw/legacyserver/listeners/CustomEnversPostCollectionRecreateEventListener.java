package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.event.EnversPostCollectionRecreateEventListenerImpl;

public class CustomEnversPostCollectionRecreateEventListener
    extends EnversPostCollectionRecreateEventListenerImpl {
    protected CustomEnversPostCollectionRecreateEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
