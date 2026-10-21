-- News Entries move from Listmonk campaigns (those tagged "webpage", whose dates could not be
-- changed) into Jelatyna. An entry is public once it is published and its date has passed.

CREATE TABLE public.news_entry (
    id character varying(100) NOT NULL PRIMARY KEY,
    title text NOT NULL,
    slug text NOT NULL,
    body text NOT NULL,
    date timestamp with time zone NOT NULL,
    published boolean NOT NULL,
    CONSTRAINT news_entry_slug_key UNIQUE (slug)
);

-- Every entry of the public feed on 2026-10-21 (api.confitura.pl/api/resources/edition-2026/news.json),
-- oldest first, published. Title, body (HTML, which Markdown passes through) and date are verbatim;
-- the bodies are copied byte for byte, non-breaking spaces included. Slugs follow the slug rule:
-- lowercase, diacritics folded to ASCII, apostrophes (' and ’) dropped, every other run of
-- non-alphanumerics one hyphen, no leading or trailing hyphen; on a clash the oldest entry keeps the
-- bare slug and later ones get -2, -3, ...
INSERT INTO public.news_entry (id, title, slug, body, date, published) VALUES
(gen_random_uuid()::text, 'Tickets are here!', 'tickets-are-here',
$body$<p><strong>This Wednesday brings good news: ticket sales for Confitura 2025 are now open!</strong></p>
<p>The conference is just one month away. Two days of software talks and a workshop are available for 555 PLN gross. Workshop participation will be limited to around 280 people, so we recommend securing your place early. Each ticket includes access to one workshop, which you will choose once the agenda is officially announced - no decisions are needed at the time of purchase.</p>
<p>We are preparing Confitura for about 800 participants. Each order allows you to purchase up to five tickets, making it easier for teams to register together. If you plan to attend, consider asking your employer to cover the cost, this year, tickets are sold via a professional platform with invoicing available, so using your training budget is simple.</p>
<p>If you agree to receive marketing communications when purchasing your ticket, we’ll send you a Confiturian’s Manual before the conference. Otherwise, you’ll have to figure out where the keynote takes place or how to get a drink all by yourself (or worse- ask others, lol).</p>
<p>Here’s the magic link to get your ticket:</p>
<p> </p>
<p><span style="font-size: 36px;"><a href="https://www.ebilet.pl/biznes/konferencje/confitura-25?partner=bcc">Confitura 2025 Tickets</a><br /></span></p>
<p> </p>
<p>Your conference <strong>ticket number</strong> from eBilet is also your <strong>discount code</strong>. It gives you 10% off Sages’ Java &amp; AI trainings (valid for one or multiple orders, including several participants).</p>
<p>The code will be active after the event, valid until the end of 2025, and cannot be combined with other offers. Feel free to share it at work!</p>
<p> </p>$body$,
'2025-08-20T19:12:42.265697Z', true),
(gen_random_uuid()::text, 'Tickets', 'tickets',
$body$<h1 id="the-final-countdown-to-confitura-has-begun">The Final Countdown to Confitura Has Begun!</h1>
<p>The clock is ticking - <strong> Confitura is almost here</strong>! If you are still hesitating, it’s time to decide. <a href="https://2025.confitura.pl/schedule">Our agenda, designed by the software community itself</a>, is packed with golden talks and must-attend workshops. Two full days of knowledge, inspiration, and a friendly, one-of-a-kind vibe - an experience you simply cannot replace.</p>
<p>Not sure about the budget? Ask your employer to cover the ticket from <a href="https://www.biznes.gov.pl/en/portal/004070">the corporate training budget</a> - we’ll gladly provide a written recommendation to support your case. Flexible options are available:</p>
<ul>
<li>1-day ticket for those commuting in and out of Warsaw the same day - choose Friday or Saturday.</li>
<li>Discounts for university students and active community contributors (OSS or community initiatives).</li>
</ul>
<p>And remember: every ticket unlocks access to our workshops! Use your ticket number (below the barcode) to reserve your spot today. Don’t wait until the very last moment - secure your place now.</p>
<p>Join us before it’s too late. Confitura awaits!</p>$body$,
'2025-09-12T08:11:12.426497Z', true),
(gen_random_uuid()::text, 'Confitura 2025 wrap up', 'confitura-2025-wrap-up',
$body$<p data-start="118" data-end="370">Confitura 2025 has gone down in history! 🎉 We welcomed over <strong data-start="179" data-end="196">450 attendees</strong> and <strong data-start="201" data-end="216">60 speakers</strong> for two inspiring days filled with knowledge, ideas, talks, and workshops. Thank you for being part of this very special <strong data-start="338" data-end="354">15th edition</strong> of Confitura.</p>
<p data-start="372" data-end="481">Preparations for <strong data-start="389" data-end="407">Confitura 2026</strong> are already underway - official dates will be announced soon. Stay tuned!</p>$body$,
'2025-09-22T08:19:08.027221Z', true),
(gen_random_uuid()::text, 'v4p', 'v4p',
$body$<h1 id="c4p-is-closed-long-live-the-4vp">C4P is closed, long live the V4P</h1>
<p class="isSelectedEnd">We've got the talks. Now it's your turn.</p>
<p class="isSelectedEnd">As every year, the Confitura community decides what makes it onto the stage. Browse the submissions, vote for your favourites, and help us build the agenda.</p>
<p>No pressure. The future of the conference is entirely in your hands. <a href="https://app.confitura.pl/vote-for-papers">VOTE 4 PAPERS</a> until July 1st.<br /><br /></p>
<p class="PDq2pG_selectionAnchorContainer" data-start="0" data-end="11"><strong data-start="0" data-end="11">TICKETS</strong></p>
<p data-start="13" data-end="134" data-is-last-node="" data-is-only-node="">Tickets for the conference will go on sale at the beginning of July, opening the path from anticipation to participation.</p>
<p><br /><br /><br /></p>$body$,
'2026-01-29T17:17:46.359407Z', true),
(gen_random_uuid()::text, 'agenda&tickets', 'agenda-tickets',
$body$<h1 id="c4p-is-closed-long-live-the-4vp">TICKETS</h1>
<p class="isSelectedEnd">Tickets for the conference will be available at the end of July.</p>
<p>While you wait, follow our <a href="https://confitura.pl/speakers">speaker lineup updates</a>, we’re busy behind the scenes shaping this year’s agenda and making sure your favorite topics and speakers find their place on the Confitura stage.</p>
<p><br /><br /><br /></p>$body$,
'2026-07-24T16:06:00.385886Z', true),
(gen_random_uuid()::text, 'TICKETS', 'tickets-2',
$body$<p>TICKETS ARE <a href="https://confitura.pl/tickets">HERE!</a></p>$body$,
'2026-08-03T09:50:32.090732Z', true),
(gen_random_uuid()::text, 'We’re in the home stretch!', 'were-in-the-home-stretch',
$body$<p>Remember to sign up for the workshops (<a href="https://confitura.pl/workshop-signup" target="_blank" rel="noopener">instructions here</a>).<br />If you don’t have tickets yet, they are still available <a href="https://www.ebilet.pl/en/biznes/konferencje/confitura?partner=bcc" target="_blank" rel="noopener">here</a>.</p>
<p>See you in a few days!<br />Don’t forget to say hello to our <a href="https://confitura.pl/partners">partners</a> while you're there.</p>$body$,
'2026-09-22T18:30:04.579805Z', true),
(gen_random_uuid()::text, 'Thank You!', 'thank-you',
$body$<p>This event wouldn’t be possible without the amazing people who choose to spend their time with us. Thank you for being part of it!</p>
<p>Have feedback for our speakers? You can share it with them directly through the <a href="https://confitura.pl/schedule">schedule page</a>. <br /><br />For our feedback you can <a href="https://forms.gle/vKPKvhne1D7R2yri9">fill a form</a>, or drop as an email :)</p>
<p> </p>
<p> </p>$body$,
'2026-09-27T10:27:57.836693Z', true);
