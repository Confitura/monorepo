# Brand rules

Confitura is Warsaw's JVM community conference: a non-profit run by Java developers, architects and industry professionals since 2007. The brand is a jar of jam (*konfitura* is Polish for fruit preserves) drawn as a lowercase **c** under a square lid, next to a lowercase *confitura* wordmark. On screen it is three colours and one typeface: Confitura red, black and white, set in Neuzeit Grotesk, with square corners and big lowercase headings. The rules below follow the official logo pack and the live sites 2023.confitura.pl, 2025.confitura.pl and confitura.pl.

## Voice and copy

Confitura talks like the community that runs it: friendly, direct, a little playful, never corporate. The line every site repeats is the brief: *"Our main goal is to promote JVM technologies and have fun doing so."*

- **Section headings are lowercase**, like the wordmark: "about us", "our partners", "stay connected", "confitura'25 in numbers". So are header links ("schedule", "speakers") and tab labels ("gold", "silver").
- Hero lines and buttons use sentence case or caps for emphasis: "Thank you! And see you next time!", "Get ticket". Each edition's theme line goes in the hero too, for example 2023's "It is not rocket science!".
- In running text write **Confitura** with a capital C. Shorten an edition as **confitura'25** in lowercase headings and **Confitura'25** in sentences.
- Write to the reader as "you" and about the organisers as "we". Keep it short; the tickets page opens with a **TL;DR**.
- Warmth is welcome. "We will be happy to start a new friendship" and an occasional 🎉 in announcements are on-brand. Keep emoji out of headings, buttons and labels.
- Confitura is bilingual. Talks run in Polish or English; mark each one `PL` or `EN`. UI copy is in English unless the whole page is Polish.
- Write the email as `confitura@confitura.pl`. The sites obfuscate it as `confitura[at]confitura[dot]pl` in body text.
- Dates: `25–26.09.2026` in compact UI, "September 25–26, 2026" or "25–26 September 2026" in English sentences, "25–26 września 2026" in Polish. Use an en dash for every range. The hero writes the place first: "Warsaw | September 25–26, 2026".
- Times are 24-hour with an en dash: `09:30–10:15`, set in `time`.

## Logo

Use the files in [`assets/logos/`](assets/logos/); never redraw the jar, retype the wordmark or recolour it.

- On white: `confitura-logo.svg` (black wordmark, red jar).
- On `black` (the header, hero and footer bands): `confitura-logo-reversed.svg` (white wordmark, red jar).
- On a `jam` fill or a dark photo: `confitura-logo-white.svg`. Never put the red jar on red.
- The jar alone (`confitura-jar.svg`) is for avatars, favicons and stickers, and it appears inside the pictograms. The 2023 site's header shrank to just the jar on scroll. Use `confitura-jar-white.svg` on `jam`.
- **Edition lockup:** the 2023 site set the year after the wordmark in the same bold face, `confitura'23`. Build it from the logo plus `display` type; don't redraw the wordmark.
- **Co-branding:** 2025 paired the logo with a partner event as `confitura × AI & NLP day`. Put an × between the two marks at equal height.
- **Clear space** is the height of the lid on every side. The SVGs already include it.
- **Minimum size:** full logo 24px tall on screen and 10mm in print; the jar alone 16px.
- For print, use the CMYK PDFs in [`assets/print/`](assets/print/) (jam 0/93/79/0, black K100).

## Colour

Confitura red, black and white carry everything, in big full-width bands. confitura.pl stacks black (hero, news, contact), red (the numbers band) and white (about, partners).

- **`jam` (#EE1F46) is the red of every Confitura site since 2023.** Use it for section headings, header links on black, active tabs, the numbers band and primary buttons. In the webpage it is `$brand` in `webpage/assets/_colors.scss`.
- **The red inside the logo files is different** (`jam-logo`, #FF0040), and the site's own header logo (`webpage/assets/logo_black.svg`, `logo_white.svg`) draws its jar in #ED363D. Leave logos as supplied and use `jam` for everything else. Settling on one screen red is a decision for the brand owner.
- **White on jam is 4.3:1.** That passes for large text only, so white text on a red fill must be 19px bold or 24px and up. Buttons use 20px bold labels for this reason, and the numbers band uses 64px figures with 20px bold labels.
- Red text smaller than 24px on white uses `jam-ink`. `jam` on black passes at any size (4.9:1).
- Hover on a red fill is `jam-press` (#CB0F33, from the 2023 site), which also raises contrast to 5.7:1.
- Text is `ink`: pure black on white and pure white on black, as on the site. Secondary text is `ink-muted`.
- `line` is the site's light grey (#EAEAEA) for borders and inactive tab bars. Never set text in it; the site's grey tab labels are unreadable at 1.2:1, so this system draws inactive labels in `ink-muted`.
- Errors use `jam-ink` plus a word ("Required", "Sold out"). The brand red is also the error red, so an error never relies on colour alone.

### Programme flavours

The live sites don't need colours beyond red, black and white, but a schedule with parallel rooms does. Four fruit colours are an **intentional addition**: `plum`, `apricot`, `gooseberry` and `blueberry`, each with a `-soft` tint.

- Use them only to tell rooms or tracks apart: the lid on a SessionCard and the square marker in a Tag. Always print the room name next to the colour.
- Flavours are fills, never text; text on a `-soft` tint is `ink`.
- `gooseberry` also means "Available" and `apricot` "Few left" on tickets, always with the word.

## Type

- **Neuzeit Grotesk** is the brand face. The wordmark is set in Neuzeit Grotesk Bold, and every Confitura site since 2023 loads it as two webfonts: `NeuzeitGro-Bol` (headings, numbers, strong text) and `NeuzeitGro-Reg` (everything else). It is a licensed Linotype font: the webpage loads it from `webpage/assets/fonts/` (`@font-face` in `webpage/assets/styles.scss`), so check the licence before copying those files anywhere else. The `display` and `sans` stacks name it first.
- **Outfit** ([`fonts/`](fonts/), SIL OFL) is the open fallback. It renders when Neuzeit isn't loaded, for example in the design-system artifact's previews, and has full Polish diacritics.
- **JetBrains Mono** (`mono`, [`fonts/`](fonts/), OFL) is for times, dates, room and language codes and code samples. It's an addition; the sites have no mono text.
- Big and tight, like the site: `h1` 48px bold, `lead` 28px, `display` 64px, all at line-height 1.15. Long running text (`body`, 18px) opens up to 1.45 for reading.
- Headings are bold Neuzeit in `jam` on white or black, and `white` on the jam band.
- Numbers in a row (stats, prices, times) use tabular figures.

## Shape: square, with a jar

- **Everything is square** (`radius-none`). The live sites have no rounded corners, no shadows and no gradients. Separate things with full-bleed colour bands, space and 1px `line` borders.
- **The lid motif** comes from the mark: a square bar `lid-height` thick, `lid-ratio` (69%) of the width of what it sits on, centred, with `lid-gap` (half its height) between them. It sits above section titles (`lid-width` there) and above cards, where it carries the room's flavour. The site's tab bars are the same square bars, drawn underneath.
- **The jar's rounding** appears only in the logo, the cover illustration (`radius-jar`) and the speaker photo frame, which uses the mark's exact proportions. Don't round UI.
- Don't draw the **c** cut into shapes; that belongs to the logo.

## Layout and spacing

- Build pages from full-width bands: black hero with the edition visual, white sections, the red numbers band, black contact footer. On desktop each band is roughly a screen tall.
- Keep content within `content-max` (1440px, `$max-width` in `webpage/assets/_sizes.scss`), with at least a `space-4` gutter on phones.
- Spacing follows a 4px base: `space-1` to `space-24`. Pad cards with `space-6`; pad bands with `space-24` on desktop and `space-12` on phones.
- Lay out cards in auto-fill grids (200px minimum) so a row never ends with one card on its own. Cards in a row stretch to the same height, and their buttons line up at the bottom.
- Social links (X, Facebook, LinkedIn, YouTube, newsletter) sit in a narrow vertical strip on the right edge on desktop.

## Imagery and pictograms

- **Each edition has its own hero visual on black.** 2023 was a space theme: an astronaut wearing a Confitura patch, between red striped planets ("It is not rocket science!"; `webpage/assets/astronaut.svg`, `rocket.svg`, `small_planet.svg`). 2025–2026 used red and green binary rain (`webpage/assets/confitura tlo 2.png`). Keep illustrations to red, white and black, with one accent at most.
- **Pictograms** are the site's own stat icons in `webpage/assets/`: `sessions.svg` (stacked slides with the jar), `presentations.svg` (a presenter at a screen with the jar), `speakers.svg`, `views.svg`, `videos.svg` and `participants.svg`. They are white filled-outline drawings with square ends, made for the red band; use them about 96px tall above a number. `sessions.svg` has red fills behind its outlines, so it only works on `jam`.
- Use real photos and stills from the YouTube channel's talk recordings for speakers and wrap-ups; no stock photos.
- Partner logos come from partners and sit in square slots. Ask for a version that reads on black.

## Iconography

The sites use **Font Awesome** (version 6 on confitura.pl) for social and UI icons: X, Facebook, LinkedIn, YouTube and an envelope for the newsletter. Use Font Awesome solid and brand icons, in `ink` or `white` on black and `jam` for active states. No emoji as icons.

## States and motion

- Hover: red fills go to `jam-press`; red links on black turn `white`; outlined controls take an `ink` border; text links get a 2px `jam` underline.
- Current page: white link text with a 2px white underline in the black header, as on the 2023 site.
- Focus: a 2px solid `focus` ring with a 2px offset on light grounds, and `white` on black bands.
- Disabled: `line` fill with an `ink-muted` label, plus a word ("Sold out") when the reason matters.
- Motion is limited to colour transitions of 150ms or less, and everything is still under `prefers-reduced-motion`.

## Components

See [components.md](components.md) for the eleven components and what each one needs.
