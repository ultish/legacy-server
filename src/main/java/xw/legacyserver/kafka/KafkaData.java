package xw.legacyserver.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import xw.legacyserver.entities.IEntity;

@Data
@AllArgsConstructor
public class KafkaData {

    public KafkaKey key;
    public IEntity value;

}
