package xw.legacyserver.listeners.hibernate;

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

import javax.transaction.Status;
import javax.transaction.Synchronization;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * define our own audit strategy that will capture all the Enver values
 * and stream them to Kafka instead!! :)
 * <p>
 * This is run
 */
public class CustomAuditStrategy implements AuditStrategy {

    @Override
    public void perform(
        Session session,
        String entityName,
        AuditConfiguration auditCfg,
        Serializable id,
        Object data,
        Object revision
    ) {
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

                    System.out.println("PEFORM after TX commit " + entityName);
                    System.out.println("id " + id);
                    System.out.println("data " + data);
                    System.out.println("revision " + revision);
                    System.out.println();

                    // at this point we should generate an async job to
                    // stream to kafka
                }
            }
        });
        System.out.println("PEFORM!");
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
                    System.out.println("PEFORM after TX commit " + entityName);
                    System.out.println("propertyName " + propertyName);
                    System.out.println("persistentCollectionChangeData " + persistentCollectionChangeData);
                    System.out.println("revision " + revision);
                    System.out.println();

                    String relTable =
                        persistentCollectionChangeData.getEntityName();
                    Map<String, Object> relData =
                        persistentCollectionChangeData.getData();
                    RevisionType revType = (RevisionType) relData.get(
                        "revtype");

                    Set<String> strings = relData.keySet();

                    Map<String, Object> originalId =
                        (Map<String, Object>) relData.get(
                            "originalId");
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

        System.out.println("add some other thing!");
    }
}
