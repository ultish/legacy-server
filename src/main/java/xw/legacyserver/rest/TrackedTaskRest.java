package xw.legacyserver.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xw.legacyserver.entities.ChargeCode;
import xw.legacyserver.entities.TimeBlock;
import xw.legacyserver.entities.TrackedTask;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackedTaskRest {
    private Integer id;
    private String notes;
    private List<Integer> chargeCodes;
    private Boolean overtimeEnabled;
    private List<Integer> timeBlocks;
    private Integer user;

    public TrackedTaskRest(TrackedTask tt) {
        this.id = tt.getId();
        this.notes = tt.getNotes();
        this.overtimeEnabled = tt.getOvertimeEnabled();
        this.chargeCodes =
            Optional.ofNullable(tt.getChargeCodes())
                .orElse(Collections.emptyList())
                .stream()
                .map(
                    ChargeCode::getId)
                .collect(Collectors.toList());
        this.user = tt.getUser().getId();
        this.timeBlocks =
            Optional.ofNullable(tt.getTimeBlocks())
                .orElse(Collections.emptyList())
                .stream()
                .map(TimeBlock::getId)
                .collect(Collectors.toList());
    }
}
