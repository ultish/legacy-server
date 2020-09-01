package xw.legacyserver.listeners.hibernate;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Component
public class CustomListenerConfigurer {
    @PersistenceUnit
    private EntityManagerFactory emf;

    private CustomKafkaHibernateListener postUpdateEventListener;

    @Autowired
    public CustomListenerConfigurer(CustomKafkaHibernateListener postUpdateEventListener) {
        this.postUpdateEventListener = postUpdateEventListener;
    }

    @PostConstruct
    protected void init() {
        SessionFactoryImpl sessionFactory =
            emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry()
            .getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_UPDATE)
            .appendListener(postUpdateEventListener);
        registry.getEventListenerGroup(EventType.PRE_COLLECTION_RECREATE)
            .appendListener(postUpdateEventListener);
        registry.getEventListenerGroup(EventType.PRE_COLLECTION_REMOVE)
            .appendListener(postUpdateEventListener);
        registry.getEventListenerGroup(EventType.PRE_COLLECTION_UPDATE)
            .appendListener(postUpdateEventListener);
        registry.getEventListenerGroup(EventType.POST_COLLECTION_RECREATE)
            .appendListener(postUpdateEventListener);
        registry.getEventListenerGroup(EventType.POST_COLLECTION_REMOVE)
            .appendListener(postUpdateEventListener);
        registry.getEventListenerGroup(EventType.POST_COLLECTION_UPDATE)
            .appendListener(postUpdateEventListener);

    }

}
