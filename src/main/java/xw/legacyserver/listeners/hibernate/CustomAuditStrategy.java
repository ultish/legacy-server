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
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import xw.legacyserver.entities.CustomRevisionEntity;
import xw.legacyserver.kafka.KafkaMetadata;
import xw.legacyserver.kafka.KafkaStreamManager;
import xw.legacyserver.kafka.KafkaStreamProcess;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
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
    private Reflections reflections;

    public CustomAuditStrategy() {
        sessionCacheCleaner = new SessionCacheCleaner();
        reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("xw.legacyserver.entities"))
            .setScanners(
                new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner(),
                new SubTypesScanner()
            )
            .useParallelExecutor(4));

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

        try {
            Class entityClass = Class.forName(entityName);

            Set<Class<?>> subTypesOf =
                reflections.getSubTypesOf(entityClass);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Set<Field> fieldsManyToMany = reflections.getFieldsAnnotatedWith(
            ManyToMany.class);

        Set<Field> fieldsOneToMany = reflections.getFieldsAnnotatedWith(
            OneToMany.class);

        Set<Field> fieldsManyToOne = reflections.getFieldsAnnotatedWith(
            ManyToOne.class);

        List<String> collect = fieldsManyToMany.stream().filter(field ->
            field.getDeclaringClass().getName().equals(entityName)
        ).map(field -> {
            String name = field.getName();
            ManyToMany annotation = field.getAnnotation(ManyToMany.class);
            String mappedBy = annotation.mappedBy();

            return name;
        }).collect(Collectors.toList());

        // TODO either here or earlier in listener chain, use the entityName
        //  to pull out the class and look for the annotations to see what it
        //  has. We don't define annotations here for the entity classes so
        //  we may not have what we want. But basically I want to generate a
        //  schema definition based on @Basic attributes vs @OneToOne,
        //  @OneToMany, or @ManyToMany. We only need to know about
        //  relationships and the entityName they are going to.
        // Lets see what we get from lombok...

        //        super.perform(session, entityName, auditCfg, id, data,
        //        revision);

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
            ((CustomRevisionEntity) revision).getRev()
        );
        actualData.remove("revtype");
        actualData.remove("originalId");

        kafkaStreamProcess.addWork(meta, actualData);

        // TODO?
        //        sessionCacheCleaner.scheduleAuditDataRemoval(session, data);

        //
        //        transaction.registerSynchronization(new Synchronization() {
        //            @Override
        //            public void beforeCompletion() {
        //                // nope
        //                System.out.println("nope!");
        //            }
        //
        //            @Override
        //            public void afterCompletion(int status) {
        //                // yea!
        //                if (status == Status.STATUS_COMMITTED) {
        //                    AuditConfiguration auditCfg2 = auditCfg;
        //
        //                    Map<String, Object> actualData = (Map<String,
        //                    Object>) data;
        //
        //                    Map<String, Object> result = new HashMap<>();
        //                    Map<String, Object> meta = new HashMap<>();
        //                    meta.put("type", "ENTITY");
        //                    meta.put("revType", actualData.get("revtype"));
        //                    meta.put("id", id);
        //                    meta.put("entity", entityName);
        //                    meta.put("rev", ((CustomRevisionEntity)
        //                    revision).getRev());
        //
        //                    actualData.remove("revtype");
        //                    actualData.remove("originalId");
        //
        //                    result.put("meta", meta);
        //                    result.put("data", actualData);
        //
        //                    ObjectMapper mapper = new ObjectMapper();
        //
        //                    try {
        //                        String send = mapper
        //                        .writerWithDefaultPrettyPrinter()
        //                            .writeValueAsString(result);
        //
        //                        System.out.println("\t>> Auditing: " +
        //                        entityName +
        //                            " [" + id + "]");
        //                        System.out.println("\t>> toSend: " + send);
        //                        System.out.println();
        //                    } catch (JsonProcessingException e) {
        //                        e.printStackTrace();
        //                    }
        //
        //                    // at this point we should generate an async
        //                    job to
        //                    // stream to kafka
        //                }
        //            }
        //        });

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
            actualEntityName, ((CustomRevisionEntity) revision).getRev()
        );

        kafkaStreamProcess.addWork(meta, data);

        // TODO?
        //        sessionCacheCleaner.scheduleAuditDataRemoval(
        //            session,
        //            persistentCollectionChangeData.getData()
        //        );

        //        transaction.registerSynchronization(new Synchronization() {
        //            @Override
        //            public void beforeCompletion() {
        //                // nope
        //                System.out.println("nope!");
        //            }
        //
        //            @Override
        //            public void afterCompletion(int status) {
        //                // yea!
        //                if (status == Status.STATUS_COMMITTED) {
        //                    AuditConfiguration auditCfg2 = auditCfg;
        //
        //                    String entityName =
        //                        persistentCollectionChangeData
        //                        .getEntityName();
        //
        //                    Map<String, Object> relData =
        //                        persistentCollectionChangeData.getData();
        //                    RevisionType revType = (RevisionType) relData.get(
        //                        "revtype");
        //
        //                    Map<String, Object> originalId =
        //                        (Map<String, Object>) relData.get(
        //                            "originalId");
        //
        //                    Map<String, Object> data = new HashMap<>();
        //                    originalId.entrySet().forEach(entry -> {
        //                        if (!entry.getKey().equals("rev")) {
        //                            data.put(entry.getKey(), entry.getValue
        //                            ());
        //                        }
        //                    });
        //
        //                    Map<String, Object> result = new HashMap<>();
        //                    Map<String, Object> meta = new HashMap<>();
        //                    meta.put("type", "REL");
        //                    meta.put("revType", revType);
        //                    meta.put("entity", entityName);
        //                    meta.put("rev", ((CustomRevisionEntity)
        //                    revision).getRev());
        //
        //                    result.put("meta", meta);
        //                    result.put("data", data);
        //
        //                    ObjectMapper mapper = new ObjectMapper();
        //                    try {
        //                        String send = mapper
        //                        .writerWithDefaultPrettyPrinter()
        //                            .writeValueAsString(result);
        //
        //                        System.out.println("\t>> Auditing
        //                        Collection [" + revType +
        //                            "]: " + entityName);
        //                        System.out.println("\t>> toSend: " + send);
        //                        System.out.println();
        //                    } catch (JsonProcessingException e) {
        //                        e.printStackTrace();
        //                    }
        //
        //                    //                    revEntity.get
        //                    // at this point we should generate an async
        //                    job to
        //                    // stream to kafka
        //                }
        //            }
        //        });
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
