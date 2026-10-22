-- A News Entry may have a cover image: the address the resources upload stored it at, or null.
ALTER TABLE public.news_entry ADD COLUMN cover text;
