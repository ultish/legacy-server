package xw.legacyserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import xw.legacyserver.dao.ChargeCodeDAO;
import xw.legacyserver.entities.ChargeCode;
import xw.legacyserver.rest.ChargeCodeRest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Transactional
public class ChargeCodeController {

    @Autowired
    ChargeCodeDAO chargeCodeDAO;

    @GetMapping("/chargecodes")
    @ResponseBody
    public List<ChargeCode> changecodes() {
        return chargeCodeDAO.findAll();
    }

    @PostMapping(value = "/chargecodes")
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

    @PutMapping("/chargecodes/{id}")
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
}
