package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.event.EnversPreCollectionUpdateEventListenerImpl;

public class CustomEnversPreCollectionUpdateEventListener
    extends EnversPreCollectionUpdateEventListenerImpl {
    protected CustomEnversPreCollectionUpdateEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
