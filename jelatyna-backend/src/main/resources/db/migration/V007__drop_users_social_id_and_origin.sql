-- Logins are the only record of how a User signs in (ADR-0003); V003 copied every social_id into one.
-- Once this runs, an image that still maps these columns no longer starts against the database.
-- No IF EXISTS: a missing column must fail the migration.
ALTER TABLE public.users DROP COLUMN social_id;
ALTER TABLE public.users DROP COLUMN origin;
