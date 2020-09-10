package xw.legacyserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import xw.legacyserver.dao.TimeBlockDAO;
import xw.legacyserver.dao.TrackedTaskDAO;
import xw.legacyserver.dao.UserDAO;
import xw.legacyserver.entities.TimeBlock;
import xw.legacyserver.entities.TrackedTask;
import xw.legacyserver.entities.User;
import xw.legacyserver.rest.TimeBlockRest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Transactional
@RestController()
@RequestMapping("/timeblocks")
public class TimeBlockController {
    @Autowired
    TimeBlockDAO timeBlockDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    TrackedTaskDAO trackedTaskDAO;

    @PersistenceContext
    EntityManager em;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<TimeBlock> getTimeBlocks() {
        return timeBlockDAO.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public TimeBlock create(@RequestBody TimeBlockRest rest) {
        User user = userDAO.findAll()
            .stream()
            .filter(u -> rest.getUser().equals(u.getId()))
            .findFirst()
            .orElse(null);
        TrackedTask trackedTask = trackedTaskDAO.find(rest.getTrackedTask());

        TimeBlock tb = new TimeBlock(null, new Date(rest.getStartTime()),
            user, trackedTask
        );

        return timeBlockDAO.save(tb);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public TimeBlock update(
        @RequestBody TimeBlockRest rest,
        @PathVariable Integer id
    ) {
        User user = userDAO.findAll()
            .stream()
            .filter(u -> rest.getUser().equals(u.getId()))
            .findFirst()
            .orElse(null);
        TrackedTask trackedTask = trackedTaskDAO.find(rest.getTrackedTask());

        TimeBlock existing = timeBlockDAO.findAll()
            .stream()
            .filter(tb -> id.equals(tb.getId()))
            .findFirst()
            .orElseThrow(() -> new RestClientException("Can't find TimeBlock "
                + rest
                .getId()));

        existing.setStartTime(new Date(rest.getStartTime()));
        existing.setUser(user);
        existing.setTrackedTask(trackedTask);

        return timeBlockDAO.save(existing);
    }

}
