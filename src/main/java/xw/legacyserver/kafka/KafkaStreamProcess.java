package xw.legacyserver.kafka;

import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.RevisionType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import xw.legacyserver.entities.IEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds any data to be streamed to Kafa within the current Transaction
 */
public class KafkaStreamProcess implements AfterTransactionCompletionProcess {
    private final KafkaTemplate<KafkaKey, IEntity> kafkaTemplate;
    private final String kafkaTopic;

    private SessionImplementor session;

    // keyed off KafkaStreamPair of EntityName and ID
    private Map<KafkaStreamPair<String, String>, KafkaStreamPair<RevisionType
        , IEntity>> work;

    public KafkaStreamProcess(
        SessionImplementor session,
        KafkaTemplate<KafkaKey, IEntity> kafkaTemplate,
        String kafkaTopic
    ) {
        this.session = session;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopic = kafkaTopic;
        work = new HashMap<>();
    }

    /**
     * When adding work the RevisionType order matters:
     * 1. DEL
     * 2. ADD
     * 3. MOD
     *
     * @param key
     * @param entity
     */
    public void addWork(KafkaKey key, IEntity entity) {
        KafkaStreamPair<RevisionType, IEntity> existingWork =
            work.get(KafkaStreamPair.of(
                key.entityName,
                key.id
            ));

        if (existingWork != null) {
            if (existingWork.getLeft() == RevisionType.DEL) {
                // delete work takes precedence
                return;
            } else if (existingWork.getLeft() == RevisionType.ADD
                && key.revisionType == RevisionType.MOD
            ) {
                // we already have ADD, more important than MOD
                return;
            }
        }

        work.put(
            KafkaStreamPair.of(key.entityName, key.id),
            KafkaStreamPair.of(key.revisionType, entity)
        );

    }

    public void removeWork() {
        // not used yet
    }

    @Override
    public void doAfterTransactionCompletion(
        boolean success, SessionImplementor session
    ) {

        if (success) {
            for (Map.Entry<KafkaStreamPair<String, String>,
                KafkaStreamPair<RevisionType, IEntity>> entry : work
                .entrySet()) {

                sendMessage(
                    entry.getValue().getLeft(),
                    entry.getValue().getRight()
                );

            }
        } else {
            System.err.println("Transaction failed commit");
        }
    }

    private void sendMessage(RevisionType revisionType, IEntity o) {

        KafkaKey key = new KafkaKey(revisionType,
            o.getKey(), o.getClass().getCanonicalName()
        );

        ListenableFuture<SendResult<KafkaKey, IEntity>> future =
            kafkaTemplate.send(
                kafkaTopic,
                key,
                o
            );

        future.addCallback(new ListenableFutureCallback<SendResult<KafkaKey,
            IEntity>>() {

            @Override
            public void onSuccess(SendResult<KafkaKey, IEntity> result) {
                System.out.println("Sent message ID=[" + result.getProducerRecord()
                    .key() + "] with " +
                    "offset=[" + result.getRecordMetadata()
                    .offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message due to : " + ex.getMessage());
            }
        });
    }
}
