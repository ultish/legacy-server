package xw.legacyserver.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "data")
public class KafkaData implements Comparable<KafkaData> {

    public KafkaMetadata meta;
    public Object data;

    @Override
    public int compareTo(KafkaData o) {
        if (this.meta.type.equals("ENTITY") && !o.meta.type.equals("ENTITY")) {
            return -1;
        } else if (this.meta.type.equals("REL") && !o.meta.type.equals("REL")) {
            return 1;
        } else {
            int revType = this.meta.revType.getRepresentation()
                .intValue() - o.meta.revType.getRepresentation().intValue();
            if (revType == 0) {
                int entName =
                    this.meta.entityName.compareTo(o.meta.entityName);
                if (entName == 0) {
                    return this.meta.id.compareTo(o.meta.id);
                } else {
                    return entName;
                }
            } else {
                return revType;
            }
        }
    }
}
