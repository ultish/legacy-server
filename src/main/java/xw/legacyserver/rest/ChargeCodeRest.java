package xw.legacyserver.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeCodeRest {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private boolean expired;
}
