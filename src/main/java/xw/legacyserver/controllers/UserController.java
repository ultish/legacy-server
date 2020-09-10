package xw.legacyserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import xw.legacyserver.dao.TrackedTaskDAO;
import xw.legacyserver.dao.UserDAO;
import xw.legacyserver.entities.User;
import xw.legacyserver.rest.UserRest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Transactional
@RestController()
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserDAO userDAO;
    @Autowired
    TrackedTaskDAO trackedTaskDAO;

    @PersistenceContext
    EntityManager em;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<User> getTimeBlocks() {
        return userDAO.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public User create(@RequestBody UserRest rest) {

        User user = new User(null, rest.getUsername(), rest.getPassword(),
            Collections.emptyList()
        );

        return userDAO.save(user);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public User update(
        @RequestBody UserRest rest,
        @PathVariable Integer id
    ) {
        User existing = userDAO.findAll()
            .stream()
            .filter(u -> id.equals(u.getId()))
            .findFirst()
            .orElseThrow(() -> new RestClientException("Can't find User " + rest
                .getId()));

        existing.setUsername(rest.getUsername());
        existing.setPassword(rest.getPassword());

        return userDAO.save(existing);
    }
}
