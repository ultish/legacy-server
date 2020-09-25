package xw.legacyserver.listeners.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.envers.RevisionType;
import org.hibernate.event.spi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import xw.legacyserver.entities.IEntity;
import xw.legacyserver.kafka.KafkaMetadata;
import xw.legacyserver.kafka.KafkaStreamManager;
import xw.legacyserver.kafka.KafkaStreamProcess;
import xw.legacyserver.kafka.RelationshipMetadata;
import xw.legacyserver.tools.ClassMetadata;

import java.io.Serializable;

/**
 * if we aren't using Hibernate Envers, we can use Hibernate's Event
 * Listeners instead. Envers uses the same Listeners to perform it's updates,
 * with extra logic for detecting relationships, inverses, loading
 * collections, tracking entities once only etc. A lot of code dealing with
 * the entity/relationships in Envers that we can be inspired from
 */
@Component
public class CustomKafkaHibernateListener extends EmptyInterceptor implements
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
    private KafkaTemplate<KafkaMetadata, Object> kafkaTemplate;

    @Autowired
    private KafkaStreamManager kafkaStreamManager;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    private void streamToKafka(
        AbstractEvent event, String entityName, KafkaMetadata kafkaMetadata,
        Object data
    ) {
        print(kafkaMetadata.getRevType(), data);

        RelationshipMetadata relationshipMetadata =
            ClassMetadata.getSchemaMetadata(entityName);

        final Transaction transaction = event.getSession().getTransaction();
        KafkaStreamProcess kafkaStreamProcess = kafkaStreamManager.get(
            transaction);

        kafkaStreamProcess.addWork(kafkaMetadata, data);

    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        //        System.out.println("test" + event);
        //        print(RevisionType.MOD, event.getEntity());

        if (event.getEntity() instanceof IEntity) {
            IEntity entity = (IEntity) event.getEntity();

            String entityName = entity.getClass().getName();

            RelationshipMetadata relationshipMetadata =
                ClassMetadata.getSchemaMetadata(entityName);

            KafkaMetadata meta = new KafkaMetadata(
                "ENTITY",
                RevisionType.MOD,
                entity.getKey(),
                entityName,
                -1,
                relationshipMetadata
            );

            streamToKafka(event, entityName, meta, entity);
        }
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        print(RevisionType.DEL, event.getEntity());

        if (event.getEntity() instanceof IEntity) {
            IEntity entity = (IEntity) event.getEntity();

            String entityName = entity.getClass().getName();

            RelationshipMetadata relationshipMetadata =
                ClassMetadata.getSchemaMetadata(entityName);

            KafkaMetadata meta = new KafkaMetadata(
                "ENTITY",
                RevisionType.DEL,
                entity.getKey(),
                entityName,
                -1,
                relationshipMetadata
            );

            streamToKafka(event, entityName, meta, entity);
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        print(RevisionType.ADD, event.getEntity());

        if (event.getEntity() instanceof IEntity) {
            IEntity entity = (IEntity) event.getEntity();

            String entityName = entity.getClass().getName();

            RelationshipMetadata relationshipMetadata =
                ClassMetadata.getSchemaMetadata(entityName);

            KafkaMetadata meta = new KafkaMetadata(
                "ENTITY",
                RevisionType.ADD,
                entity.getKey(),
                entityName,
                -1,
                relationshipMetadata
            );

            streamToKafka(event, entityName, meta, entity);
        }
    }

    /*
    TODO Envers uses:
    - PostCollectionRecreate
    - PostDelete
    - PostInsert
    - PostUpdate
    - PreCollectionRemove
    - PreCollectionUpdate
     */

    private void updateCollection(AbstractCollectionEvent event) {
        CollectionEntry collectionEntry = event.getSession()
            .getPersistenceContext()
            .getCollectionEntry(event.getCollection());

        PersistentCollection persistentCollection = event.getCollection();

        if (!collectionEntry.getLoadedPersister().isInverse()) {
            Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();
            if (affectedOwnerOrNull != null && affectedOwnerOrNull instanceof IEntity) {

                Serializable affectedOwnerIdOrNull =
                    event.getAffectedOwnerIdOrNull();
                String entityName = event.getAffectedOwnerEntityName();

                RelationshipMetadata relationshipMetadata =
                    ClassMetadata.getSchemaMetadata(entityName);

                KafkaMetadata meta = new KafkaMetadata(
                    "REL",
                    RevisionType.MOD,
                    affectedOwnerIdOrNull.toString(),
                    entityName,
                    -1,
                    relationshipMetadata
                );

                streamToKafka(event, entityName, meta, affectedOwnerOrNull);
            }
        }

    }

    @Override
    public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
        System.out.println("\npost recreate collection");

        updateCollection(event);
    }

    @Override
    public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
        System.out.println("\npost remove collection");
        //
        //        String ownerEntityName = event.getAffectedOwnerEntityName();
        //        Serializable affectedOwnerIdOrNull =
        //            event.getAffectedOwnerIdOrNull();
        //        Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();
        //
        //        Object value = event.getCollection().getValue();
        //
        //        System.out.println("\tName: " + ownerEntityName);
        //        System.out.println("\tID: " + affectedOwnerIdOrNull);
        //        System.out.println("\tCollection: " + event.getCollection());
        //        print(RevisionType.DEL, affectedOwnerOrNull);
        //
        //        System.out.println("/post remove collection----------\n");
    }

    @Override
    public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
        System.out.println("\npost update collection");
        //
        //        String ownerEntityName = event.getAffectedOwnerEntityName();
        //        Serializable affectedOwnerIdOrNull =
        //            event.getAffectedOwnerIdOrNull();
        //        Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();
        //
        //        Object value = event.getCollection().getValue();
        //
        //        System.out.println("\tName: " + ownerEntityName);
        //        System.out.println("\tID: " + affectedOwnerIdOrNull);
        //        System.out.println("\tCollection: " + event.getCollection());
        //        print(RevisionType.MOD, affectedOwnerOrNull);
        //
        //        System.out.println("/post update collection----------\n");
    }

    @Override
    public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
        System.out.println("\npre remove collection");
        System.out.println(event.getCollection());

        updateCollection(event);
    }

    @Override
    public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
        System.out.println("\npre update collection");
        System.out.println(event.getCollection());

        updateCollection(event);
    }

    @Override
    public void onPreRecreateCollection(PreCollectionRecreateEvent event) {
        System.out.println("\npre recreate collection");
        //        System.out.println(event.getCollection());
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
