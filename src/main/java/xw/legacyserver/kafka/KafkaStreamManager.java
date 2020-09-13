package xw.legacyserver.kafka;

import org.hibernate.Transaction;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.event.spi.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaStreamManager {

    private final Map<Transaction, KafkaStreamProcess> kafkaStreamMap;
    @Autowired
    private KafkaTemplate<KafkaMetadata, Object> kafkaTemplate;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    public KafkaStreamManager() {
        kafkaStreamMap = new ConcurrentHashMap<>();
    }

    public KafkaStreamProcess get(Transaction transaction) {
        KafkaStreamProcess kafkaStreamProcess = kafkaStreamMap.get(transaction);
        if (kafkaStreamProcess == null) {
            kafkaStreamProcess = new KafkaStreamProcess(
                //                session,
                kafkaTemplate,
                kafkaTopic
            );
            kafkaStreamMap.put(transaction, kafkaStreamProcess);
            transaction.registerSynchronization(new Synchronization() {

                @Override
                public void afterCompletion(int status) {
                    final KafkaStreamProcess stream =
                        kafkaStreamMap.get(transaction);
                    if (stream != null) {
                        try {
                            stream.doAfterTransactionCompletion(
                                status == Status.STATUS_COMMITTED,
                                null
                            );
                        } finally {
                            kafkaStreamMap.remove(transaction);
                        }

                    }
                }

                @Override
                public void beforeCompletion() {

                }
            });

        }

        return kafkaStreamProcess;
    }

    public KafkaStreamProcess get(EventSource session) {
        final Transaction transaction = session.getTransaction();

        // one per transaction
        KafkaStreamProcess kafkaStreamProcess = kafkaStreamMap.get(transaction);
        if (kafkaStreamProcess == null) {
            kafkaStreamProcess = new KafkaStreamProcess(
                //                session,
                kafkaTemplate,
                kafkaTopic
            );
            kafkaStreamMap.put(transaction, kafkaStreamProcess);

            session.getActionQueue()
                .registerProcess((AfterTransactionCompletionProcess) (
                        success
                        , sessionImplementor
                    ) -> {
                        final KafkaStreamProcess stream =
                            kafkaStreamMap.get(transaction);
                        if (stream != null) {
                            try {
                                stream.doAfterTransactionCompletion(
                                    success,
                                    sessionImplementor
                                );
                            } finally {
                                kafkaStreamMap.remove(transaction);
                            }

                        }
                    }
                );
        }

        return kafkaStreamProcess;
    }

}
