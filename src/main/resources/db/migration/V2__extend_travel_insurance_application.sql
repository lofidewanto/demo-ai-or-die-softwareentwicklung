-- V2: Extend the travel insurance application schema for the ARK webapp.
--
-- V1 established the baseline table. Following Flyway best practices we never
-- edit an applied migration; instead we add the additional columns and the
-- normalized child table for the (1..4) insured persons here.

ALTER TABLE travel_insurance_application
    ADD COLUMN salutation           VARCHAR(10)   NOT NULL DEFAULT 'HERR';
ALTER TABLE travel_insurance_application
    ADD COLUMN applicant_birth_date DATE          NOT NULL DEFAULT DATE '1970-01-01';
ALTER TABLE travel_insurance_application
    ADD COLUMN phone                VARCHAR(50);
ALTER TABLE travel_insurance_application
    ADD COLUMN street               VARCHAR(150)  NOT NULL DEFAULT '';
ALTER TABLE travel_insurance_application
    ADD COLUMN house_number         VARCHAR(20)   NOT NULL DEFAULT '';
ALTER TABLE travel_insurance_application
    ADD COLUMN postal_code          VARCHAR(10)   NOT NULL DEFAULT '';
ALTER TABLE travel_insurance_application
    ADD COLUMN city                 VARCHAR(100)  NOT NULL DEFAULT '';
ALTER TABLE travel_insurance_application
    ADD COLUMN iban                 VARCHAR(34)   NOT NULL DEFAULT '';
ALTER TABLE travel_insurance_application
    ADD COLUMN premium_amount       DECIMAL(10,2) NOT NULL DEFAULT 0.00;
ALTER TABLE travel_insurance_application
    ADD COLUMN premium_currency     VARCHAR(3)    NOT NULL DEFAULT 'EUR';

-- The demo defaults above only exist to satisfy NOT NULL for the ALTER; new
-- rows are always written with real values by the application.

-- Normalized child table for the insured persons (1..4 per application).
CREATE TABLE travel_insured_person (
    application_id BIGINT  NOT NULL,
    person_index   INT     NOT NULL,
    birth_date     DATE    NOT NULL,
    CONSTRAINT pk_travel_insured_person PRIMARY KEY (application_id, person_index),
    CONSTRAINT fk_insured_person_application
        FOREIGN KEY (application_id) REFERENCES travel_insurance_application (id)
);
