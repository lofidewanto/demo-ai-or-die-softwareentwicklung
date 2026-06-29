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

Das Deployment erfolgt über GitHub Pages. Dazu muss GitHub Pages in den
Repository-Einstellungen (*Settings → Pages*) aktiviert und ein GitHub-Actions-Workflow
(z. B. `.github/workflows/pages.yml`) ergänzt werden, der die HTML-Datei nach `public/`
kopiert (als `index.html` sowie unter Originalnamen) und sie bei jedem Push auf `main`
veröffentlicht.

### Lokal ansehen

```sh
open wm-2026-premium-report.html
```

## 2. TAA-Webapp – Auslandsreiseversicherung

Spring-Boot-Webanwendung für das DEVK-Produkt *Auslandsreiseversicherung* (TAA).
Das Projekt liegt derzeit als Scaffold vor; die fachlichen Anforderungen sind in der
User Story [`README-app-development-taa.md`](README-app-development-taa.md) beschrieben.

* **Artefakt:** `com.example:demo-ai-or-die-softwareentwicklung`
* **Stack:** Spring Boot 4.0.x, Java 21
* **Dependencies:** Spring Web MVC, Data JPA, Flyway, Thymeleaf, DevTools

### Projektstruktur

```
src/
├── main/
│   ├── java/com/example/taa/DemoTaaApplication.java   # Spring-Boot-Einstiegspunkt
│   └── resources/
│       ├── application.properties
│       └── db/migration/                              # Flyway-Migrationen
└── test/
    └── java/com/example/taa/DemoTaaApplicationTests.java
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
