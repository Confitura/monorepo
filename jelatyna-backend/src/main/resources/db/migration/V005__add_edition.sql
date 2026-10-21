-- An Edition is one yearly Confitura conference; exactly one is the Current Edition (ADR-0004).
-- Its C4P period replaces the conference.c4p properties: unset means closed.

CREATE TABLE public.edition (
    id character varying(100) NOT NULL PRIMARY KEY,
    year integer NOT NULL,
    name text NOT NULL,
    conference_start timestamp with time zone NOT NULL,
    last_day date NOT NULL,
    c4p_start timestamp with time zone,
    c4p_end timestamp with time zone,
    current boolean NOT NULL,
    CONSTRAINT edition_year_key UNIQUE (year)
);

-- At most one Edition is current; this migration makes Edition 2026 that one.
CREATE UNIQUE INDEX edition_current_key ON public.edition (current) WHERE current;

-- The C4P period is prod's CONFERENCE_C4P_START/END on 2026-10-21.
INSERT INTO public.edition (id, year, name, conference_start, last_day, c4p_start, c4p_end, current)
VALUES (gen_random_uuid()::text, 2026, 'Confitura 2026',
        TIMESTAMP '2026-09-25 09:00:00' AT TIME ZONE 'Europe/Warsaw',
        DATE '2026-09-26',
        TIMESTAMP '2026-02-01 00:00:00' AT TIME ZONE 'Europe/Warsaw',
        TIMESTAMP '2026-08-06 23:59:59' AT TIME ZONE 'Europe/Warsaw',
        true);
