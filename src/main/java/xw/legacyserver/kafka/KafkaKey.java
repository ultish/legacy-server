package xw.legacyserver.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.envers.RevisionType;

@Data
@AllArgsConstructor
public class KafkaKey {
    RevisionType revisionType;
    String id;
    String entityName;

    public static KafkaKey of(
        RevisionType revisionType, String entityName, String id

    ) {
        return new KafkaKey(revisionType, id, entityName);
    }
}
