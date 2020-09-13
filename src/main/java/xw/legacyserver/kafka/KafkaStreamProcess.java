package xw.legacyserver.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Holds any data to be streamed to Kafa within the current Transaction
 */
public class KafkaStreamProcess implements AfterTransactionCompletionProcess {
    private final KafkaTemplate<KafkaMetadata, Object> kafkaTemplate;
    private final String kafkaTopic;

    // a sorted list of work. So Entities first, then Relationships. Do
    // creates, updates, then deletes.
    private SortedSet<KafkaData> sortedWork;

    public KafkaStreamProcess(
        KafkaTemplate<KafkaMetadata, Object> kafkaTemplate,
        String kafkaTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopic = kafkaTopic;
        this.sortedWork = new TreeSet<>();
    }

    public void addWork(KafkaMetadata key, Object object) {
        sortedWork.add(new KafkaData(key, object));
    }

    public void removeWork() {
        // not used yet
    }

    @Override
    public void doAfterTransactionCompletion(
        boolean success, SessionImplementor session
    ) {

        if (success) {
            for (KafkaData kafkaData : sortedWork) {
                try {
                    sendMessage(kafkaData);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("Transaction failed commit");
        }
    }

    private void sendMessage(KafkaData data) throws
        JsonProcessingException {

        ListenableFuture<SendResult<KafkaMetadata, Object>> future =
            kafkaTemplate.send(
                kafkaTopic, data
            );

        future.addCallback(new ListenableFutureCallback<SendResult<KafkaMetadata,
            Object>>() {

            @Override
            public void onSuccess(SendResult<KafkaMetadata, Object> result) {
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
