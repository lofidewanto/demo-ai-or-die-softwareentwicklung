# demo-ai-or-die-softwareentwicklung

Experimente und Demos rund um **KI-gestützte Softwareentwicklung** ("AI or die").
Dieses Repository bündelt zwei eigenständige Anwendungsfälle, die jeweils aus einem
ausführlichen Prompt (Spezifikation) und dem daraus erzeugten Ergebnis bestehen.

## Inhalt des Repositories

| Datei / Verzeichnis | Beschreibung |
| --- | --- |
| `wm-2026-premium-report.html` | Vollständig eigenständiger HTML-Premium-Report zur FIFA WM 2026 (Analytics-Dashboard, kein externes CSS/JS). Wird über GitHub Pages veröffentlicht. |
| `wm-2026-premium-report.pdf` | PDF-Export desselben Reports. |
| `README-soccer-2026.md` | Prompt/Spezifikation, aus der der WM-2026-Report generiert wurde. |
| `README-app-development-taa.md` | User Story & Akzeptanzkriterien für die TAA-Webapp (Auslandsreiseversicherung). |
| `pom.xml`, `src/`, `mvnw`, `mvnw.cmd`, `.mvn/` | Spring-Boot-Projekt (`demo-ai-or-die-softwareentwicklung`) als technische Grundlage der TAA-Webapp. |

## 1. WM 2026 Premium Report

Ein datengetriebener Premium-Report zur FIFA Fußball-Weltmeisterschaft 2026:
Executive Dashboard, Gruppen- und Teamanalysen, Deutschland-Spezial, Wettmarkt-Bewertung,
Monte-Carlo-/K.-o.-Simulation und statistische Visualisierungen.

* **Technik:** HTML5 mit eingebettetem CSS/JavaScript, keine externen Bibliotheken, responsive (Mobile First), Light/Dark-Theme (Standard: Light).
* **Spezifikation:** siehe [`README-soccer-2026.md`](README-soccer-2026.md).

### Live (GitHub Pages)

* Report: https://lofidewanto.github.io/demo-ai-or-die-softwareentwicklung/

Das Deployment erfolgt automatisch über GitHub Actions
([`.github/workflows/pages.yml`](.github/workflows/pages.yml)): Der Workflow kopiert die
HTML-Datei nach `public/` (als `index.html` sowie unter Originalnamen) und veröffentlicht
sie bei jedem Push auf `main` über GitHub Pages. Voraussetzung ist, dass GitHub Pages in
den Repository-Einstellungen (*Settings → Pages → Source: GitHub Actions*) aktiviert ist.

### Lokal ansehen

```sh
open wm-2026-premium-report.html
```

## 2. TAA-Webapp – Auslandsreisekrankenversicherung

Spring-Boot-Webanwendung für das DEVK-Produkt *Auslandsreisekrankenversicherung* (TAA).
Die Anwendung bildet das Produkt fachlich ab und führt Nutzer:innen durch einen
digitalen Angebots- und Antragsprozess im DEVK-Design. Sie orientiert sich an der
DEVK-Referenzstrecke (`Anleitung → Allgemeine Angaben → Unser Angebot → Ihr Antrag →
Ihr Versicherungsschein`).

> Umsetzung der User Story
> [#1](https://github.com/lofidewanto/demo-ai-or-die-softwareentwicklung/issues/1).
> Der detaillierte Implementierungsplan liegt unter
> [`docs/user-stories-planung/us-001-auslandsreisekrankenversicherung-webapp.md`](docs/user-stories-planung/us-001-auslandsreisekrankenversicherung-webapp.md).
>
> **Hinweis:** reine Demonstrationsanwendung – kein echtes DEVK-Angebot, kein
> Vertragsabschluss, kein Beitragseinzug.

* **Artefakt:** `com.example:demo-ai-or-die-softwareentwicklung`
* **Stack:** Spring Boot 4.0.x, Java 21
* **Dependencies:** Spring Web MVC, Data JPA, Flyway, Thymeleaf, H2, DevTools,
  Bean Validation (`spring-boot-starter-validation` – einzige begründete Ergänzung,
  Version über den Parent-BOM verwaltet, konfliktfrei)

### Funktionsumfang

* Produkt-Landingpage mit Leistungen und Einstieg in den Abschlussprozess
* Mehrstufiger Angebotsrechner (POST-Redirect-GET, Session-Wizard mit Fortschritts-Stepper)
* Beitragsberechnung (`PremiumCalculator`) für 1–4 Personen, altersabhängig
* Serverseitige Eingabevalidierung inkl. IBAN-Prüfung (Mod-97), deutschsprachige
  Fehlermeldungen im DEVK-Design
* DEVK-Markendesign (Farben/Funktionsfarben, Typografie), responsiv (Mobile First)
* Persistenz des Antrags (H2 + Flyway) inkl. Kindtabelle für versicherte Personen
* Baseline-Security: HTTP-Security-Header, gehärtete Session-Cookies, Output-Escaping

### Architektur (Clean Architecture)

```
src/main/java/com/example/taa/
├── DemoTaaApplication.java
├── domain/          # Modell (Records/Enums), PremiumCalculator, Repository-Port (framework-frei)
├── application/     # Use-Cases (TravelInsuranceService), Command-DTO
├── infrastructure/  # JPA-Persistenz-Adapter, Flyway, Security-Header, Bean-Konfiguration
└── web/             # Controller, Formulare (Bean Validation), Session-Wizard, View-Modelle
src/main/resources/
├── templates/       # Thymeleaf (Layout-Fragment, Stepper, 4 Wizard-Seiten, Fehlerseite)
├── static/          # devk.css (Designsystem), wizard.js, favicon.svg
├── db/migration/    # Flyway V1 (Baseline) + V2 (Schema-Erweiterung)
├── application.properties
└── messages.properties / ValidationMessages.properties
src/test/java/com/example/taa/
├── domain/service/PremiumCalculatorTest.java                 # Unit
├── application/TravelInsuranceServiceTest.java               # Unit (Mockito)
├── web/QuoteWizardControllerTest.java                        # @WebMvcTest
└── infrastructure/persistence/...RepositoryAdapterTest.java  # @DataJpaTest
```

### Build & Start

```sh
# Anwendung starten
./mvnw spring-boot:run

# Tests ausführen
./mvnw test

# Paket bauen
./mvnw clean package
```

Die Anwendung ist anschließend unter http://localhost:8080 erreichbar.

## Repository

```
git clone https://github.com/lofidewanto/demo-ai-or-die-softwareentwicklung.git
```
