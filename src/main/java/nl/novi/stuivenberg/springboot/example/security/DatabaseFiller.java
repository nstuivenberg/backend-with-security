package nl.novi.stuivenberg.springboot.example.security;

import nl.novi.stuivenberg.springboot.example.security.payload.request.SignupRequest;
import nl.novi.stuivenberg.springboot.example.security.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * In deze klasse voegen we drie gebruikers met één rol toe en eentje met allen:
 * admin, admin@admin.nl, 123456, ROL: admin
 * mod, mod@mod.nl, 123456, ROL: mod
 * user, user@user.nl, 123456, ROL: user
 * superuser, super@user.nl, 123456, ROLLEN: Admin, mod, user
 *
 * UITLEG COMPONENT ANNOTATIE
 * http://zetcode.com/springboot/component/
 */
@Component
public class DatabaseFiller implements CommandLineRunner {

    private final AuthorizationService authorizationService;

    @Autowired
    public DatabaseFiller(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public void run(String... args) {

        Set<String> rollen = new HashSet<>();
        rollen.add("admin");

        SignupRequest admin = new SignupRequest();
        admin.setUsername("nick");
        admin.setEmail("nick@admin.nl");
        admin.setPassword("nicknick");
        admin.setRole(rollen);
        authorizationService.registerUser(admin);

        SignupRequest user = new SignupRequest();
        user.setUsername("sjaak");
        user.setEmail("sjaak@sjaak.nl");
        user.setPassword("sjaaksjaak");
        rollen.add("user");
        user.setRole(rollen);
        authorizationService.registerUser(user);

        SignupRequest superuser = new SignupRequest();
        superuser.setUsername("superuser");
        superuser.setEmail("super@user.nl");
        superuser.setPassword("123456");
        rollen.add("admin");
        superuser.setRole(rollen);
        authorizationService.registerUser(superuser);

    }
}
