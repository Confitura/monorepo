# Components

Eleven components for conference pages. The reference implementation is React 18 in [`components/bundle.js`](components/bundle.js) (`window.Confitura`), styled by [`components/bundle.css`](components/bundle.css) with props in [`components/index.d.ts`](components/index.d.ts). The webpage and admin app are Vue, so treat these as the spec for markup, spacing and states rather than code to import: the CSS uses only the custom properties from [`tokens.css`](tokens.css), and class names start with `cf-`.


## SiteHeader

The black top bar from confitura.pl: reversed logo, lowercase red links, the edition dates and the ticket button. It stays black in both themes.

- **Provide:** `logo` (an `<img>` of `confitura-logo-reversed.svg`); `links` (`label`, `href`, `current`) with lowercase labels; `date` (shown, e.g. "25–26.09.2026"); `dateTime`; and `cta` (`label`, `href`).
- Links are `jam` on black (4.9:1); the current link turns white with a 2px white underline, as on the 2023 site.
- Keep it to about five links. On phones, collapse them behind a red lowercase "menu" link, as confitura.pl does.

## SectionHeading

A section title the way confitura.pl sets it: 48px bold, lowercase, in jam, with the square lid above it and an optional lead.

- **Provide:** `title` (write it in lowercase: "about us", "our partners"); optionally `lead` (28px intro), `eyebrow`, `level` (default 2) and `align`.
- On the jam band set the title in `white` instead. StatGrid does this for you.
- Keep titles short; the site's are two or three words.

## Button

Use a button for the one action a section is for: `primary` (jam fill, white label) for "Get ticket", `secondary` (ink fill) for supporting actions, `ghost` for low-weight links.

- **Provide:** the label as children; `variant` (`primary`, `secondary` or `ghost`; default `primary`); `size` (`md` 48px or `lg` 60px); `href` to render a link. Other props pass through to the `<button>`.
- Labels are 20px bold (24px at `lg`). White on jam is only 4.3:1, so don't shrink the label below 19px bold.
- Square corners, like everything on confitura.pl.
- Use one `primary` per view. Labels start with a verb: "Get ticket", "Leave feedback". In the black header a lowercase "tickets" matches the nav.
- Disable with `disabled` (or `aria-disabled` on links) and say why in the label when it matters: "Sold out".

## Tabs

Lowercase labels over square bars, from the partner tiers on confitura.pl. Use it to switch partner tiers, schedule days or ticket types.

- **Provide:** `items` (`label`, optional `value`); `label` for screen readers; and either `defaultSelected` or a controlled `selected` with `onSelect`.
- The active tab has a jam bar and a `jam-ink` bold label; the others have a `line` bar and an `ink-muted` label. (The site draws inactive labels in #EAEAEA, which can't be read, so this system doesn't.)
- Tabs share the width equally. Keep labels to one word: "gold", "friday".
- Render the panel yourself under the tabs.

## Tag

A short label for a room, track, session type, language or ticket status.

- **Provide:** the text as children; `tone` (`neutral` or a flavour: `jam`, `plum`, `apricot`, `gooseberry`, `blueberry`); `variant` (`soft` or `outline`); optionally `marker` and `title`.
- A flavour tone shows the square lid marker in the flavour colour on its `-soft` tint, with `ink` text. The word carries the meaning; the colour only helps.
- Use `variant="outline"` for language codes, `PL` and `EN`, with `title="In Polish"` or `title="In English"`.
- Keep tags to one or two words. Don't use them as buttons.

## SessionCard

A talk, workshop, keynote or panel in the schedule, shaped as a jar whose lid carries the room's flavour.

- **Provide:** `start` and `end` (24-hour), `title`, `room`, `tone` (the room's flavour, default `jam`), `speakers` (`name`, `company`), `lang` (`PL` or `EN`), `kind` (`talk`, `workshop`, `keynote` or `panel`), and optionally `level` and `href`.
- Keep each room's flavour the same all day and print a legend. The room name always shows next to its colour.
- Write the title in the talk's own language and leave it untranslated.
- Lay cards out in a grid; cards in a row stretch to equal height, so tags line up at the bottom.
- Keynotes get a larger title. Don't add other emphasis.

## SpeakerCard

A speaker's portrait in a jar-shaped frame, with name, company and talk.

- **Provide:** `name`; optionally `company`, `talk`, `photo` (a URL, cropped to cover), `initials` and `href`.
- Without a photo the frame shows initials in `jam-ink` on `jam-soft`. Use that rather than a grey silhouette.
- Use portraits with the face centred and some space above the head; the jar's rounded corners crop the edges.
- The frame is the jar's own proportions, lid included. Don't add borders or shadows to it.

## TicketCard

One ticket type with what it includes, what it leaves out, its availability and a buy button.

- **Provide:** `name`, `duration` ("2 days"), `includes` and `excludes` (lists of short phrases), `status` (`available`, `few-left` or `sold-out`), and optionally `price`, `note`, `featured` and `cta` (`label`, `href`).
- Mark the ticket most people should buy as `featured`; it gets the jam lid, a jam border and the primary button. Only one per row.
- List exclusions plainly, e.g. "Lunch". They render muted with an empty marker and a "Not included" label for screen readers.
- A sold-out ticket drops its button and keeps the card, so people can see what they missed.
- Put conditions like "Bring a valid student ID." in `note`.

## Callout

A boxed note for the one condition people must not miss, such as the lunch rule on the tickets page.

- **Provide:** the message as children (paragraphs); optionally `title` (default "Note!") and `tone` (`jam` for warnings, `neutral` for helpful asides).
- One per section at most. If everything is a note, nothing is.
- Keep the title to a few words; it renders in caps.

## StatGrid

The red numbers band from confitura.pl: a lowercase white title, an optional lead, then white pictograms over big figures with bold labels.

- **Provide:** `items` (`value`, `label`, optional `icon` URL of a pictogram from `webpage/assets/`); optionally `title` ("confitura'25 in numbers"), `lead` and `variant` (`band`, the default, or `plain` on white).
- Use the matching pictogram for each figure, as confitura.pl does: `sessions.svg` for presentations, `speakers.svg`, `presentations.svg` for workshops, `views.svg`, `videos.svg`, `participants.svg`.
- Values are strings, so "1 mln +" and "386" work. Labels are lowercase: "youtube videos".
- Run it full-width; use three or six items so rows fill evenly.
- The band is white on jam at 64px and 20px bold, the smallest sizes that pass contrast there.

## PartnerTier

One sponsorship tier with a heading and a grid of logo slots.

- **Provide:** `tier` (Platinum, Gold, Silver, Bronze, Tech or Media), `partners` (`name`, `logo` URL, `href`), and `size` (`lg` for Platinum, `md` for Gold, `sm` for the rest).
- Logos sit at up to 80% width and 60% height of a 2:1 slot on `surface-raised`. Ask partners for a version that reads on the dark theme.
- Without a logo, the slot shows the partner's name in `ink-muted`.
- List tiers from highest to lowest, each in its own PartnerTier.
