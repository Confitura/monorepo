# PII in logs

Personal data (names, e-mail addresses, logins, IP addresses) and credentials (tokens, OAuth
secrets) must not reach logs: in prod they are shipped off the box. This records the log
statements that leaked them, the fixes (S1-S9), and the conventions for new logging.

The fixes come from `ee8858e9` ("remove PII log") and PR #465 ("Logs pii cleanup", `0ace3b11`).
Several classes they touched have since been removed or moved; each fix below says what holds
today.

## Fixes

**S1 - Twitter profile body.** `TwitterService.mapToUser` logged the whole
`verify_credentials` response at info: the Twitter profile (name, handle and other profile
fields such as location and bio). No e-mail was in it, since the request never sent
`include_email`. The statement was deleted; it stays deleted.

**S2 - Allegro buyer login.** `AllegroVoucherSender` and `AllegroClient.sendMessage` logged the
buyer's Allegro login. They logged the voucher message id instead (`sending voucher message {}
failed`), or logged the send without the recipient (`sending Allegro message`). *Code removed:* the Allegro integration is gone (#738); only the
`AllegroContext` embeddable on `Voucher` remains, and nothing logs it.

**S3 - Allegro bodies and secrets.** `AllegroClient` logged every API response body (buyer data,
message text) and had ScribeJava `.debug()` on, which dumps raw requests and responses.
`AllegroAuthorizationContext` printed the expected and received OAuth `state` with
`System.out`. The fix logged verb, URL and status, and for errors only the parsed error
envelope (`code: message (path)`), dropped `.debug()`, and logged just "state does not
match". *Code removed* with the Allegro integration; the error-envelope idea lives on in
convention 4.

**S4 - Mail recipient addresses.** `MailSender` logged the full recipient address on every
send, and `MailController` logged the whole `MessageInfo` (address, name, link token) when a
send failed. Both now log the template and `MessageInfo.maskedEmail()`. Today
`MandrillMailSender.send` does this. *`MailController` is removed* (#745), so only the sender
remains.

**S5 - `MessageInfo.toString()`.** The Lombok `toString` printed the address, the ticket bytes
and the variable values (the recipient's name and the link token). It is now allowlisted:
`hasTicket`, the masked address, and the variable *keys* only.

**S6 - Participant and buyer in failure logs.** `RegistrationController` logged the user's
e-mail when sending a ticket or survey failed, and the voucher's original buyer when a
reminder failed. It now logs ids: `Error on sending ticket to participant {}` with
`user.getId()`. *Partly removed:* the reminder and survey senders no longer exist; the ticket
path is the one left.

**S7 - Entity and principal `toString`.** Entities and `JelatynaPrincipal` end up in log lines
through `{}` placeholders. `JelatynaPrincipal` and `Voucher` got `@ToString(onlyExplicitlyIncluded
= true)` (principal: id and roles, no name; voucher: id, type, send date, no buyer or comment),
and `ParticipationData` lost `gender`, `size` and `info`, and `User` lost `photo`, from the
included fields. Today none of these four prints a name, e-mail or free text.

**S8 - Login token in the URL.** The backend redirected the browser to
`...?access_token=<jwt>&state=...`; query strings are written to access logs, proxies and
`Referer` headers. The redirect now carries `?state=...#access_token=<jwt>`: a fragment is
never sent to a server. `OAuth2LoginController`, `TwitterLoginController` and their fake
counterparts in `demo` build it, and the admin-app's `login.[provider].vue` reads it with
`useUrlSearchParams('hash-params')`. The page ignores a token passed in the query string
(`pages.spec.ts` covers both).

**S9 - Client IPs in the admin-app's nginx log.** `admin-app/docker/nginx.conf` defines a
`no_ip` log format, nginx's `combined` without `$remote_addr`, and writes the access log with
it.

## Conventions

1. **Log ids, not e-mails.** Entity ids identify a record without exposing the person:
   `log.error("Error on sending ticket to participant {}", user.getId())`
   (`RegistrationController`); `log.info("Rejected token of user {} that no longer exists",
   principal.getId())` (`JwtAuthenticationFilter`).
2. **No id yet? Mask the address.** `MessageInfo.maskedEmail()` keeps the first character and
   the domain (`e***@example.com`), and returns `<no address>` for null and `***` for a value
   without a usable `@`. `MandrillMailSender` logs `template, messageInfo.maskedEmail()`; `MessageInfoTest`
   pins the format.
3. **Allowlist `toString` for anything holding PII.** Use `@ToString(onlyExplicitlyIncluded =
   true)` and `@ToString.Include` on the safe fields, never the default "everything but".
   Examples: `User`, `Voucher`, `ParticipationData`, `JelatynaPrincipal`, `MessageInfo`
   (which includes keys, not values). New fields are then private by default.
4. **Third-party errors: the envelope, not the body.** Log or rethrow the provider's status,
   code and message, not the response, which may echo personal data or the request.
   `MandrillMailSender.send` rethrows `MandrillApiError` as an `IOException` carrying
   `getMandrillErrorAsJson()`, "never the rendered mail". The Allegro `describeErrors` that
   first did this (S3) is gone.
5. **Credentials never go in a query string.** URLs reach access logs, proxies and
   `Referer`; put tokens in a fragment (S8) or an `Authorization` header. `JwtAuthenticationFilter`
   reads the bearer from the header.

## Known gaps

- `FakeLoginUsers` (profile `fake-security`, demo only) logs the fake admin's token on
  start-up. It is a dummy secret for local development, never real data; do not copy it.
- `Security.isAuthenticated` and `TokenService.asToken` log a `JelatynaPrincipal` / `User` at
  info on every call; they are safe only because of S7, so keep those `toString`s allowlisted.
