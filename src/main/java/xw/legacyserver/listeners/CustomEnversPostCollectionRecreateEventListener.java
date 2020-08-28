package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.spi.AuditConfiguration;
import org.hibernate.envers.event.spi.EnversPostCollectionRecreateEventListenerImpl;

public class CustomEnversPostCollectionRecreateEventListener
    extends EnversPostCollectionRecreateEventListenerImpl {
    protected CustomEnversPostCollectionRecreateEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }
}
