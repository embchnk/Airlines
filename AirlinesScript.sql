
CREATE SEQUENCE public.admin_admin_pk_seq;

CREATE TABLE public.admin (
                admin_pk INTEGER NOT NULL DEFAULT nextval('public.admin_admin_pk_seq'),
                login VARCHAR NOT NULL,
                password VARCHAR NOT NULL,
                CONSTRAINT admin_pk PRIMARY KEY (admin_pk)
);


ALTER SEQUENCE public.admin_admin_pk_seq OWNED BY public.admin.admin_pk;

CREATE SEQUENCE public.registration_code_code_pk_seq;

CREATE TABLE public.registration_code (
                code_pk INTEGER NOT NULL DEFAULT nextval('public.registration_code_code_pk_seq'),
                code VARCHAR NOT NULL,
                admin_pk INTEGER NOT NULL,
                CONSTRAINT code_pk PRIMARY KEY (code_pk)
);


ALTER SEQUENCE public.registration_code_code_pk_seq OWNED BY public.registration_code.code_pk;

CREATE SEQUENCE public.airport_airport_pk_seq;

CREATE TABLE public.airport (
                airport_pk INTEGER NOT NULL DEFAULT nextval('public.airport_airport_pk_seq'),
                city VARCHAR NOT NULL,
                CONSTRAINT airport_pk PRIMARY KEY (airport_pk)
);


ALTER SEQUENCE public.airport_airport_pk_seq OWNED BY public.airport.airport_pk;

CREATE SEQUENCE public.flight_flight_pk_seq;

CREATE TABLE public.flight (
                flight_pk INTEGER NOT NULL DEFAULT nextval('public.flight_flight_pk_seq'),
                departure_airport_pk INTEGER NOT NULL,
                arrival_airport_pk INTEGER NOT NULL,
                departure TIMESTAMP NOT NULL,
                arrival TIMESTAMP NOT NULL,
                reservations INTEGER NOT NULL,
                CONSTRAINT flight_pk PRIMARY KEY (flight_pk, departure_airport_pk, arrival_airport_pk)
);


ALTER SEQUENCE public.flight_flight_pk_seq OWNED BY public.flight.flight_pk;

CREATE SEQUENCE public.company_company_pk_seq_1;

CREATE TABLE public.company (
                company_pk INTEGER NOT NULL DEFAULT nextval('public.company_company_pk_seq_1'),
                name VARCHAR NOT NULL,
                CONSTRAINT company_pk PRIMARY KEY (company_pk)
);


ALTER SEQUENCE public.company_company_pk_seq_1 OWNED BY public.company.company_pk;

CREATE SEQUENCE public.plane_plane_pk_seq;

CREATE TABLE public.plane (
                plane_pk INTEGER NOT NULL DEFAULT nextval('public.plane_plane_pk_seq'),
                model VARCHAR NOT NULL,
                capacity REAL NOT NULL,
                passengers_number INTEGER NOT NULL,
                CONSTRAINT plane_pk PRIMARY KEY (plane_pk)
);


ALTER SEQUENCE public.plane_plane_pk_seq OWNED BY public.plane.plane_pk;

CREATE SEQUENCE public.plane_unit_unit_pk_seq;

CREATE TABLE public.plane_unit (
                unit_pk INTEGER NOT NULL DEFAULT nextval('public.plane_unit_unit_pk_seq'),
                plane_pk INTEGER NOT NULL,
                company_pk INTEGER NOT NULL,
                CONSTRAINT unit_pk PRIMARY KEY (unit_pk, plane_pk, company_pk)
);


ALTER SEQUENCE public.plane_unit_unit_pk_seq OWNED BY public.plane_unit.unit_pk;

ALTER TABLE public.registration_code ADD CONSTRAINT admin_registration_code_fk
FOREIGN KEY (admin_pk)
REFERENCES public.admin (admin_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.flight ADD CONSTRAINT airport_flight_fk
FOREIGN KEY (departure_airport_pk)
REFERENCES public.airport (airport_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.flight ADD CONSTRAINT airport_flight_fk1
FOREIGN KEY (arrival_airport_pk)
REFERENCES public.airport (airport_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.plane_unit ADD CONSTRAINT company_plane_unit_fk
FOREIGN KEY (company_pk)
REFERENCES public.company (company_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.plane_unit ADD CONSTRAINT plane_plane_unit_fk
FOREIGN KEY (plane_pk)
REFERENCES public.plane (plane_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
