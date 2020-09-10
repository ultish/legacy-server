package xw.legacyserver.dao;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import xw.legacyserver.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
@NoArgsConstructor
public class UserDAO {
    @PersistenceContext
    private EntityManager em;

    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class)
            .getResultList();
    }

    public User save(User tb) {
        em.persist(tb);
        return tb;
    }
}
