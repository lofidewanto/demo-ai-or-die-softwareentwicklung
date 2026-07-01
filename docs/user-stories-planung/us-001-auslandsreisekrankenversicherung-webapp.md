# Implementierungsplan – US-001: Webapp Auslandsreisekrankenversicherung (TAA)

> Technischer Implementierungsplan zur User Story
> [#1](https://github.com/lofidewanto/demo-ai-or-die-softwareentwicklung/issues/1)
> „Erstellung einer Webapp für die Auslandsreiseversicherung".
> Dieser Plan ist die verbindliche Grundlage der Umsetzung im selben Commit-Zyklus.
>
> **Ziel:** Demonstration einer produktionsnahen Spring-Boot-Webanwendung für das
> DEVK-Produkt *Auslandsreisekrankenversicherung (ARK / „TAA")*.

---

## 1. Zusammenfassung

Es wird eine Spring-Boot-Webanwendung erstellt, die

1. das Produkt **Auslandsreisekrankenversicherung** fachlich informativ darstellt
   (Landing-Page „Anleitung"),
2. einen **digitalen Abschlussprozess** (Angebotsrechner + Antrag) als mehrstufigen
   Wizard bereitstellt,
3. sich an der **DEVK-Referenzstrecke** (`vpuark/ark.html`) orientiert,
4. das **DEVK-Design** (Markenportal) umsetzt,
5. Eingaben **serverseitig validiert** und Fehler im DEVK-Design anzeigt,
6. **responsiv** auf Desktop, Tablet und Smartphone nutzbar ist.

Die Umsetzung folgt einer **Clean-Architecture-orientierten Schichtung**
(Domain / Application / Infrastructure / Web) und nutzt ausschließlich die in der
`pom.xml` vordefinierten Technologien plus **eine** fachlich zwingend notwendige
Ergänzung (Bean Validation).

---

## 2. Analyse der Referenz (Ist-Aufnahme)

Quellen (öffentlich): DEVK-Produktseite, DEVK-Referenzstrecke `vpuark/ark.html`,
DEVK-Markenportal.

### 2.1 Fachliches Produkt
- Weltweiter Versicherungsschutz im Ausland für **bis zu 2 Monate pro Reise**.
- **Jahresversicherung**; Beitrag **ab 15,90 € / Jahr** (Einzelperson bis zur
  Vollendung des 60. Lebensjahres – Faktum von der Produktseite).
- Leistungen u. a.: ärztliche Behandlung, Krankenhaus, schmerzstillende
  Zahnbehandlung, Krankenrücktransport, Überführung/Bestattung, Kinderbetreuung.

### 2.2 Referenzstrecke (Ablauf `vpuark/ark.html`)
Die linke Prozessnavigation („Ihre Eingaben") der Referenz gibt die Schritte vor:

| # | Schritt (Referenz)      | Inhalt                                                                 |
|---|-------------------------|------------------------------------------------------------------------|
| 1 | **Anleitung**           | Einstieg / Erklärung „So funktioniert's", benötigte Unterlagen         |
| 2 | **Allgemeine Angaben**  | Versicherungsbeginn, Anzahl zu versichernder Personen (1–4), Geburtsdatum je Person |
| 3 | **Unser Angebot**       | Berechneter Jahresbeitrag                                              |
| 4 | **Ihr Antrag**          | Antragsteller-/Versicherungsnehmerdaten + Bankverbindung               |
| 5 | **Ihr Versicherungsschein** | Bestätigung / Abschluss                                            |

Beobachtete Feldnamen der Referenz (Schritt „Allgemeine Angaben"):
`versicherungsbeginn` (date), `anzahlZuVersicherndePersonen` (1–4),
`geburtsdatumVP1..VP4` (date). Pflichtfeldkennzeichnung mit `*`
(„Diese Angaben sind erforderlich"). Buttons „Weiter" / „Abbrechen".

Beobachtete Security-Header der Referenz (werden gespiegelt):
`X-Content-Type-Options: nosniff`, `X-Frame-Options: SAMEORIGIN`,
`Referrer-Policy`, `Content-Security-Policy`, Session-Cookie
`SameSite=Lax; HttpOnly; Secure`.

### 2.3 DEVK-Designsystem (Markenportal)

**Primärfarben**

| Name       | HEX       | Einsatz                          |
|------------|-----------|----------------------------------|
| DEVK-Grün  | `#008746` | Marke, Header, Primär-Buttons    |
| Dunkelgrün | `#005437` | Headlines, Textakzente           |
| Hellgrün   | `#8bc379` | Flächen (leicht)                 |

**Funktionsfarben (digitale Interfaces – maßgeblich für Validierung/Status)**

| Name           | HEX       | Einsatz                         |
|----------------|-----------|---------------------------------|
| Interface Grün | `#008746` | Erfolg / positiv                |
| Interface Rot  | `#ea5237` | Fehler / negativ                |
| Interface Blau | `#4293d0` | Status / Info                   |
| Interface Gelb | `#f8ab1b` | Warnung / Button-Hintergrund    |

**Hellgrün-Abstufungen** (Trägerflächen): 20 % `#e8f2e4`, 40 % `#d1e6c9`, 60 % `#b9daaf`.

**Typografie:** Marken-Fonts sind *Ahkio* (Headlines) und *Univers Next* (Fließtext),
beide lizenzpflichtig. Das Markenportal definiert **Arial** als offizielle
Ersatzschrift und schreibt sie für **Produktnamen sowie formelle/vertragliche
Kontexte** ausdrücklich vor. Da es sich hier um einen Versicherungsantrag (Vertrag)
handelt, wird durchgängig eine **Arial-/Grotesk-Fallback-Stack** verwendet:
`Arial, "Helvetica Neue", Helvetica, "Univers Next", sans-serif`.
(Keine Bündelung lizenzpflichtiger Fonts; keine externen Font-CDNs → self-contained.)

---

## 3. Architektur

### 3.1 Schichten (Clean Architecture, pragmatisch)

```
web            → Controller, Formulare (Bean Validation), Session-Wizard, Thymeleaf-Views
  │  (nutzt)
application    → Use-Cases (TravelInsuranceService), Command-DTOs
  │  (nutzt)
domain         → Modell (Records/Enums), PremiumCalculator, Repository-Port  ← frei von Framework
  ▲  (implementiert Port)
infrastructure → JPA-Adapter (Persistence), Flyway, Security-Header, Bean-Konfiguration
```

**Abhängigkeitsrichtung** zeigt stets nach innen zur Domain. Die Domain kennt weder
Spring noch JPA (Testbarkeit, Wartbarkeit). Persistenz wird über einen
**Repository-Port** (Interface in `domain`) entkoppelt, der von einem
**Adapter in `infrastructure`** implementiert wird (Hexagonal / Ports & Adapters).

### 3.2 Paketstruktur (`com.example.taa`)

```
domain
  ├─ model/        Salutation, InsuredPerson, Money, Applicant, TravelInsuranceApplication
  ├─ service/      PremiumCalculator
  └─ repository/   TravelInsuranceApplicationRepository (Port)
application
  ├─ TravelInsuranceService
  └─ command/      SubmitApplicationCommand
infrastructure
  ├─ config/       DomainConfig (@Bean PremiumCalculator), SecurityHeadersFilter
  └─ persistence/  TravelInsuranceApplicationEntity, InsuredPersonEmbeddable,
                   TravelInsuranceApplicationJpaRepository, ...RepositoryAdapter, ...Mapper
web
  ├─ HomeController
  ├─ QuoteWizardController
  ├─ form/         GeneralDetailsForm, ApplicationForm
  ├─ session/      QuoteWizard (@SessionScope)
  └─ support/      WizardStep (enum, Stepper-Modell)
```

### 3.3 Domänenmodell (Kern)

- `Money(BigDecimal amount, String currency)` – Wertobjekt, Formatierung `de-DE`.
- `InsuredPerson(LocalDate birthDate)` – `ageAt(LocalDate)`.
- `Salutation` – `FRAU`, `HERR`, `DIVERS`.
- `Applicant(salutation, firstName, lastName, birthDate, street, houseNumber,
  postalCode, city, email, phone)`.
- `TravelInsuranceApplication` (Aggregat): `id`, `insuranceStart`,
  `List<InsuredPerson>`, `Applicant`, `iban`, `Money premium`, `Instant submittedAt`.
  Factory `newApplication(...)` (ohne `id`/`submittedAt`).

Java 21: konsequent **Records** für unveränderliche Wertobjekte.

### 3.4 Beitrags-Logik (`PremiumCalculator`) – dokumentierte Demo-Annahmen

Basiert auf dem **Faktum** „15,90 € / Jahr für Einzelperson bis 60". Alle weiteren
Werte sind **nachvollziehbare Demo-Annahmen** (kein echter DEVK-Tarif):

| Konstellation                        | Jahresbeitrag |
|--------------------------------------|---------------|
| Person, Alter < 60                   | 15,90 €       |
| Person, Alter ≥ 60 (Senior)          | 42,00 €       |
| 1–2 Personen                         | Summe der Einzelbeiträge |
| 3–4 Personen (Familientarif), alle < 60 | 39,90 € (Pauschale) |
| 3–4 Personen, mind. 1 Person ≥ 60    | 69,00 € (Pauschale) |

Alter = `Period.between(geburtsdatum, versicherungsbeginn).getYears()`.
Ergebnis stets `EUR`. Werte als benannte Konstanten, unit-getestet.

---

## 4. Prozess / Wizard (Soll)

Spiegelt die Referenzstrecke. **POST-Redirect-GET**, Zustand in
`@SessionScope`-Bean `QuoteWizard`. Guards sichern die Reihenfolge
(Bearbeitungsstand nachvollziehbar).

| Route (GET/POST)                     | Schritt / View                     | Aktion |
|--------------------------------------|------------------------------------|--------|
| `GET /`                              | Landing „Anleitung" (`index`)      | Produktinfos + CTA |
| `GET/POST /rechner/allgemeine-angaben` | „Allgemeine Angaben"             | Validierung → Beitrag berechnen → Redirect Angebot |
| `GET /rechner/angebot`               | „Unser Angebot"                    | Beitrag anzeigen (Guard) |
| `GET/POST /rechner/antrag`           | „Ihr Antrag"                       | Validierung → `submit()` → Redirect Schein |
| `GET /rechner/versicherungsschein`   | „Ihr Versicherungsschein"          | Bestätigung + Antragsnummer (Guard) |

**Stepper** (`WizardStep`): Allgemeine Angaben → Unser Angebot → Ihr Antrag →
Ihr Versicherungsschein, mit aktivem/erledigtem Status je Seite.

---

## 5. Validierung (AK 5 & 6)

Serverseitig autoritativ mit **Jakarta Bean Validation** (Hibernate Validator).

**`GeneralDetailsForm`**
- `insuranceStart` `@NotNull @FutureOrPresent`
- `numberOfPersons` `@NotNull @Min(1) @Max(4)`
- `birthDateP1..P4` – `@Past`; konditionale Pflicht (P1 immer, P2..P4 abhängig von
  `numberOfPersons`) via `@AssertTrue`-Methoden; Plausibilität Alter (0–120).

**`ApplicationForm`**
- `salutation` `@NotNull`; `firstName`/`lastName` `@NotBlank @Size(max=100)`
- `birthDate` `@NotNull @Past`
- `street`/`city` `@NotBlank`; `houseNumber` `@NotBlank @Size(max=20)`
- `postalCode` `@NotBlank @Pattern("\\d{5}")`
- `email` `@NotBlank @Email`; `phone` optional `@Pattern`
- `iban` `@NotBlank` + Format-/Prüfsummen-Check (DE-IBAN, Mod-97) via `@AssertTrue`
- `acceptTerms`, `acceptPrivacy` `@AssertTrue` (Bedingungen/Datenschutz)

Fehlermeldungen zentral in **`ValidationMessages.properties`** (Deutsch), Anzeige
als Fehler-Summary + feldnahe Meldungen im **Interface-Rot** (`#ea5237`).

---

## 6. Persistenz (nutzt vorhandene JPA/Flyway/H2-Dependencies)

- Bestehende Migration `V1__init.sql` bleibt **unverändert** (Flyway-Immutabilität).
- Neue Migration **`V2__extend_travel_insurance_application.sql`**:
  - ergänzt Spalten: `salutation, applicant_birth_date, phone, street, house_number,
    postal_code, city, iban, premium_amount, premium_currency`,
  - legt Kindtabelle **`travel_insured_person(application_id, birth_date)`** an
    (1..4 versicherte Personen, normalisiert).
- Mapping der vorhandenen NOT-NULL-Spalten aus V1:
  `first_name/last_name/email` = Antragsteller; `destination = "Weltweit"`;
  `travel_start = Versicherungsbeginn`; `travel_end = Beginn + 1 Jahr`.
- JPA: `@ElementCollection` für versicherte Personen; Adapter mappt Domain ↔ Entity.

---

## 7. Security & Betrieb (Best Practices, ohne Overengineering)

- **Security-Header** dependency-frei via Servlet-Filter (spiegelt Referenz):
  `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`,
  `Referrer-Policy: same-origin`, restriktive `Content-Security-Policy` (`'self'`).
- **Session-Cookie** gehärtet: `HttpOnly`, `SameSite=Lax`, Timeout 30 min.
- **XSS**: Thymeleaf escaped standardmäßig alle Ausgaben.
- **Eingaben**: durchgängige serverseitige Validierung.
- **Bewusste Abgrenzung (dokumentiert):** Kein `spring-boot-starter-security`.
  Begründung: öffentlicher, nicht authentifizierter Angebots-/Antragsdemonstrator
  ohne Login/Autorisierung; volle Spring-Security-Integration (inkl.
  CSRF-Token-Handling) wäre für den Demo-Umfang unverhältnismäßig (AK 8: keine
  unnötigen Abhängigkeiten). Für einen echten Produktivbetrieb würde Spring Security
  inkl. CSRF ergänzt – als Ausblick vermerkt.

---

## 8. Abhängigkeiten (`pom.xml`)

- **Keine** bestehende Version wird geändert (Parent-BOM `spring-boot 4.0.7`,
  Java 21). Vorhandene Starter (webmvc, thymeleaf, data-jpa, flyway, h2, devtools,
  Test-Starter) werden bevorzugt genutzt.
- **Eine Ergänzung:** `spring-boot-starter-validation`.
  - *Begründung:* Bean Validation (Hibernate Validator + Jakarta EL) ist im
    Classpath **nicht** vorhanden, aber für AK 5/6 (Eingabevalidierung, Pflichtfelder,
    Fehlermeldungen) zwingend erforderlich.
  - *Konfliktfreiheit:* Version wird vom Spring-Boot-Parent-BOM verwaltet
    (kein `<version>`-Override) → keine Versionskonflikte, kein Überschreiben.

---

## 9. Teststrategie (Testbarkeit)

| Testart              | Fokus                                                              |
|----------------------|-------------------------------------------------------------------|
| Unit                 | `PremiumCalculatorTest` – alle Tarif-/Alters-/Personen-Fälle       |
| Unit (Mock)          | `TravelInsuranceServiceTest` – Submit persistiert & liefert Beitrag |
| Web-Slice `@WebMvcTest` | `QuoteWizardControllerTest` – Render, Validierungsfehler, Redirects |
| Persistence `@DataJpaTest` | Adapter-Test – Speichern/Lesen inkl. Kindtabelle & Flyway     |
| Kontext              | vorhandener `DemoTaaApplicationTests.contextLoads`                 |

---

## 10. Abbildung der Akzeptanzkriterien

| AK | Umsetzung |
|----|-----------|
| 1 Technische Basis | Spring Boot 4.0.7 unverändert; nur `starter-validation` ergänzt (begründet) |
| 2 Produkt          | Landing-Page mit Leistungen/Beitrag + digitaler Antragsprozess |
| 3 Referenzstrecke  | Wizard spiegelt Anleitung→Allg. Angaben→Angebot→Antrag→Schein |
| 4 DEVK-Design      | CSS-Designsystem mit DEVK-Farben/Funktionsfarben, Grün-Header, responsive |
| 5 Usability        | Stepper, klare Labels, Pflichtfeld-`*`, Fehlerhinweise, Bearbeitungsstand |
| 6 Validierung      | Bean Validation, konsistente Fehlermeldungen im DEVK-Design |
| 7 Responsivität    | Mobile-First-CSS, Breakpoints Tablet/Desktop |
| 8 Qualität         | Clean Architecture, Schichtentrennung, Tests, Doku |

---

## 11. Annahmen (explizit)

1. Beitragswerte außer „15,90 € (< 60)" sind Demo-Annahmen (Abschnitt 3.4).
2. Produkt = **Jahresversicherung, weltweit**, wie in der Referenz-Strecke; keine
   Einmalreise-/Regionsvarianten (nicht Teil der Referenz-Schritte → kein
   Overengineering).
3. DEVK-Logo wird als schlichte, self-contained **Wortmarke „DEVK"** im
   Marken-Grün dargestellt (keine Hotlinks/lizenzsensiblen Assets in der Demo).
4. Zahlung: nur **Bankverbindung (IBAN)** wie in der Referenz („Bankverbindungsdaten
   des Antragstellers"); kein realer Zahlungs-/Policierungs-Backendaufruf.
5. H2 In-Memory: Daten werden pro Start neu aufgebaut (Demo).

---

## 12. Arbeitsschritte (Umsetzungsreihenfolge)

1. `pom.xml`: `spring-boot-starter-validation` ergänzen.
2. Domain (Model, `PremiumCalculator`, Repository-Port).
3. Application (`TravelInsuranceService`, Command).
4. Infrastructure (JPA-Entity/Adapter/Mapper, `V2`-Migration, Security-Filter, Config).
5. Web (Controller, Formulare, Session-Wizard, `WizardStep`).
6. Views (Thymeleaf-Layout/Fragmente, 5 Seiten, Fehlerseite) + `devk.css`.
7. Konfiguration (`application.properties`-Härtung, `ValidationMessages.properties`).
8. Tests.
9. Build (`./mvnw clean verify`), manuelle Verifikation, README-Aktualisierung.
