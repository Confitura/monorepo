# Confitura

Domain glossary for the Confitura conference management monorepo. Only terms whose
meaning is specific to this project belong here.

## FAQ

**FAQ Entry**:
A single question-and-answer pair, managed independently, published to the public
webpage. Belongs to exactly one Category.
_Avoid_: Question (that is only the entry's title field)

**Category** (`FaqCategory`):
A named dictionary entry that groups FAQ Entries. A first-class entity (not a free-text
label): it owns its `name` (unique, compared trimmed and case-insensitively), its
own `published` flag (same field name as on FAQ Entry, for consistency), and its own
display order — independent of the order of the Entries within it. An Entry is shown
publicly iff its Category is `published` AND the Entry itself is `published` (master
switch; hiding a Category does not alter its Entries' own flags). An Entry references its Category by foreign key; the public
dump derives the category string from `Category.name`.
_Avoid_: Group, Section, Tag

**Merge (categories)**:
Recovery operation that moves every FAQ Entry from one Category into another and then
removes the emptied Category. Used to fix duplicates created by mistake.
