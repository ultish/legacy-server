package xw.legacyserver.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne
    private User user;

    @ToString.Exclude
    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private TrackedTask trackedTask;

    @Override
    public String getKey() {
        return id.toString();
    }
}
