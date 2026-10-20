-- Baseline: schema-only pg_dump of prod (PostgreSQL 16.10), Coolify backup of 2026-10-20 01:00.
-- Generated with pg_restore --schema-only --no-owner --no-privileges --no-comments; the psql-only
-- header (\restrict, SET ..., search_path reset) is stripped so Flyway can run it. See docs/adr/0002.

--
-- Name: admin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admin (
    id bigint NOT NULL,
    bio character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    mail character varying(255),
    twitter character varying(255),
    webpage character varying(255),
    filename character varying(255)
);

--
-- Name: agenda; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.agenda (
    id character varying(100) NOT NULL,
    label character varying(255),
    presentation_id character varying(100),
    room_id character varying(100),
    time_slot_day_id character varying(255) NOT NULL,
    time_slot_display_order integer NOT NULL
);

--
-- Name: agenda_day; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.agenda_day (
    id character varying(100) NOT NULL,
    created_by character varying(255),
    created_date timestamp(6) without time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp(6) without time zone,
    date date NOT NULL,
    display_order integer NOT NULL,
    label character varying(255) NOT NULL
);

--
-- Name: agenda_room; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.agenda_room (
    id character varying(100) NOT NULL,
    created_by character varying(255),
    created_date timestamp(6) without time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp(6) without time zone,
    display_order integer NOT NULL,
    label character varying(255),
    day_id character varying(100) NOT NULL
);

--
-- Name: agenda_time_slot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.agenda_time_slot (
    day_id character varying(255) NOT NULL,
    display_order integer NOT NULL,
    created_by character varying(255),
    created_date timestamp(6) without time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp(6) without time zone,
    end_time time(0) without time zone,
    for_all_rooms boolean NOT NULL,
    start_time time(0) without time zone
);

--
-- Name: allegro_voucher_message; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.allegro_voucher_message (
    id character varying(255) NOT NULL,
    allegro_message character varying(10000),
    buyer_login character varying(255),
    checkout_form_id character varying(255),
    send_date timestamp without time zone
);

--
-- Name: available_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.available_tags (
    id character varying(255) NOT NULL,
    name character varying(255)
);

--
-- Name: demographic_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.demographic_data (
    id character varying(255) NOT NULL,
    city character varying(255),
    experience character varying(255),
    meal_option character varying(255),
    role character varying(255)
);

--
-- Name: demographic_data_technologies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.demographic_data_technologies (
    demographic_data_id character varying(255) NOT NULL,
    technologies character varying(255)
);

--
-- Name: faq_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.faq_category (
    id character varying(255) NOT NULL,
    created_by character varying(255),
    created_date timestamp(6) with time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp(6) with time zone,
    display_order integer NOT NULL,
    name character varying(255),
    published boolean NOT NULL
);

--
-- Name: faq_entry; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.faq_entry (
    id character varying(255) NOT NULL,
    created_by character varying(255),
    created_date timestamp(6) with time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp(6) with time zone,
    answer text,
    category character varying(255),
    display_order integer NOT NULL,
    published boolean NOT NULL,
    question character varying(255),
    category_id character varying(255)
);

--
-- Name: news; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.news (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    description character varying(255),
    published boolean NOT NULL,
    shortdescription character varying(255),
    title character varying(255)
);

--
-- Name: participation_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.participation_data (
    id character varying(100) NOT NULL,
    created_by character varying(255),
    created_date timestamp without time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp without time zone,
    arrival_date timestamp without time zone,
    email character varying(255),
    first_name character varying(255),
    gender character varying(255),
    info character varying(255),
    last_name character varying(255),
    privacy_policy_accepted boolean NOT NULL,
    registered_by character varying(255),
    size character varying(255),
    survey_send_date timestamp without time zone,
    ticket_send_date timestamp without time zone,
    voucher_id character varying(255) NOT NULL,
    registration_date timestamp(6) without time zone
);

--
-- Name: partner; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.partner (
    id character varying(100) NOT NULL,
    description character varying(10000),
    logo character varying(255),
    name character varying(255),
    published boolean NOT NULL,
    type character varying(255),
    www character varying(255),
    orientation character varying(255),
    slug character varying(255)
);

--
-- Name: presentation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presentation (
    id character varying(100) NOT NULL,
    description text,
    language character varying(255),
    level character varying(255),
    short_description text,
    status character varying(255),
    title character varying(255),
    workshop boolean NOT NULL,
    duration_in_minutes integer,
    expected_price double precision,
    is_free boolean,
    max_group_size integer,
    pre_selection_status character varying(255),
    pre_selection_comment character varying(255),
    rating_enabled boolean DEFAULT true NOT NULL
);

--
-- Name: presentation_like; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presentation_like (
    id character varying(100) NOT NULL,
    token character varying(255) NOT NULL,
    presentation_id character varying(100)
);

--
-- Name: presentation_rate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presentation_rate (
    id character varying(100) NOT NULL,
    comment character varying(255),
    value character varying(255),
    presentation_id character varying(100),
    reviewer_token character varying(255)
);

--
-- Name: presentation_ratings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presentation_ratings (
    presentation_id character varying(100) NOT NULL,
    ratings_id character varying(100) NOT NULL
);

--
-- Name: presentation_speakers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presentation_speakers (
    presentations_id character varying(100) NOT NULL,
    speakers_id character varying(255) NOT NULL
);

--
-- Name: presentation_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presentation_tags (
    presentation_id character varying(100) NOT NULL,
    tags_id character varying(255) NOT NULL
);

--
-- Name: rate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rate (
    id character varying(100) NOT NULL,
    comment character varying(255),
    value character varying(255)
);

--
-- Name: simple_content; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.simple_content (
    title character varying(255) NOT NULL,
    content text
);

--
-- Name: tag; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tag (
    id character varying(255) NOT NULL,
    name character varying(255)
);

--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id character varying(255) NOT NULL,
    created_by character varying(255),
    created_date timestamp without time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp without time zone,
    bio text,
    email text,
    github text,
    is_admin boolean NOT NULL,
    is_volunteer boolean NOT NULL,
    name text,
    origin text,
    photo text,
    privacy_policy_accepted boolean,
    social_id text,
    twitter text,
    username text,
    www text,
    participation_data_id character varying(100)
);

--
-- Name: users_performed_rate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users_performed_rate (
    id character varying(100) NOT NULL,
    reviewer_token character varying(255),
    presentation_id character varying(100)
);

--
-- Name: users_personal_agenda; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users_personal_agenda (
    user_id character varying(255) NOT NULL,
    personal_agenda_id character varying(100) NOT NULL
);

--
-- Name: volunteer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.volunteer (
    id bigint NOT NULL,
    bio character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    mail character varying(255),
    twitter character varying(255),
    webpage character varying(255)
);

--
-- Name: vote; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.vote (
    id character varying(100) NOT NULL,
    client character varying(255),
    vote_order integer,
    rate integer,
    token character varying(255),
    vote_date timestamp without time zone,
    presentation_id character varying(100),
    CONSTRAINT vote_rate_check CHECK (((rate >= '-1'::integer) AND (rate <= 1)))
);

--
-- Name: voucher; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.voucher (
    id character varying(255) NOT NULL,
    created_by character varying(255),
    created_date timestamp without time zone,
    last_modified_by character varying(255),
    last_modified_date timestamp without time zone,
    auction_id character varying(255),
    auction_name character varying(255),
    buyer_login character varying(255),
    comment character varying(255),
    original_buyer character varying(255),
    ticket_send_date timestamp without time zone,
    type character varying(255)
);

--
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (id);

--
-- Name: agenda_day agenda_day_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda_day
    ADD CONSTRAINT agenda_day_pkey PRIMARY KEY (id);

--
-- Name: agenda agenda_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda
    ADD CONSTRAINT agenda_pkey PRIMARY KEY (id);

--
-- Name: agenda_room agenda_room_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda_room
    ADD CONSTRAINT agenda_room_pkey PRIMARY KEY (id);

--
-- Name: agenda_time_slot agenda_time_slot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda_time_slot
    ADD CONSTRAINT agenda_time_slot_pkey PRIMARY KEY (day_id, display_order);

--
-- Name: allegro_voucher_message allegro_voucher_message_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.allegro_voucher_message
    ADD CONSTRAINT allegro_voucher_message_pkey PRIMARY KEY (id);

--
-- Name: available_tags available_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.available_tags
    ADD CONSTRAINT available_tags_pkey PRIMARY KEY (id);

--
-- Name: demographic_data demographic_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.demographic_data
    ADD CONSTRAINT demographic_data_pkey PRIMARY KEY (id);

--
-- Name: faq_category faq_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.faq_category
    ADD CONSTRAINT faq_category_pkey PRIMARY KEY (id);

--
-- Name: faq_entry faq_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.faq_entry
    ADD CONSTRAINT faq_entry_pkey PRIMARY KEY (id);

--
-- Name: news news_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news
    ADD CONSTRAINT news_pkey PRIMARY KEY (id);

--
-- Name: participation_data participation_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.participation_data
    ADD CONSTRAINT participation_data_pkey PRIMARY KEY (id);

--
-- Name: partner partner_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.partner
    ADD CONSTRAINT partner_pkey PRIMARY KEY (id);

--
-- Name: presentation_like presentation_like_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_like
    ADD CONSTRAINT presentation_like_pkey PRIMARY KEY (id);

--
-- Name: presentation presentation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation
    ADD CONSTRAINT presentation_pkey PRIMARY KEY (id);

--
-- Name: presentation_rate presentation_rate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_rate
    ADD CONSTRAINT presentation_rate_pkey PRIMARY KEY (id);

--
-- Name: presentation_ratings presentation_ratings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_ratings
    ADD CONSTRAINT presentation_ratings_pkey PRIMARY KEY (presentation_id, ratings_id);

--
-- Name: presentation_speakers presentation_speakers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_speakers
    ADD CONSTRAINT presentation_speakers_pkey PRIMARY KEY (presentations_id, speakers_id);

--
-- Name: presentation_tags presentation_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_tags
    ADD CONSTRAINT presentation_tags_pkey PRIMARY KEY (presentation_id, tags_id);

--
-- Name: rate rate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rate
    ADD CONSTRAINT rate_pkey PRIMARY KEY (id);

--
-- Name: simple_content simple_content_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.simple_content
    ADD CONSTRAINT simple_content_pkey PRIMARY KEY (title);

--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);

--
-- Name: presentation_ratings uk_kait65g5tv0fomt358nbx821x; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_ratings
    ADD CONSTRAINT uk_kait65g5tv0fomt358nbx821x UNIQUE (ratings_id);

--
-- Name: faq_category ukfn09klt00p3fb2ajtppyqjaxo; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.faq_category
    ADD CONSTRAINT ukfn09klt00p3fb2ajtppyqjaxo UNIQUE (name);

--
-- Name: demographic_data_technologies ukj042d06kfeg3ccgggl5nymxq4; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.demographic_data_technologies
    ADD CONSTRAINT ukj042d06kfeg3ccgggl5nymxq4 UNIQUE (demographic_data_id, technologies);

--
-- Name: presentation_like unique_like; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_like
    ADD CONSTRAINT unique_like UNIQUE (presentation_id, token);

--
-- Name: participation_data unique_voucher; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.participation_data
    ADD CONSTRAINT unique_voucher UNIQUE (voucher_id);

--
-- Name: users_performed_rate users_performed_rate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_performed_rate
    ADD CONSTRAINT users_performed_rate_pkey PRIMARY KEY (id);

--
-- Name: users_personal_agenda users_personal_agenda_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_personal_agenda
    ADD CONSTRAINT users_personal_agenda_pkey PRIMARY KEY (user_id, personal_agenda_id);

--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

--
-- Name: volunteer volunteer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volunteer
    ADD CONSTRAINT volunteer_pkey PRIMARY KEY (id);

--
-- Name: vote vote_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vote
    ADD CONSTRAINT vote_pkey PRIMARY KEY (id);

--
-- Name: voucher voucher_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.voucher
    ADD CONSTRAINT voucher_pkey PRIMARY KEY (id);

--
-- Name: presentation_tags fk2bvvcwm6vndgc7njvbmwjjodx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_tags
    ADD CONSTRAINT fk2bvvcwm6vndgc7njvbmwjjodx FOREIGN KEY (presentation_id) REFERENCES public.presentation(id);

--
-- Name: vote fk2ln6e2bed6b276oata023atqf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vote
    ADD CONSTRAINT fk2ln6e2bed6b276oata023atqf FOREIGN KEY (presentation_id) REFERENCES public.presentation(id);

--
-- Name: agenda fk7mtf51nuo85ar4pj3hdxbhgv6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda
    ADD CONSTRAINT fk7mtf51nuo85ar4pj3hdxbhgv6 FOREIGN KEY (room_id) REFERENCES public.agenda_room(id);

--
-- Name: presentation_ratings fk921l6fpk3nm4yphuhfe903rlf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_ratings
    ADD CONSTRAINT fk921l6fpk3nm4yphuhfe903rlf FOREIGN KEY (ratings_id) REFERENCES public.rate(id);

--
-- Name: presentation_rate fk9annyamkkq777du05ffs7avl4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_rate
    ADD CONSTRAINT fk9annyamkkq777du05ffs7avl4 FOREIGN KEY (presentation_id) REFERENCES public.presentation(id);

--
-- Name: users_personal_agenda fk9v3c3m3onq9c7dwlh126nog7s; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_personal_agenda
    ADD CONSTRAINT fk9v3c3m3onq9c7dwlh126nog7s FOREIGN KEY (personal_agenda_id) REFERENCES public.agenda(id);

--
-- Name: users_personal_agenda fkbamlc5bdw3tcjqcxhaa0g4lqh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_personal_agenda
    ADD CONSTRAINT fkbamlc5bdw3tcjqcxhaa0g4lqh FOREIGN KEY (user_id) REFERENCES public.users(id);

--
-- Name: agenda_room fkdwt33vpipdt9b3apy568l7pt5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda_room
    ADD CONSTRAINT fkdwt33vpipdt9b3apy568l7pt5 FOREIGN KEY (day_id) REFERENCES public.agenda_day(id);

--
-- Name: faq_entry fkge0arfiomkxb5qprkctyjxcqk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.faq_entry
    ADD CONSTRAINT fkge0arfiomkxb5qprkctyjxcqk FOREIGN KEY (category_id) REFERENCES public.faq_category(id);

--
-- Name: participation_data fkgp3k20faaesolacxaws10n7ew; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.participation_data
    ADD CONSTRAINT fkgp3k20faaesolacxaws10n7ew FOREIGN KEY (voucher_id) REFERENCES public.voucher(id);

--
-- Name: demographic_data_technologies fkk0i3wefilyc9tfxsgpascbfme; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.demographic_data_technologies
    ADD CONSTRAINT fkk0i3wefilyc9tfxsgpascbfme FOREIGN KEY (demographic_data_id) REFERENCES public.demographic_data(id);

--
-- Name: agenda fkljng2w79qeb1n6jmal7fm1eyc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda
    ADD CONSTRAINT fkljng2w79qeb1n6jmal7fm1eyc FOREIGN KEY (presentation_id) REFERENCES public.presentation(id);

--
-- Name: presentation_ratings fklxkd7qr9gd5ieopo8movgj38k; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_ratings
    ADD CONSTRAINT fklxkd7qr9gd5ieopo8movgj38k FOREIGN KEY (presentation_id) REFERENCES public.presentation(id);

--
-- Name: users fkngrdxhs8a5jsi029hlna1urdh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkngrdxhs8a5jsi029hlna1urdh FOREIGN KEY (participation_data_id) REFERENCES public.participation_data(id);

--
-- Name: presentation_speakers fkorb1dvnex3oo2bg4988kjpu96; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_speakers
    ADD CONSTRAINT fkorb1dvnex3oo2bg4988kjpu96 FOREIGN KEY (speakers_id) REFERENCES public.users(id);

--
-- Name: users_performed_rate fkp4fwje8g23mtkyv3mxsx83vi2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_performed_rate
    ADD CONSTRAINT fkp4fwje8g23mtkyv3mxsx83vi2 FOREIGN KEY (presentation_id) REFERENCES public.presentation(id);

--
-- Name: presentation_like fkqooxsoxl9j5kk4yc9cvlfiw04; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_like
    ADD CONSTRAINT fkqooxsoxl9j5kk4yc9cvlfiw04 FOREIGN KEY (presentation_id) REFERENCES public.presentation(id);

--
-- Name: agenda fkr2vaj0sl6sikis6nhdtui46h2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agenda
    ADD CONSTRAINT fkr2vaj0sl6sikis6nhdtui46h2 FOREIGN KEY (time_slot_day_id, time_slot_display_order) REFERENCES public.agenda_time_slot(day_id, display_order);

--
-- Name: presentation_speakers fkr49da3xsil1ktlv4sl9gagi5o; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_speakers
    ADD CONSTRAINT fkr49da3xsil1ktlv4sl9gagi5o FOREIGN KEY (presentations_id) REFERENCES public.presentation(id);

--
-- Name: presentation_tags fkt4swiylyqwbhu6e5rqu7tk5qj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presentation_tags
    ADD CONSTRAINT fkt4swiylyqwbhu6e5rqu7tk5qj FOREIGN KEY (tags_id) REFERENCES public.available_tags(id);

--
-- PostgreSQL database dump complete
--

