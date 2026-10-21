-- Talks, workshops and agenda Days belong to an Edition (ADR-0004). Rooms, time slots and agenda
-- entries hang off their Day, so they follow it. Every existing row is Edition 2026's: the only
-- Edition V005 created.

ALTER TABLE public.presentation ADD COLUMN edition_id character varying(100) REFERENCES public.edition (id);
UPDATE public.presentation SET edition_id = (SELECT id FROM public.edition WHERE year = 2026);
ALTER TABLE public.presentation ALTER COLUMN edition_id SET NOT NULL;

ALTER TABLE public.agenda_day ADD COLUMN edition_id character varying(100) REFERENCES public.edition (id);
UPDATE public.agenda_day SET edition_id = (SELECT id FROM public.edition WHERE year = 2026);
ALTER TABLE public.agenda_day ALTER COLUMN edition_id SET NOT NULL;
