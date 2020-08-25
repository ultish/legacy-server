package xw.legacyserver.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xw.legacyserver.entities.ChargeCode;
import xw.legacyserver.rest.ChargeCodeRest;

import java.util.Arrays;
import java.util.List;

@RestController
public class ChargeCodeController {

    @GetMapping("/chargecodes")
    public List<ChargeCodeRest> changecodes() {
        return Arrays.asList(new ChargeCodeRest(1, "hello"));
    }
}
