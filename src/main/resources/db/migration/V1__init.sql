-- Initial schema for the TAA Auslandsreiseversicherung (foreign travel insurance) webapp.
-- This baseline migration exists so that Flyway has at least one migration to run
-- on startup. Extend with further V2__*.sql, V3__*.sql migrations as the data model grows.

CREATE TABLE travel_insurance_application (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(100)  NOT NULL,
    last_name       VARCHAR(100)  NOT NULL,
    email           VARCHAR(255)  NOT NULL,
    destination     VARCHAR(100)  NOT NULL,
    travel_start    DATE          NOT NULL,
    travel_end      DATE          NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
