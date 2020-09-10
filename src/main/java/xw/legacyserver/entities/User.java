package xw.legacyserver.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"trackedTasks"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Audited
public class User extends IEntity {
    @Id
    @GeneratedValue(generator = "cc-sequence-generator")
    @GenericGenerator(name = "cc-sequence-generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name",
                value = "users_id_seq"),
            @org.hibernate.annotations.Parameter(name = "initial_value",
                value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size",
                value = "1")
        })
    private Integer id;
    private String username;
    private String password;

    @ToString.Exclude
    @JsonIgnoreProperties({"user"})
    @OneToMany(mappedBy = "user")
    private List<TrackedTask> trackedTasks;

    @Override
    public String getKey() {
        return id.toString();
    }
}
