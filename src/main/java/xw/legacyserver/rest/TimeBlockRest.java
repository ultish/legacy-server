package xw.legacyserver.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeBlockRest {
    private Integer id;
    private Long startTime;
    private Integer user;
    private Integer trackedTask;
}
