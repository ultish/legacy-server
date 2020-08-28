package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.spi.AuditConfiguration;
import org.hibernate.envers.event.spi.EnversPreCollectionUpdateEventListenerImpl;

public class CustomEnversPreCollectionUpdateEventListener
    extends EnversPreCollectionUpdateEventListenerImpl {
    protected CustomEnversPreCollectionUpdateEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
