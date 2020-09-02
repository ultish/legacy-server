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
import xw.legacyserver.entities.ChargeCode;
import xw.legacyserver.entities.CustomRevisionEntity;
import xw.legacyserver.rest.ChargeCodeRest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping("/chargecodes")
public class ChargeCodeController {

    @Autowired
    ChargeCodeDAO chargeCodeDAO;
    @PersistenceContext
    EntityManager em;

    // Spring 4.3+   @GetMapping("/chargecodes/audit/{id}/revisions")
    @RequestMapping(method = RequestMethod.GET,
        value = "/audit/{id}/revisions")
    @ResponseBody
    public List<Number> revisionsChargeCode(
        @PathVariable Integer id
    ) {
        List<Number> revisionNumbers =
            AuditReaderFactory.get(em).getRevisions(ChargeCode.class, id);
        return revisionNumbers;
    }

    //    @GetMapping("/chargecodes/audit/{id}/{revision}")
    @RequestMapping(method = RequestMethod.GET,
        value = "/audit/{id}/{revision}")
    @ResponseBody
    public ChargeCode auditChargeCode(
        @PathVariable Integer id,
        @PathVariable Integer revision
    ) {
        return AuditReaderFactory.get(em).find(ChargeCode.class, id,
            revision
        );
    }

    //    @GetMapping("/chargecodes/audit/{id}/haschange/{property}")
    @RequestMapping(method = RequestMethod.GET,
        value = "/audit/{id}/haschange/{property}")
    @ResponseBody
    public List<Tuple> auditChangesChargeCode(
        @PathVariable Integer id,
        @PathVariable String property
    ) {
        AuditQuery query = AuditReaderFactory.get(em).createQuery()
            .forRevisionsOfEntity(ChargeCode.class, false, true)
            .add(AuditEntity.id().eq(id))
            .add(AuditEntity.property(property).hasChanged());

        List<Object[]> queryResult = query.getResultList();

        List<Tuple> result = queryResult.stream().map(o -> {
            ChargeCode cc = (ChargeCode) o[0];
            CustomRevisionEntity revision = (CustomRevisionEntity) o[1];
            RevisionType type = (RevisionType) o[2];
            return new Tuple(
                cc,
                revision.getRev(),
                new Date(revision.getRevtstmp()),
                type
            );
        }).collect(Collectors.toList());

        return result;
    }

    //    @GetMapping("/chargecodes")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ChargeCode> changecodes() {
        return chargeCodeDAO.findAll();
    }

    //    @PostMapping(value = "/chargecodes")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ChargeCode createChargeCode(
        @RequestBody ChargeCodeRest chargeCode
    ) {
        ChargeCode cc = new ChargeCode(null, chargeCode.getName(),
            chargeCode.getCode(), chargeCode.getDescription(),
            chargeCode.isExpired(), new Date(), new Date(), new ArrayList<>()
        );
        return chargeCodeDAO.save(cc);
    }

    //    @PutMapping("/chargecodes/{id}")
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public ChargeCode update(
        @RequestBody ChargeCodeRest chargeCode,
        @PathVariable Integer id
    ) {
        ChargeCode existing = chargeCodeDAO.find(id);
        if (existing == null) {
            throw new RestClientException("Could not find ChargeCode with ID:" +
                " " + id);
        }
        existing.setCode(chargeCode.getCode());
        existing.setName(chargeCode.getName());
        existing.setDescription(chargeCode.getDescription());
        existing.setExpired(chargeCode.isExpired());
        return chargeCodeDAO.save(existing);
    }

    @Value
    @Data
    @AllArgsConstructor
    private class Tuple {
        ChargeCode chargeCode;
        Integer rev;
        Date revtstmp;
        RevisionType revType;
    }
}
