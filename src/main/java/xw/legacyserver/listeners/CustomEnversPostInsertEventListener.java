package xw.legacyserver.listeners;

import org.hibernate.envers.configuration.spi.AuditConfiguration;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;

public class CustomEnversPostInsertEventListener
    extends EnversPostInsertEventListenerImpl {
    protected CustomEnversPostInsertEventListener(AuditConfiguration enversConfiguration) {
        super(enversConfiguration);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        super.onPostInsert(event);
        final String entityName = event.getPersister().getEntityName();

        if (getAuditConfiguration().getEntCfg().isVersioned(entityName)) {
            System.out.println("Versioned entity " + event.getEntity());
        }
    }
}
