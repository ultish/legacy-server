package xw.legacyserver.listeners.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
import org.hibernate.envers.entities.mapper.relation.MiddleComponentData;
import org.hibernate.envers.entities.mapper.relation.MiddleIdData;
import org.hibernate.envers.strategy.AuditStrategy;
import org.hibernate.envers.tools.query.Parameters;
import org.hibernate.envers.tools.query.QueryBuilder;
import xw.legacyserver.entities.CustomRevisionEntity;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * define our own audit strategy that will capture all the Enver values
 * and stream them to Kafka instead!! :)
 * <p>
 * This is run
 */
public class CustomAuditStrategy
    implements AuditStrategy /*extends DefaultAuditStrategy*/ {

    @Override
    public void perform(
        Session session,
        String entityName,
        AuditConfiguration auditCfg,
        Serializable id,
        Object data,
        Object revision
    ) {

        //        super.perform(session, entityName, auditCfg, id, data,
        //        revision);

        final Transaction transaction = session.getTransaction();

        transaction.registerSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
                // nope
                System.out.println("nope!");
            }

            @Override
            public void afterCompletion(int status) {
                // yea!
                if (status == Status.STATUS_COMMITTED) {
                    AuditConfiguration auditCfg2 = auditCfg;

                    Map<String, Object> actualData = (Map<String, Object>) data;

                    Map<String, Object> result = new HashMap<>();
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("type", "ENTITY");
                    meta.put("revType", actualData.get("revtype"));
                    meta.put("id", id);
                    meta.put("entity", entityName);
                    meta.put("rev", ((CustomRevisionEntity) revision).getRev());

                    actualData.remove("revtype");
                    actualData.remove("originalId");

                    result.put("meta", meta);
                    result.put("data", actualData);

                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        String send = mapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(result);

                        System.out.println("\t>> Auditing: " + entityName +
                            " [" + id + "]");
                        System.out.println("\t>> toSend: " + send);
                        System.out.println();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    // at this point we should generate an async job to
                    // stream to kafka
                }
            }
        });

    }

    @Override
    public void performCollectionChange(
        Session session,
        String entityName,
        String propertyName,
        AuditConfiguration auditCfg,
        PersistentCollectionChangeData persistentCollectionChangeData,
        Object revision
    ) {
        //        super.performCollectionChange(
        //            session,
        //            entityName,
        //            propertyName,
        //            auditCfg,
        //            persistentCollectionChangeData,
        //            revision
        //        );

        System.out.println("PEFORM Collection!");
        final Transaction transaction = session.getTransaction();

        transaction.registerSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
                // nope
                System.out.println("nope!");
            }

            @Override
            public void afterCompletion(int status) {
                // yea!
                if (status == Status.STATUS_COMMITTED) {
                    AuditConfiguration auditCfg2 = auditCfg;

                    String entityName =
                        persistentCollectionChangeData.getEntityName();

                    Map<String, Object> relData =
                        persistentCollectionChangeData.getData();
                    RevisionType revType = (RevisionType) relData.get(
                        "revtype");

                    Map<String, Object> originalId =
                        (Map<String, Object>) relData.get(
                            "originalId");

                    Map<String, Object> data = new HashMap<>();
                    originalId.entrySet().forEach(entry -> {
                        if (!entry.getKey().equals("rev")) {
                            data.put(entry.getKey(), entry.getValue());
                        }
                    });

                    Map<String, Object> result = new HashMap<>();
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("type", "REL");
                    meta.put("revType", revType);
                    meta.put("entity", entityName);
                    meta.put("rev", ((CustomRevisionEntity) revision).getRev());

                    result.put("meta", meta);
                    result.put("data", data);

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String send = mapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(result);

                        System.out.println("\t>> Auditing Collection [" + revType +
                            "]: " + entityName);
                        System.out.println("\t>> toSend: " + send);
                        System.out.println();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    //                    revEntity.get
                    // at this point we should generate an async job to
                    // stream to kafka
                }
            }
        });
    }

    @Override
    public void addEntityAtRevisionRestriction(
        GlobalConfiguration globalCfg,
        QueryBuilder rootQueryBuilder,
        Parameters parameters,
        String revisionProperty,
        String revisionEndProperty,
        boolean addAlias,
        MiddleIdData idData,
        String revisionPropertyPath,
        String originalIdPropertyName,
        String alias1,
        String alias2,
        boolean inclusive
    ) {

        //        super.addEntityAtRevisionRestriction(
        //            globalCfg,
        //            rootQueryBuilder,
        //            parameters,
        //            revisionProperty,
        //            revisionEndProperty,
        //            addAlias,
        //            idData,
        //            revisionPropertyPath,
        //            originalIdPropertyName,
        //            alias1,
        //            alias2,
        //            inclusive
        //        );

        System.out.println("add sotmghing!");
    }

    @Override
    public void addAssociationAtRevisionRestriction(
        QueryBuilder rootQueryBuilder,
        Parameters parameters,
        String revisionProperty,
        String revisionEndProperty,
        boolean addAlias,
        MiddleIdData referencingIdData,
        String versionsMiddleEntityName,
        String eeOriginalIdPropertyPath,
        String revisionPropertyPath,
        String originalIdPropertyName,
        String alias1,
        boolean inclusive,
        MiddleComponentData... componentDatas
    ) {
        //        super.addAssociationAtRevisionRestriction(
        //            rootQueryBuilder,
        //            parameters,
        //            revisionProperty,
        //            revisionEndProperty,
        //            addAlias,
        //            referencingIdData,
        //            versionsMiddleEntityName,
        //            eeOriginalIdPropertyPath,
        //            revisionPropertyPath,
        //            originalIdPropertyName,
        //            alias1,
        //            inclusive,
        //            componentDatas
        //        );

        System.out.println("add some other thing!");
    }
}
