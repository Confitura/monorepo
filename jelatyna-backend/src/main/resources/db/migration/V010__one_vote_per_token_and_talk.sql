-- A voter (token) has one vote per talk. Two vote-for-papers starts at once used to create a full set
-- each, so of every duplicate set one row stays: a rated one (the latest vote) if any, otherwise the
-- one earliest in the voter's ballot.
DELETE FROM public.vote WHERE id IN (
    SELECT id FROM (
        SELECT id, row_number() OVER (PARTITION BY token, presentation_id
            ORDER BY rate IS NULL, vote_date DESC NULLS LAST, vote_order, id) AS kept_rank
        FROM public.vote
    ) ranked
    WHERE kept_rank > 1
);

CREATE UNIQUE INDEX vote_token_presentation_id_key ON public.vote (token, presentation_id);
