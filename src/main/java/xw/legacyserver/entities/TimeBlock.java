package xw.legacyserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"trackedTask", "user"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "timeblocks")
@Audited
public class TimeBlock extends IEntity {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(name = "sequence-generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name",
                value = "timeblocks_id_seq"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        })
    private Integer id;

    private Date startTime;

    @ToString.Exclude
    @JsonIgnoreProperties({"trackedTask"})
    @ManyToOne
    private User user;

    @ToString.Exclude
    @JsonIgnoreProperties({"timeBlocks", "user"})
    @ManyToOne
    private TrackedTask trackedTask;

    @Override
    public String getKey() {
        return id.toString();
    }
}
