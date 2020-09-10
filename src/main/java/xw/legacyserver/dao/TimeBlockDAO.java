package xw.legacyserver.dao;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import xw.legacyserver.entities.TimeBlock;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
@NoArgsConstructor
public class TimeBlockDAO {

    @PersistenceContext
    private EntityManager em;

    public List<TimeBlock> findAll() {
        return em.createQuery("select tb from TimeBlock tb", TimeBlock.class)
            .getResultList();
    }

    public TimeBlock save(TimeBlock tb) {
        em.persist(tb);
        return tb;
    }
}
