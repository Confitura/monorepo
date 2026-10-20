-- Drops what V001 copied from prod but no entity maps and no native SQL reads (ADR-0002).
-- No IF EXISTS and no CASCADE: a missing object or an unknown dependant must fail the migration.

-- presentation_ratings references rate, so it goes first. Each table's own keys go with it.
DROP TABLE public.presentation_ratings;
DROP TABLE public.rate;

DROP TABLE public.users_personal_agenda;
DROP TABLE public.users_performed_rate;

DROP TABLE public.admin;
DROP TABLE public.news;
DROP TABLE public.tag;
DROP TABLE public.volunteer;

-- Free-text category, superseded by category_id (ADR-0001).
ALTER TABLE public.faq_entry DROP COLUMN category;
