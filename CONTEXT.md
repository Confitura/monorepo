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

## News

**News Entry**:
A dated announcement published on the public webpage's news page; the newest one also
headlines the home page. Written and edited by admins in Jelatyna. Not an email: it
reaches no subscriber's inbox. Its date (a moment, with time of day) is both the date
shown and when it goes public: an entry is public once it is published and its date has
passed, so a future date schedules it. Entries are listed newest date first. Each has its
own page, addressed by a slug that an admin may change but that never follows edits to the
title, and may have a cover image.
_Avoid_: Campaign, post

**Newsletter**:
An email sent to subscribers from the newsletter platform (Listmonk), written there.
Independent of News Entries: an announcement that should be both is written twice.
_Avoid_: News

## Users and logins

**User**:
A person known to Jelatyna: the owner of a profile, roles (admin, volunteer), talks and
registration. One per person; has zero or more Logins.
_Avoid_: Account, member

**Login**:
One identity-provider account (a provider plus that provider's id for the person, e.g.
GitHub user 12345) that signs in as exactly one User.
_Avoid_: Account, social account, identity

**Manual User**:
A User created by an admin instead of through sign-in, so it has no Login yet, e.g. a
speaker added before they ever log in.
_Avoid_: Fake user, placeholder

**Link (a Login)**:
Attaching a Login that belongs to no User yet to the signed-in User, proven by completing
that provider's sign-in while signed in.
_Avoid_: Connect, add account

**Merge (users)**:
Folding one User (the absorbed User) into another (the survivor) when both are the same
person: every Login, talk and the ticket move to the survivor, which keeps its own profile
values and takes the absorbed User's only where its own are empty; roles and privacy
consent combine (either one counts). The absorbed User is then deleted. Self-service when
the person proves both by signing in (the signed-in User survives); otherwise an admin
merges and picks the survivor. Blocked when both Users hold a ticket. Irreversible; a
wrong merge is repaired by hand (e.g. moving a talk by adding and removing a co-speaker).
_Avoid_: Combine, join, dedupe

**Unlink (a Login)**:
Removing a Login from a User; the Login is deleted. A User can unlink any Login except
their last; an admin can unlink any, which can leave the User with no Login.
_Avoid_: Disconnect, remove account
