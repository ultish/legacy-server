package xw.legacyserver.listeners.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
import org.hibernate.envers.entities.mapper.relation.MiddleComponentData;
import org.hibernate.envers.entities.mapper.relation.MiddleIdData;
import org.hibernate.envers.strategy.DefaultAuditStrategy;
import org.hibernate.envers.synchronization.SessionCacheCleaner;
import org.hibernate.envers.tools.query.Parameters;
import org.hibernate.envers.tools.query.QueryBuilder;
import xw.legacyserver.entities.CustomRevisionEntity;
import xw.legacyserver.kafka.KafkaMetadata;
import xw.legacyserver.kafka.KafkaStreamManager;
import xw.legacyserver.kafka.KafkaStreamProcess;
import xw.legacyserver.kafka.RelationshipMetadata;
import xw.legacyserver.tools.ClassMetadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * define our own audit strategy that will capture all the Enver values
 * and stream them to Kafka instead!! :)
 * <p>
 * This is run
 */
public class CustomAuditStrategy
    extends DefaultAuditStrategy {

    private final SessionCacheCleaner sessionCacheCleaner;
    private KafkaStreamManager kafkaStreamManager;

    public CustomAuditStrategy() {
        sessionCacheCleaner = new SessionCacheCleaner();
    }

    private KafkaStreamManager getKafkaStreamManager() {
        if (this.kafkaStreamManager == null) {
            kafkaStreamManager =
                SpringContext.getBean(KafkaStreamManager.class);
        }
        return kafkaStreamManager;
    }

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

        RelationshipMetadata relationshipMetadata =
            ClassMetadata.getSchemaMetadata(entityName);

        KafkaStreamManager kafkaStreamManager = getKafkaStreamManager();
        final Transaction transaction = session.getTransaction();
        KafkaStreamProcess kafkaStreamProcess = kafkaStreamManager.get(
            transaction);

        Map<String, Object> actualData = (Map<String, Object>) data;
        KafkaMetadata meta = new KafkaMetadata(
            "ENTITY",
            (RevisionType) actualData.get("revtype"),
            id.toString(),
            entityName,
            ((CustomRevisionEntity) revision).getRev(),
            relationshipMetadata
        );
        actualData.remove("revtype");
        actualData.remove("originalId");

        // TODO disabled
        //        kafkaStreamProcess.addWork(meta, actualData);
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

        //        System.out.println("PEFORM Collection!");

        RelationshipMetadata relationshipMetadata =
            ClassMetadata.getSchemaMetadata(entityName);

        KafkaStreamManager kafkaStreamManager = getKafkaStreamManager();
        final Transaction transaction = session.getTransaction();
        KafkaStreamProcess kafkaStreamProcess = kafkaStreamManager.get(
            transaction);

        String actualEntityName =
            persistentCollectionChangeData.getEntityName();
        Map<String, Object> relData =
            persistentCollectionChangeData.getData();
        RevisionType revType = (RevisionType) relData.get(
            "revtype");
        Map<String, Object> originalId =
            (Map<String, Object>) relData.get(
                "originalId");

        List<java.lang.String> keys = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        originalId.entrySet().forEach(entry -> {
            if (!entry.getKey().equals("rev")) {
                data.put(entry.getKey(), entry.getValue());
                keys.add(entry.getKey() + ":" + entry.getValue().toString());
            }
        });

        KafkaMetadata meta = new KafkaMetadata("REL", revType,
            keys.stream().collect(Collectors.joining(",")),
            actualEntityName, ((CustomRevisionEntity) revision).getRev(),
            relationshipMetadata

        );

        // TODO disabled
        //        kafkaStreamProcess.addWork(meta, data);

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

        super.addEntityAtRevisionRestriction(
            globalCfg,
            rootQueryBuilder,
            parameters,
            revisionProperty,
            revisionEndProperty,
            addAlias,
            idData,
            revisionPropertyPath,
            originalIdPropertyName,
            alias1,
            alias2,
            inclusive
        );

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
        super.addAssociationAtRevisionRestriction(
            rootQueryBuilder,
            parameters,
            revisionProperty,
            revisionEndProperty,
            addAlias,
            referencingIdData,
            versionsMiddleEntityName,
            eeOriginalIdPropertyPath,
            revisionPropertyPath,
            originalIdPropertyName,
            alias1,
            inclusive,
            componentDatas
        );

        System.out.println("add some other thing!");
    }

}
