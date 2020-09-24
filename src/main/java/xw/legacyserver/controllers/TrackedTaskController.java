package xw.legacyserver.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import xw.legacyserver.dao.ChargeCodeDAO;
import xw.legacyserver.dao.TimeBlockDAO;
import xw.legacyserver.dao.TrackedTaskDAO;
import xw.legacyserver.dao.UserDAO;
import xw.legacyserver.entities.*;
import xw.legacyserver.rest.TrackedTaskRest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RestController()
@RequestMapping("/trackedtasks")
public class TrackedTaskController {
    @Autowired
    TrackedTaskDAO trackedTaskDAO;
    @Autowired
    ChargeCodeDAO chargeCodeDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    TimeBlockDAO timeBlockDAO;

    @PersistenceContext
    EntityManager em;

    // Spring 4.3+    @GetMapping("/trackedtasks/audit/{id}/revisions")
    @RequestMapping(method = RequestMethod.GET,
        value = "/audit/{id}/revisions")
    @ResponseBody
    public List<Number> revisionsTrackedTask(
        @PathVariable Integer id
    ) {
        List<Number> revisionNumbers =
            AuditReaderFactory.get(em).getRevisions(
                TrackedTask.class,
                id
            );
        return revisionNumbers;
    }

    // @GetMapping("/trackedtasks/audit/{id}/{revision}")
    @RequestMapping(method = RequestMethod.GET,
        value = "/audit/{id}/{revision}")
    @ResponseBody
    public TrackedTask auditTrackedTask(
        @PathVariable Integer id,
        @PathVariable Integer revision
    ) {
        return AuditReaderFactory.get(em).find(TrackedTask.class, id,
            revision
        );
    }

    //    @GetMapping("/trackedtasks/audit/{id}/haschange/{property}")
    @RequestMapping(method = RequestMethod.GET,
        value = "/audit/{id}/haschange/{property}")
    public List<Tuple> auditChangesTrackedTask(
        @PathVariable Integer id,
        @PathVariable String property
    ) {
        AuditQuery query = AuditReaderFactory.get(em).createQuery()
            .forRevisionsOfEntity(TrackedTask.class, false, true)
            .add(AuditEntity.id().eq(id))
            .add(AuditEntity.property(property).hasChanged());

        List<Object[]> queryResult = query.getResultList();

        List<Tuple> result = queryResult.stream().map(o -> {
            TrackedTask tt = (TrackedTask) o[0];
            CustomRevisionEntity revision = (CustomRevisionEntity) o[1];
            RevisionType type = (RevisionType) o[2];
            return new Tuple(
                tt,
                revision.getRev(),
                new Date(revision.getRevtstmp()),
                type
            );
        }).collect(Collectors.toList());

        return result;
    }

    //    @GetMapping("/trackedtasks")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<TrackedTask> getAllTrackedTasks() {
        return trackedTaskDAO.findAll();
    }

    //    @PostMapping("/trackedtasks")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public TrackedTask create(@RequestBody TrackedTaskRest trackedTask) {
        List<ChargeCode> cc = new ArrayList<>();

        if (!trackedTask.getChargeCodes().isEmpty()) {
            cc = chargeCodeDAO.findAll()
                .stream()
                .filter(c -> trackedTask.getChargeCodes().contains(c.getId()))
                .collect(Collectors.toList());
        }
        List<TimeBlock> timeBlocks = new ArrayList<>();
        if (!trackedTask.getTimeBlocks().isEmpty()) {
            timeBlocks = timeBlockDAO.findAll()
                .stream()
                .filter(tb -> trackedTask.getTimeBlocks().contains(tb.getId()))
                .collect(Collectors.toList());
        }

        User user = null;
        if (trackedTask.getUser() != null) {
            user = userDAO.findAll()
                .stream()
                .filter(u -> trackedTask.getUser().equals(u.getId()))
                .findFirst()
                .orElse(null);
        }

        TrackedTask tt = new TrackedTask(null, trackedTask.getNotes(), cc,
            timeBlocks,
            new Date(), new Date(), trackedTask.getOvertimeEnabled(), user
        );
        return trackedTaskDAO.save(tt);
    }

    //    @PutMapping("/trackedtasks/{id}")
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    //    @ResponseBody
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

            List<Integer> existingCCIds = existing.getChargeCodes()
                .stream()
                .map(ChargeCode::getId)
                .collect(
                    Collectors.toList());

            List<Integer> toRemove = existingCCIds
                .stream()
                .filter(existingCC -> !trackedTask.getChargeCodes()
                    .contains(existingCC))
                .collect(Collectors.toList());

            List<Integer> newCCIds = trackedTask.getChargeCodes()
                .stream()
                .filter(ccId -> !existingCCIds.contains(ccId))
                .collect(Collectors.toList());

            List<ChargeCode> newCCs = chargeCodeDAO.findAll().stream()
                .filter(cc -> newCCIds.contains(cc.getId()))
                .collect(Collectors.toList());

            List<ChargeCode> toRemoveCCs = existing.getChargeCodes().stream()
                .filter(cc -> toRemove.contains(cc.getId()))
                .collect(Collectors.toList());

            existing.getChargeCodes().removeAll(toRemoveCCs);
            existing.getChargeCodes().addAll(newCCs);

            //            existing.setChargeCodes(
            //                chargeCodeDAO.findAll()
            //                    .stream()
            //                    .filter(cc -> trackedTask.getChargeCodes()
            //                        .contains(cc.getId()))
            //                    .collect(Collectors.toList()));
        }

        if (trackedTask.getTimeBlocks().isEmpty()) {
            existing.getTimeBlocks().forEach(tb -> tb.setTrackedTask(null));

            existing.setTimeBlocks(Collections.emptyList());
        } else {

            List<TimeBlock> timeBlocks = timeBlockDAO.findAll()
                .stream()
                .filter(tb -> trackedTask.getTimeBlocks()
                    .contains(tb.getId()))
                .collect(Collectors.toList());

            // update owning side of the relationship
            timeBlocks.forEach(tb -> tb.setTrackedTask(existing));

            existing.setTimeBlocks(timeBlocks);
        }
        if (trackedTask.getUser() != null) {
            existing.setUser(userDAO.findAll()
                .stream()
                .filter(u -> trackedTask.getUser().equals(u.getId()))
                .findFirst()
                .orElse(null));
        } else {
            existing.setUser(null);
        }

        existing.setNotes(trackedTask.getNotes());
        existing.setOvertimeEnabled(trackedTask.getOvertimeEnabled());
        return trackedTaskDAO.save(existing);
    }

    @Value
    @Data
    @AllArgsConstructor
    private class Tuple {
        TrackedTask trackedTask;
        Integer rev;
        Date revtstmp;
        RevisionType revType;
    }
}
