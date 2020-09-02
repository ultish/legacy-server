package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.event.EnversPreCollectionRemoveEventListenerImpl;

public class CustomEnversPreCollectionRemoveEventListener
    extends EnversPreCollectionRemoveEventListenerImpl {
    protected CustomEnversPreCollectionRemoveEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
