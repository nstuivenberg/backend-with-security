# NOVI Spring security backend

Deze backend is gemaakt door NOVI voor opleidingsdoeleinden. Studenten die een frontend opdracht maken en slechts gebruik willen maken van een backend voor het registeren en inloggen van gebruikers, kunnen gebruik maken van deze service. Het is niet mogelijk om andere informatie (naast `email`, `gebruikersnaam`, `wachtwoord` en `role`) op te slaan in deze database.



## Wensen vanuit de frontend
Als gebruiker wil ik graag:
* Nieuwe gebruikers kunnen aanmaken
* Kunnen inloggen met een bestaand account
* De gegevens van het huidige ingelogde account opvragen met alleen de accessToken
* De gegevens van een bestaand account (die op dat moment ingelogd is) kunnen wijzigen
* Alle gebruikers kunnen opvragen, als ik een admin ben

## Inhoud
 * [Beschrijving](#beschrijving)
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
    
## Beschrijving


### Gebruikersrollen
Deze backend ondersteunt het gebruik van twee user-rollen:
1. `user`
2. `admin`

Elke gebruiker kan één of meerdere rollen hebben. Deze worden vastgesteld bij het _aanmaken_ van de gebruiker en kunnen daarna niet meer worden gewijzigd. Het is belangrijk om je te realiseren dat dat wanneer een gebruiker de `admin`-rol heeft dat deze dan niet _automatisch_ ook de `user`-rol heeft. 

Dat zit zo: stel, je maakt een gebruiker aan met een admin-rol en logt daarmee in. Als je met dit account REST-endpoints wil benaderen die toegankelijk zijn voor gebruikers met een `user` rol, zou je wellicht denken dat een admin dan automatisch ook geautoriseerd is om deze te bekijken. Dat is echter niet zo. Als je met een account dat alléén een admin rol heeft probeert om een user-endpoint te benaderen, geeft de applicatie de volgende error:

```
HTTP 401 Unauthorized
```

In de situatie dat een admin zowel gebruikers-rechten heeft als admin-rechten, krijgt deze dus twee rollen toegewezen. 

### Rest endpoints
Alle rest-endpoints draaien op deze server: https://polar-lake-14365.herokuapp.com. Dit is de basis-uri. Alle voorbeeld-data betreffende de endpoints zijn in JSON format weergegeven. 

** 0. Test **
`GET /api/test/all`

Dit endpoint is vrij toegankelijk en is niet afgeschermd. Het is daarom een handig endpoint om te testen of het verbinden met de backend werkt. De response bevat een enkele string: `"Public Content."`

** 1. Registreren**
`POST /api/auth/signup`

Het aanmaken van een nieuwe gebruiker (met user-rol) vereist de volgende informatie:

```json
{
   "username": "piet",
   "email" : "piet@novi.nl",
   "password" : "123456",
   "role": ["user"]
}
```

Het aanmaken van een nieuwe gebruiker (met admin-rol) vereist de volengende informatie:

```json
{
   "username": "klaas",
   "email" : "klaas@novi.nl",
   "password" : "123456",
   "role": ["admin"]
}
```

Het is ook mogelijk een gebruiker aan te maken met twee rollen:

```json
{
   "role": ["user", "admin"]
}
```

De response bevat een succesmelding.

_Let op_: er mogen géén additionele keys worden meegestuurd. Het e-mailadres moet altijd een e-mailadres zijn (met @ erin) anders geeft de backend een foutmelding.

** 2. Inloggen**
`POST /api/auth/signup`

Het inloggen van een bestaande gebruiker kan alleen als deze al geregistreerd is. Inloggen vereist de volgende informatie:

```json
{
   "username": "user",
   "password" : "123456",
}
```

De response bevat een authorisatie-token (JWT) en alle gebruikersinformatie. Onderstaand voorbeeld laat de repsonse zien na het inloggen van een gebruiker met een admin-rol:

```json
{
    "id": 6,
    "username": "mod3",
    "email": "mod3@novi.nl",
    "roles": [
        "ROLE_USER",
        "ROLE_MODERATOR"
    ],
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb2QzIiwiaWF0IjoxNTk1NTg4MDk0LCJleHAiOjE1OTU2NzQ0OTR9.AgP4vCsgw5TMj_ePbPzMJXWWBNfFphJBHzAvTFyW9fzZ6UL-JO42pRq9puXAOlGh4hTijspAQAS-J8doHqADTA",
    "tokenType": "Bearer"
}
```

** 3. Gegevens opvragen **
`GET /api/user`

Het opvragen van de gebruikersgegevens vereist een `Bearer` + `token` header:

```json
{
   "Authorization": "Bearer xxx.xxx.xxx",
}
```

De response bevat alle informatie over de gebruiker zoals beschreven bij registratie. 

** 4. Gegevens aanpassen **
`POST /api/user/update`

Het is mogelijk om een gebruiker zijn eigen e-mail of wachtwoord aan te laten passen. Hiervoor moet, naast de informatie die wordt geüpdate, een `Bearer` + `token` header worden meegestuurd:

```json
{
   "Authorization": "Bearer xxx.xxx.xxx",
}
```

Om het e-mailadres aan te passen moet de volgende data worden meegestuurd:

```json
{
   "email" : "sjaak@sjaak.nl",
}
```

Om het wachtwoord aan te passen moet de volgende data worden meegestuurd:

```json
{
   "email" : "sjaak@sjaak.nl",
}
```


** 5. Alle gebruikers [admin]**
`GET /api/admin/all`

Dit rest-endpoint geeft een lijst van alle gebruikers terug, maar is alleen toegankelijk voor gebruikers met de admin-rol. (IETS MEESTUREN?)

** 6. Testen van de rollen [user] [admin]**
`GET /api/test/user`
Alleen gebruikers met een user-rol kunnen dit endpoint benaderen. De response bevat een enkele string: `"User Content."` (IETS MEESTUREN?)

`GET /api/test/admin`
Alleen gebruikers met een admin-rol kunnen dit endpoint benaderen. De response bevat een enkele string: `"Admin Board."` (IETS MEESTUREN?)

### Restpoints benaderen in Postman
Wanneer je een authorisation-token hebt ontvangen zal de backend bij alle beveiligde endpoints willen controleren wie de aanvrager is op basis van deze token. Dit zul je dus ook in Postman mee moeten geven.

![Plaatje postman met Authorization](img/auth_postman_example.png)

Onder het kopje headers voeg je als `Key` `Authorization` toe. Daarin zet je `<TOKEN TYPE> <SPATIE> <ACCESSTOKEN>`. 
Zonder de <>. Zie plaatje hierboven.
