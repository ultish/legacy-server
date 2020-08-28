package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.spi.AuditConfiguration;
import org.hibernate.envers.event.spi.EnversPreCollectionRemoveEventListenerImpl;

public class CustomEnversPreCollectionRemoveEventListener
    extends EnversPreCollectionRemoveEventListenerImpl {
    protected CustomEnversPreCollectionRemoveEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
