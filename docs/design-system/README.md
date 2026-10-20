# Confitura design system

Brand rules, design tokens and logo files for anything that carries the Confitura name: the webpage, the admin app, slides, sponsor offers and print. It was built from the official logo pack and the live sites (2023.confitura.pl, 2025.confitura.pl, confitura.pl).

## What's here

| Page or file | What it holds |
| --- | --- |
| [brand.md](brand.md) | The rules: voice, logo use, colour, type, shape, layout, imagery |
| [components.md](components.md) | Eleven conference components and what each one needs |
| [facts.md](facts.md) | Facts and figures for copy, plus the history of editions |
| [tokens.json](tokens.json) | Every token with a usage note (source of truth) |
| [tokens.css](tokens.css) | The same tokens as CSS custom properties, generated from `tokens.json` |
| [assets/logos/](assets/logos/) | Official logo and jar in SVG and PNG |
| [assets/print/](assets/print/) | CMYK PDF masters for printers |
| [components/](components/) | Reference implementation (React, CSS, types) |
| [fonts/](fonts/) | Open fallback fonts: Outfit and JetBrains Mono |

## How it maps to the code

| Design system | In the repo |
| --- | --- |
| `jam` #EE1F46 | `$brand` in `webpage/assets/_colors.scss` |
| Neuzeit Grotesk (`display`, `sans`) | `webpage/assets/fonts/`, loaded in `webpage/assets/styles.scss` |
| `content-max` 1440px | `$max-width` in `webpage/assets/_sizes.scss` |
| Pictograms | `webpage/assets/{sessions,presentations,speakers,views,videos,participants}.svg` |
| Site header logos | `webpage/assets/logo_black.svg`, `logo_white.svg` (jar drawn in #ED363D) |

## Open decisions

- **One screen red.** The UI uses #EE1F46, the official logo files #FF0040 and the site's header logo #ED363D.
- **Edition count.** Both the 2023 and the 2025 sites call their edition the 15th.
- **History.** The editions table in [facts.md](facts.md) is missing 2012–2016.

The editable version, with live previews, is a Claude design-system artifact: https://claude.ai/artifact/FX9cZGivKyM5yfF52zq8bT (private; ask for access). When you change it there, copy the changed files here too.
