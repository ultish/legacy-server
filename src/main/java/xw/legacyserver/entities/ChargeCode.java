package xw.legacyserver.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true, exclude = "trackedTasks")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chargecodes")
@Audited
public class ChargeCode extends IEntity {
    @Id
    @GeneratedValue(generator = "cc-sequence-generator")
    @GenericGenerator(name = "cc-sequence-generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name",
                value = "chargecodes_id_seq"),
            @org.hibernate.annotations.Parameter(name = "initial_value",
                value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size",
                value = "1")
        })
    private Integer id;
    private String name;
    private String code;
    private String description;
    private boolean expired;
    private Date createdAt;
    private Date updatedAt;

    @ToString.Exclude
    //    @JsonIgnoreProperties({"chargeCodes"})
    @ManyToMany(mappedBy = "chargeCodes")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<TrackedTask> trackedTasks;

    @Override
    public String getKey() {
        return id.toString();
    }
}
