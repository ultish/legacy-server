package xw.legacyserver.controllers;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.tools.Pair;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Set;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @PersistenceContext
    EntityManager em;

    // Spring 4.3+   @GetMapping("/audit/revision/{revision}")
    @RequestMapping(method = RequestMethod.GET,
        value = "/revision/{revision}")
    @ResponseBody
    public Set<Pair<String, Class>> revisionData(@PathVariable Integer revision) {
        Set<Pair<String, Class>> modifiedEntityTypes =
            AuditReaderFactory.get(em)
                .getCrossTypeRevisionChangesReader().findEntityTypes(revision);

        return modifiedEntityTypes;
    }
}
