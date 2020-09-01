package xw.legacyserver.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaStreamPair<L, R> {
    private L left;
    private R right;

    public static <L, R> KafkaStreamPair of(L left, R right) {
        return new KafkaStreamPair(left, right);
    }
}
