package nl.novi.stuivenberg.springboot.example.security.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String generatePublicContent() {
        return "De API is bereikbaar.";
    }

    public String generateUserContent() {
        return "Het geheim voor een goed cijfer is niet het maken van je huiswerk en hard je best doen, " +
                "maar uitzoeken wie jouw eindopdracht nakijkt en deze persoon overladen met bloemen en chocolaatjes.";
    }

    public String generateAdminContent() {
        return "Dit endpoint is alleen toegankelijk voor adminrollen.";
    }

}
