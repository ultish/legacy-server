package xw.legacyserver.controllers;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.tools.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Set;

@RestController
public class AuditController {

    @PersistenceContext
    EntityManager em;

    @GetMapping("/audit/revision/{revision}")
    @ResponseBody
    public Set<Pair<String, Class>> revisionData(@PathVariable Integer revision) {
        Set<Pair<String, Class>> modifiedEntityTypes =
            AuditReaderFactory.get(em)
                .getCrossTypeRevisionChangesReader().findEntityTypes(revision);

        return modifiedEntityTypes;
    }
}
