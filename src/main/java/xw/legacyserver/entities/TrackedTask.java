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
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"chargeCodes", "user",
    "timeBlocks"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trackedtasks")
@Audited
public class TrackedTask extends IEntity {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(name = "sequence-generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name",
                value = "trackedtasks_id_seq"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        })
    private Integer id;
    private String notes;

    @ToString.Exclude
    //    @JsonIgnoreProperties({"trackedTasks"})
    @ManyToMany
    @JoinTable(name = "taskcodes", joinColumns = @JoinColumn(name =
        "\"trackedtaskId\""),
        inverseJoinColumns = @JoinColumn(name = "\"chargecodeId\"")
    )
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<ChargeCode> chargeCodes;

    @ToString.Exclude
    //    @JsonIgnoreProperties({"trackedTask", "user"})
    @OneToMany(mappedBy = "trackedTask")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<TimeBlock> timeBlocks;

    private Date createdAt;
    private Date updatedAt;
    private Boolean overtimeEnabled;

    @ToString.Exclude
    //    @JsonIgnoreProperties({"trackedTasks"})
    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @Override
    public String getKey() {
        return id.toString();
    }
}



