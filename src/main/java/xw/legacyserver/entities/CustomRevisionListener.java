package xw.legacyserver.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

import java.io.Serializable;

public class CustomRevisionListener implements EntityTrackingRevisionListener {

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
    public void entityChanged(
        Class entityClass,
        String entityName,
        Serializable entityId,
        RevisionType revisionType,
        Object revisionEntity
    ) {

        //        System.out.println("----Entity Changed----");
        //        System.out.println(entityName + ": " + entityId);
        //        print(revisionEntity);
        //        System.out.println("/----Entity Changed----\n");
    }

    @Override
    public void newRevision(Object revisionEntity) {
        System.out.println("----New Revision----");
        print(revisionEntity);
        System.out.println("/----New Revision----\n");
    }
}
