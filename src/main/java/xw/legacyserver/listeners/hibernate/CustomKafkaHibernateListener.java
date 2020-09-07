package xw.legacyserver.listeners.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.envers.RevisionType;
import org.hibernate.event.spi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import xw.legacyserver.entities.IEntity;
import xw.legacyserver.kafka.KafkaData;
import xw.legacyserver.kafka.KafkaKey;
import xw.legacyserver.kafka.KafkaStreamManager;

import java.io.Serializable;

/**
 * if we aren't using Hibernate Envers, we can use Hibernate's Event
 * Listeners instead. Envers uses the same Listeners to perform it's updates,
 * with extra logic for detecting relationships, inverses, loading
 * collections, tracking entities once only etc. A lot of code dealing with
 * the entity/relationships in Envers that we can be inspired from
 */
@Component
public class CustomKafkaHibernateListener implements
    PostUpdateEventListener,
    PostInsertEventListener,
    PostDeleteEventListener,
    PreCollectionRemoveEventListener,
    PreCollectionUpdateEventListener,
    PreCollectionRecreateEventListener,
    PostCollectionRemoveEventListener,
    PostCollectionUpdateEventListener,
    PostCollectionRecreateEventListener {

    @Autowired
    private KafkaTemplate<KafkaKey, KafkaData> kafkaTemplate;

    @Autowired
    private KafkaStreamManager kafkaStreamManager;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    private void streamToKafka(
        AbstractEvent event, RevisionType revisionType,
        Object o
    ) {
        if (o != null && o instanceof IEntity) {

            // we should wrap this message with metadata eg MOD, CREATE,
            // DELETE and any other
            //                sendMessage(revisionType, (IEntity) o);

            IEntity ie = (IEntity) o;
            kafkaStreamManager.get(event.getSession())
                .addWork(KafkaKey.of(
                    revisionType,
                    ie.getClass().getCanonicalName(),
                    ie.getKey()
                ), ie);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        System.out.println("test" + event);
        print(RevisionType.MOD, event.getEntity());
        streamToKafka(event, RevisionType.MOD, event.getEntity());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        print(RevisionType.DEL, event.getEntity());
        streamToKafka(event, RevisionType.DEL, event.getEntity());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        print(RevisionType.ADD, event.getEntity());

        streamToKafka(event, RevisionType.ADD, event.getEntity());
    }

    //    @Override
    //    public boolean requiresPostCommitHanding(EntityPersister persister) {
    //        return false;
    //    }

    @Override
    public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
        System.out.println("post recreate collection");
        String ownerEntityName = event.getAffectedOwnerEntityName();

        CollectionEntry collectionEntry = event.getSession()
            .getPersistenceContext()
            .getCollectionEntry(event.getCollection());

        if (!collectionEntry.getLoadedPersister().isInverse()) {
            // not recording inverse side of relationships

            //
            //        // always check if we need to do something with this
            //        owner entity
            //        if (ownerEntityName.equals("xw.legacyserver.entities
            //        .TrackedTask") || ownerEntityName
            //            .equals("xw.legacyserver.entities.ChargeCode")) {

            Serializable affectedOwnerIdOrNull =
                event.getAffectedOwnerIdOrNull();
            Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();

            Object value = event.getCollection().getValue();

            System.out.println("\tName: " + ownerEntityName);
            System.out.println("\tID: " + affectedOwnerIdOrNull);
            System.out.println("\tInverse: " +
                collectionEntry.getLoadedPersister().isInverse());
            print(RevisionType.MOD, affectedOwnerOrNull);

            streamToKafka(event, RevisionType.MOD, affectedOwnerOrNull);
        } else {
            System.out.println("skip " + ownerEntityName);
        }
        System.out.println("/post recreate collection----------\n");
    }

    @Override
    public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
        System.out.println("post remove collection");

        String ownerEntityName = event.getAffectedOwnerEntityName();
        Serializable affectedOwnerIdOrNull =
            event.getAffectedOwnerIdOrNull();
        Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();

        Object value = event.getCollection().getValue();

        //        System.out.println("\tName: " + ownerEntityName);
        //        System.out.println("\tID: " + affectedOwnerIdOrNull);
        //        print(affectedOwnerOrNull);
        //        System.out.println("/post remove collection----------\n");
    }

    @Override
    public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
        System.out.println("post update collection");

        String ownerEntityName = event.getAffectedOwnerEntityName();
        Serializable affectedOwnerIdOrNull =
            event.getAffectedOwnerIdOrNull();
        Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();

        Object value = event.getCollection().getValue();

        //        System.out.println("\tName: " + ownerEntityName);
        //        System.out.println("\tID: " + affectedOwnerIdOrNull);
        //        print(affectedOwnerOrNull);
        //        System.out.println("/post update collection----------\n");
    }

    @Override
    public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
        System.out.println("pre remove collection");
    }

    @Override
    public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
        System.out.println("pre update collection");
    }

    @Override
    public void onPreRecreateCollection(PreCollectionRecreateEvent event) {
        System.out.println("pre recreate collection");
    }

    private void print(
        RevisionType revisionType,
        Object o
    ) {
        if (o != null) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                String entityJson = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(o);

                System.out.println(entityJson);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
