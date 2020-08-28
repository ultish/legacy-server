package xw.legacyserver.controllers;

import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import xw.legacyserver.dao.ChargeCodeDAO;
import xw.legacyserver.dao.TrackedTaskDAO;
import xw.legacyserver.entities.ChargeCode;
import xw.legacyserver.entities.TrackedTask;
import xw.legacyserver.rest.TrackedTaskRest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
public class TrackedTaskController {
    @Autowired
    TrackedTaskDAO trackedTaskDAO;
    @Autowired
    ChargeCodeDAO chargeCodeDAO;
    @PersistenceContext
    EntityManager em;

    @GetMapping("/trackedtasks/audit/{id}/revisions")
    @ResponseBody
    public List<Number> revisions(
        @PathVariable Integer id
    ) {
        List<Number> revisionNumbers =
            AuditReaderFactory.get(em).getRevisions(TrackedTask.class, id);
        return revisionNumbers;
    }

    @GetMapping("/trackedtasks/audit/{id}/{revision}")
    @ResponseBody
    public TrackedTask audit(
        @PathVariable Integer id,
        @PathVariable Integer revision
    ) {
        return AuditReaderFactory.get(em).find(TrackedTask.class, id, revision);
    }

    @GetMapping("/trackedtasks")
    @ResponseBody
    public List<TrackedTask> getAll() {
        return trackedTaskDAO.findAll();
    }

    @PostMapping("/trackedtasks")
    @ResponseBody
    public TrackedTask create(@RequestBody TrackedTaskRest trackedTask) {
        List<ChargeCode> cc = new ArrayList<>();

        if (!trackedTask.getChargeCodes().isEmpty()) {
            cc = chargeCodeDAO.findAll()
                .stream()
                .filter(c -> trackedTask.getChargeCodes().contains(c.getId()))
                .collect(Collectors.toList());
        }
        TrackedTask tt = new TrackedTask(null, trackedTask.getNotes(), cc,
            new Date(), new Date(), trackedTask.getOvertimeEnabled()
        );
        return trackedTaskDAO.save(tt);
    }

    @PutMapping("/trackedtasks/{id}")
    @ResponseBody
    public TrackedTask update(
        @RequestBody TrackedTaskRest trackedTask,
        @PathVariable Integer id
    ) {
        TrackedTask existing = trackedTaskDAO.find(id);
        if (existing == null) {
            throw new RestClientException("Could not find Tracked Task with " +
                "ID: " + id);
        }

        if (trackedTask.getChargeCodes().isEmpty()) {
            existing.setChargeCodes(Collections.emptyList());
        } else {
            existing.setChargeCodes(
                chargeCodeDAO.findAll()
                    .stream()
                    .filter(cc -> trackedTask.getChargeCodes()
                        .contains(cc.getId()))
                    .collect(Collectors.toList()));
        }

        existing.setNotes(trackedTask.getNotes());
        existing.setOvertimeEnabled(trackedTask.getOvertimeEnabled());
        return trackedTaskDAO.save(existing);
    }
}
