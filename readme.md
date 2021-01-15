# Spring security voorbeeld
Een inleiding

## Wensen vanuit de frontend
Als gebruiker wil ik graag:
* Nieuwe gebruikers kunnen aanmaken
* Kunnen inloggen met een bestaand account
* De gegevens van het huidige ingelogde account opvragen met alleen de accessToken
* De gegevens van een bestaand account (die op dat moment ingelogd is) kunnen wijzigen
* Alle gebruikers kunnen opvragen, als ik een admin ben

## Inhoud
 * [Voorbereiding](#voorbereiding)
 * [Korte uitleg](#korte-uitleg)
    * [Gebruikersrollen](#gebruikersrollen)
    * [Rest endpoints](#rest-endpoints)
 * [Hoe te gebruiken](#hoe-te-gebruiken)
    * [Gebruiker aanmaken](#gebruiker-aanmaken)
    * [Inloggen](#inloggen)
    * [Rest endpoint benaderen met access-token](#rest-endpoint-benaderen-met-access-token)
 * [Beveiligingslek](#beveiligingslek) 
 * [Uitleg code](#uitleg-code)
    * [De payload-package](#de-payload-package-dto)
    * [Repository package](#repository-package)
    

    

### Gebruikersrollen
Dit voorbeeld maakt gebruik van drie user-rollen. `user` & `admin`. Elke gebruiker kan 0 tot meerdere rollen 
hebben. Het is belangrijk om je te realiseren dat wanneer een gebruiker de `admin`-rol heeft dat deze dan niet
automatisch de `user`-rol heeft. Er is geen mogelijkheid om de rol van de gebruiker aan te passen.

###### Voorbeeld 
Je maakt een gebruiker aan met de admin-rol. Je logt in met deze gebruiker. Als je met deze gebruiker wilt communiceren
met een rest-endpoint dat alleen antwoord op gebruikers met de rol `user` dan geeft de applicatie de volgende error 
terug:
```
HTTP 401 Unauthorized
```

### Rest endpoints.
Alle rest-endpoints draaien op deze server: https://polar-lake-14365.herokuapp.com.

De back-end is op de volgende end-points te bereiken:
 1. `/api/auth/signup`
    POST-request
    * Hier kun je de volgende JSON sturen om een gebruiker aan te maken.
     ```json
        {
        "username": "user",
        "email" : "user@user.com",
        "password" : "123456",
        "role": ["user"]
        }
     ```
    Een e-mailadres moet altijd een e-mailadres zijn.
 2. `/api/auth/signin`
    POST-request
    * Hier kun je login gegevens naar sturen. Je krijgt een Authorisatie-token terug.
    ```json
        {
        "username":"user",
        "password":"123456"
        }
    ```
 3. `/api/test/all`
    GET-request
    * Iedereen kan data uit deze end-point uitlezen.
 4. `/api/test/user`
    GET-request
    * Alleen (ingelogd) gebruikers met de user-rol kunnen data uit deze API uitlezen.
 5. `/api/test/mod`
    GET-request
     * Alleen (ingelogd) gebruikers met de mod-rol kunnen data uit deze API uitlezen.
 6. `/api/test/admin`
    GET-request
     * Alleen (ingelogd) gebruikers met de admin-rol kunnen data uit deze API uitlezen.
7. `/api/user`
    GET-request
   * Dit rest-endpoint retourneert de gegevens van de gebruiker. Je moet de Bearer + token meesturen.
8. `/api/user/update`
    POST-request
   * Dit rest-endpoint laat de gebruiker zijn e-mail en of wachtwoord aanpassen. Je moet de Bearer + token meesturen.
    ```json
    {
        "email" : "sjaak@sjaak.nl",
        "password" : "12",
        "repeatedPassword" : "121212"
    }
    ```
9. `/api/admin/all`
   GET-request
    * Dit rest-endpoint geeft een lijst van alle gebruikers terug. Deze is alleen toegankelijk voor gebruikers met de admin-rol.
    
 
## Hoe te gebruiken met postman.
Je kunt tegen de hierboven genoemde rest-points communiceren.

### Gebruiker aanmaken
Praat via Postman met de volgende link: `http://localhost:8080/api/auth/signup` en geef de volgende JSON in de body mee:

#### Gebruiker met userrol aanmaken
```json
{
    "username": "user",
    "email" : "user@user.com",
    "password" : "123456",
    "role": ["user"]
}
```
#### Gebruiker met mod- en userrol aanmaken
```json
{
    "username": "mod",
    "email" : "mod@mod.com",
    "password" : "123456",
    "role": ["mod", "user"]
}
```
#### Gebruiker met adminrol aanmaken
```json
{
    "username": "admin",
    "email" : "admin@admin.com",
    "password" : "123456",
    "role": ["admin"]
}
```

#### Gebruiker met alledrie de rollen (niet veilig)
```json
{
    "username": "superadmin",
    "email" : "superadmin@admin.com",
    "password" : "123456",
    "role": ["admin", "mod", "user"]
}
```

### Inloggen
Wanneer je inlogt geeft de backend-server een Json WebToken terug. Bewaar deze, want deze moet je meesturen.

Praat via Postman met de volgende link: `http://localhost:8080/api/auth/signin` en geef de volgende JSON in de body mee:
#### Inloggen user
```json
{
    "username":"user",
    "password":"123456"
}
```

#### Inloggen mod
```json
{
    "username":"mod",
    "password":"123456"
}
```

#### Inloggen admin
```json
{
    "username":"admin",
    "password":"123456"
}
```
#### Resultaat
De backend-server communiceert het volgende (soortgelijks) terug:
```json
{
    "id": 6,
    "username": "mod3",
    "email": "mod3@mod.com",
    "roles": [
        "ROLE_USER",
        "ROLE_MODERATOR"
    ],
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb2QzIiwiaWF0IjoxNTk1NTg4MDk0LCJleHAiOjE1OTU2NzQ0OTR9.AgP4vCsgw5TMj_ePbPzMJXWWBNfFphJBHzAvTFyW9fzZ6UL-JO42pRq9puXAOlGh4hTijspAQAS-J8doHqADTA",
    "tokenType": "Bearer"
}
```

Wil je als ingelogde gebruiker nu tegen de beveiligde rest-points aanpraten dan moet je altijd `tokenType` en
`accesstoken` meesturen. Zie volgend kopje.

### Rest endpoint benaderen met access-token
Op het moment dat bovenstaande is gelukt, dan heb je van de server een Bearer + access  token ontvangen. Spring security
geeft deze uit en controleert op basis van die token wat de gebruiker wel of niet mag doen op de website. Dus willen we
praten met één van de drie beveiligde rest endpoints, dan moeten we token type + access token meegeven in postman. Dat
doen we zo:

![Plaatje postman met Authorization](img/auth_postman_example.png)

Onder het kopje headers voeg je als `Key` `Authorization` toe. Daarin zet je `<TOKEN TYPE> <SPATIE> <ACCESSTOKEN>`. 
Zonder de <>. Zie plaatje hierboven.

De volgende resultaten worden teruggegevn door de server, wanneer het succesvol is:

 1. `/api/test/all`
    * Iedereen kan data uit deze end-point uitlezen.
    * `Public Content.`
 2. `/api/test/user`
    * Alleen (ingelogd) gebruikers met de user-rol kunnen data uit deze API uitlezen.
    * `User Content.`
 3. `/api/test/mod`
     * Alleen (ingelogd) gebruikers met de mod-rol kunnen data uit deze API uitlezen.
     `Moderator Board.`
 4. `/api/test/admin`
     * Alleen (ingelogd) gebruikers met de admin-rol kunnen data uit deze API uitlezen.
     `Admin Board.`
