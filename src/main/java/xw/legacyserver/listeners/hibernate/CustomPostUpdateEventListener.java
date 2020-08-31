package xw.legacyserver.listeners.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * if we aren't using Hibernate Envers, we can use Hibernate's Event
 * Listeners instead. Envers uses the same Listeners to perform it's updates,
 * with extra logic for detecting relationships, inverses, loading
 * collections, tracking entities once only etc. A lot of code dealing with
 * the entity/relationships in Envers that we can be inspired from
 */
@Component
public class CustomPostUpdateEventListener implements
    PostUpdateEventListener,
    PreCollectionRemoveEventListener,
    PreCollectionUpdateEventListener,
    PreCollectionRecreateEventListener,
    PostCollectionRemoveEventListener,
    PostCollectionUpdateEventListener,
    PostCollectionRecreateEventListener {

    private void print(Object o) {
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

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        System.out.println("test" + event);
        print(event.getEntity());
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    @Override
    public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
        System.out.println("post recreate collection");
        String ownerEntityName = event.getAffectedOwnerEntityName();

        // always check if we need to do something with this owner entity
        if (ownerEntityName.equals("xw.legacyserver.entities.TrackedTask") || ownerEntityName
            .equals("xw.legacyserver.entities.ChargeCode")) {

            Serializable affectedOwnerIdOrNull =
                event.getAffectedOwnerIdOrNull();
            Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();

            Object value = event.getCollection().getValue();

            CollectionEntry collectionEntry = event.getSession()
                .getPersistenceContext()
                .getCollectionEntry(event.getCollection());

            System.out.println("\tName: " + ownerEntityName);
            System.out.println("\tID: " + affectedOwnerIdOrNull);
            System.out.println("\tInverse: " +
                collectionEntry.getLoadedPersister().isInverse());
            print(affectedOwnerOrNull);
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

        System.out.println("\tName: " + ownerEntityName);
        System.out.println("\tID: " + affectedOwnerIdOrNull);
        print(affectedOwnerOrNull);
        System.out.println("/post remove collection----------\n");
    }

    @Override
    public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
        System.out.println("post update collection");

        String ownerEntityName = event.getAffectedOwnerEntityName();
        Serializable affectedOwnerIdOrNull =
            event.getAffectedOwnerIdOrNull();
        Object affectedOwnerOrNull = event.getAffectedOwnerOrNull();

        Object value = event.getCollection().getValue();

        System.out.println("\tName: " + ownerEntityName);
        System.out.println("\tID: " + affectedOwnerIdOrNull);
        print(affectedOwnerOrNull);
        System.out.println("/post update collection----------\n");
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
}
