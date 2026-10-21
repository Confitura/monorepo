-- A User owns zero or more Logins: a provider plus that provider's id for the person (ADR-0003).
-- users.social_id and users.origin stay until a later migration drops them, so the previous image
-- can still be redeployed.

CREATE TABLE public.login (
    id character varying(100) NOT NULL PRIMARY KEY,
    user_id character varying(255) NOT NULL REFERENCES public.users (id),
    provider text NOT NULL,
    external_id text NOT NULL,
    created_date timestamp without time zone,
    CONSTRAINT login_provider_external_id_key UNIQUE (provider, external_id)
);

-- One Login per users.social_id, which is base64 of "<provider>/<id>" (Twitter's id is the username).
-- The Login is as old as its User, the only record of when that sign-in first happened.
-- A blank social_id names no sign-in, so its User stays a Manual User (no Login).
-- No dedup: two Users sharing a social_id fail the unique key and so the whole migration.
INSERT INTO public.login (id, user_id, provider, external_id, created_date)
SELECT gen_random_uuid()::text,
       id,
       split_part(decoded, '/', 1),
       substr(decoded, strpos(decoded, '/') + 1),
       created_date
FROM (SELECT id, created_date, convert_from(decode(social_id, 'base64'), 'UTF8') AS decoded
      FROM public.users
      WHERE social_id IS NOT NULL AND social_id <> '') AS users_with_social_id;
