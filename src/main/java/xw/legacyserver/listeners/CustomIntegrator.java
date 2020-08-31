package xw.legacyserver.listeners;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.configuration.spi.AuditConfiguration;
import org.hibernate.envers.event.spi.EnversListenerDuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class CustomIntegrator implements Integrator {
    private AuditConfiguration enversConfiguration;

    @Override
    public void integrate(
        Configuration configuration,
        SessionFactoryImplementor sessionFactory,
        SessionFactoryServiceRegistry serviceRegistry
    ) {

        final EventListenerRegistry listenerRegistry =
            serviceRegistry.getService(
                EventListenerRegistry.class);

        listenerRegistry.addDuplicationStrategy(
            EnversListenerDuplicationStrategy.INSTANCE);

        enversConfiguration = AuditConfiguration.getFor(
            configuration,
            serviceRegistry.getService(
                ClassLoaderService.class
            )
        );

        if (enversConfiguration.getEntCfg().hasAuditedEntities()) {
            listenerRegistry.appendListeners(
                EventType.POST_DELETE, new
                    CustomEnversPostDeleteEventListener(
                    enversConfiguration
                )
            );
            listenerRegistry.appendListeners(
                EventType.POST_INSERT, new CustomEnversPostInsertEventListener(
                    enversConfiguration
                )
            );
            listenerRegistry.appendListeners(
                EventType.POST_UPDATE, new
                    CustomEnversPostUpdateEventListener(
                    enversConfiguration
                )
            );
            listenerRegistry.appendListeners(
                EventType.POST_COLLECTION_RECREATE,
                new CustomEnversPostCollectionRecreateEventListener(
                    enversConfiguration)
            );
            listenerRegistry.appendListeners(
                EventType.PRE_COLLECTION_REMOVE,
                new CustomEnversPreCollectionRemoveEventListener(
                    enversConfiguration)
            );
            listenerRegistry.appendListeners(
                EventType.PRE_COLLECTION_UPDATE,
                new CustomEnversPreCollectionUpdateEventListener(
                    enversConfiguration)
            );
        }
    }

    @Override
    public void integrate(
        MetadataImplementor metadata,
        SessionFactoryImplementor sessionFactory,
        SessionFactoryServiceRegistry serviceRegistry
    ) {

    }

    @Override
    public void disintegrate(
        SessionFactoryImplementor sessionFactory,
        SessionFactoryServiceRegistry serviceRegistry
    ) {
        if (enversConfiguration != null) {
            enversConfiguration.destroy();
        }
    }
}
