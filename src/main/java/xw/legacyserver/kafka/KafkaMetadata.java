package xw.legacyserver.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.RevisionType;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class KafkaMetadata {
    String type;
    RevisionType revType;
    String id;
    String entityName;
    int rev;

}
