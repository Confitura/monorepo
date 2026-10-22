-- A Day id is unique within its Edition, not globally (ADR-0004): the webpage's schedule pages
-- hardcode the Day ids, so every Edition needs the same ones. A Day is keyed by (edition_id, id),
-- and its rooms, time slots and agenda entries reference it by both. Existing rows keep their
-- values and gain their Day's Edition.

-- The keys that point at a Day id alone go first.
ALTER TABLE public.agenda_room DROP CONSTRAINT fkdwt33vpipdt9b3apy568l7pt5;
ALTER TABLE public.agenda DROP CONSTRAINT fkr2vaj0sl6sikis6nhdtui46h2;

ALTER TABLE public.agenda_day DROP CONSTRAINT agenda_day_pkey;
ALTER TABLE public.agenda_day ADD CONSTRAINT agenda_day_pkey PRIMARY KEY (edition_id, id);

ALTER TABLE public.agenda_room ADD COLUMN edition_id character varying(100);
UPDATE public.agenda_room room SET edition_id = day.edition_id FROM public.agenda_day day WHERE day.id = room.day_id;
ALTER TABLE public.agenda_room ALTER COLUMN edition_id SET NOT NULL;
ALTER TABLE public.agenda_room ADD CONSTRAINT agenda_room_day_fkey
    FOREIGN KEY (edition_id, day_id) REFERENCES public.agenda_day (edition_id, id);

-- Time slots never had a key to their Day, so one may outlive it; that one stays in the Current Edition.
ALTER TABLE public.agenda_time_slot ADD COLUMN edition_id character varying(100);
UPDATE public.agenda_time_slot slot SET edition_id = COALESCE(
    (SELECT day.edition_id FROM public.agenda_day day WHERE day.id = slot.day_id),
    (SELECT edition.id FROM public.edition edition WHERE edition.current));
ALTER TABLE public.agenda_time_slot ALTER COLUMN edition_id SET NOT NULL;
ALTER TABLE public.agenda_time_slot DROP CONSTRAINT agenda_time_slot_pkey;
ALTER TABLE public.agenda_time_slot ADD CONSTRAINT agenda_time_slot_pkey PRIMARY KEY (edition_id, day_id, display_order);

ALTER TABLE public.agenda ADD COLUMN time_slot_edition_id character varying(100);
UPDATE public.agenda entry SET time_slot_edition_id = slot.edition_id FROM public.agenda_time_slot slot
    WHERE slot.day_id = entry.time_slot_day_id AND slot.display_order = entry.time_slot_display_order;
ALTER TABLE public.agenda ALTER COLUMN time_slot_edition_id SET NOT NULL;
ALTER TABLE public.agenda ADD CONSTRAINT agenda_time_slot_fkey
    FOREIGN KEY (time_slot_edition_id, time_slot_day_id, time_slot_display_order)
    REFERENCES public.agenda_time_slot (edition_id, day_id, display_order);
