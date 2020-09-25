package xw.legacyserver.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RelationshipMetadata {
    List<Map<String, Object>> data;
}
