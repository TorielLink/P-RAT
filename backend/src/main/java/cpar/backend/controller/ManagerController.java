package cpar.backend.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
@Profile("manager")
public class ManagerController {

    @GetMapping("/rapport")
    public String rapport() {
        return "Rapport réservé au profil manager";
    }
}