package xw.legacyserver.entities;

import lombok.Data;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity(name = "revinfo")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity
    implements Serializable {

    @Id
    @GeneratedValue
    @RevisionNumber
    private int rev;

    @RevisionTimestamp
    private long revtstmp;

    @ElementCollection
    @JoinTable(name = "revchanges", joinColumns = @JoinColumn(name = "rev"))
    @Column(name = "entityname")
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames;

}
