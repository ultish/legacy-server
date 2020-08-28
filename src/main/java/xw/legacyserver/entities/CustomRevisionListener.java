package xw.legacyserver.entities;

import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

import java.io.Serializable;

public class CustomRevisionListener implements EntityTrackingRevisionListener {

    @Override
    public void entityChanged(
        Class entityClass,
        String entityName,
        Serializable entityId,
        RevisionType revisionType,
        Object revisionEntity
    ) {

        System.out.println(entityName);
    }

    @Override
    public void newRevision(Object revisionEntity) {
        System.out.println(revisionEntity);
    }
}
