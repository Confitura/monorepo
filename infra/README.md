# Confitura infrastructure as code

Declared state of the three Coolify applications that run Confitura:

| Service | Coolify app | Directory in this repo |
| --- | --- | --- |
| https://confitura.pl/ | `webpage` | `webpage/` |
| https://app.confitura.pl/ | `admin_app` | `admin-app/` |
| https://api.confitura.pl/ | `backend` | `jelatyna-backend/` |

Configuration changes land as commits here, not as clicks in the Coolify UI.

**Building and deploying images is not this directory's job.**
`.github/workflows/deploy-images.yml` builds each service, pushes it to GHCR and
calls Coolify's deploy endpoint. That keeps working exactly as it does today.
This directory owns configuration only: environment variables, domains, ports,
limits, health checks.

## Status

**Imported, not yet applied.** On 2026-10-09 the three applications and 38 of
their environment variables were imported into state, and `tofu plan` reports
"No changes". Nothing has ever been applied from here.

Still to do:

- **CI secrets.** `infra-plan.yml` and `infra-apply.yml` exist but need the
  secrets and environment described under [CI](#ci) before they can run.
- **15 backend variables with dotted names** (`SPRING.DATASOURCE.URL`,
  `APP.CORS.ORIGINS[0]`, ...) are unmanaged because the provider only accepts
  shell identifiers. They need renaming to their Spring relaxed-binding form
  (`SPRING_DATASOURCE_URL`, `APP_CORS_ORIGINS_0`), which restarts the backend.
  The full list is in `production.auto.tfvars`.
- **Preview copies** of every variable, and admin_app's `VITE_API_URL` /
  `VITE_SELF_URL` (which exist in Coolify but do nothing, see below), are
  deliberately unmanaged.

## Layout

```
infra/
  .sops.yaml                      age public key + encryption rules
  modules/coolify-app/            the shared shape of a Confitura application
  production/
    versions.tf                   provider pins + the B2 state backend
    providers.tf                  Coolify endpoint; token comes from the env
    main.tf                       SOPS decryption + one module call per app
    production.auto.tfvars        >>> non-secret config, edit this one <<<
    secrets.enc.yaml              SOPS-encrypted secret values
  scripts/
    coolify-inventory.sh          read-only dump of the live configuration
    import-production.sh          tofu import of the live applications
```

## How do I add an environment variable?

### A non-secret one

1. Edit `infra/production/production.auto.tfvars` and add an entry under the
   right application's `env_vars`:

   ```hcl
   env_vars = {
     FEATURE_NEW_AGENDA = { value = "true", is_runtime = true }
   }
   ```

2. Open a pull request. The plan workflow comments the `tofu plan` on the PR.
   Read it. It should show exactly one variable being added and nothing else.
3. Merge. The apply workflow applies it.
4. **Redeploy the application in Coolify.** Changing a variable does not
   restart the container, so the running service keeps the old value until it
   is redeployed. Then check the service still answers.

### A secret one

1. Add the name (not the value) to the application's `secret_env_var_flags` in
   `production.auto.tfvars` if it needs a flag; otherwise skip this step.
2. Add the value to the encrypted file:

   ```bash
   sops infra/production/secrets.enc.yaml
   ```

   The layout is `<application key>.<VAR_NAME>: value`, e.g. a `DB_PASSWORD`
   for the backend is `backend: { DB_PASSWORD: ... }`. SOPS encrypts values and
   leaves the names readable, so a diff shows *which* secret changed without
   showing what it changed to.

3. Commit the encrypted file. Never commit a decrypted copy; `.gitignore`
   covers the usual names but it cannot save you from a new one.

You need the age private key to run `sops`. The owner holds it (locally at
`~/.config/sops/age/keys.txt`, pointed to by `SOPS_AGE_KEY_FILE`, and backed up
in a password manager); CI gets it as the GitHub Actions secret `SOPS_AGE_KEY`.
It is not in this repo and must not be pasted anywhere, including an agent chat.

#### If a private key is ever exposed

Rotate rather than hope. This was done on 2026-10-09 after the original key
leaked into an agent run transcript; it was cheap because the file held only a
placeholder. Once production values are in the file, rotation also means
changing every one of those values, since anyone holding the old key can read
them from git history.

1. `age-keygen -o ~/.config/sops/age/keys.txt` (move the old file aside first).
2. Replace the recipient in `infra/.sops.yaml` with the new public key.
3. `sops updatekeys infra/production/secrets.enc.yaml` (needs the old key to
   decrypt), commit, and update the `SOPS_AGE_KEY` GitHub secret.

`SOPS_AGE_KEY` holds the multi-line age *key-file* form (`# created:` /
`# public key:` / `AGE-SECRET-KEY-...`), so a command that prints only
environment variable *names* — `env | cut -d= -f1` — still prints the key body.
Redact with `sed -E 's/AGE-SECRET-KEY-1[0-9A-Z]+/<REDACTED>/'` when inspecting
the environment.

### Changing a variable's name

Renaming forces Coolify to replace the variable: the old one is deleted and a
new one created. For a variable the running container reads at startup, treat it
as a restart, not an edit.

### What you cannot change here

`admin-app`'s `VITE_API_URL` and `VITE_SELF_URL` are baked into the image at
build time as `--build-arg` in `deploy-images.yml`. Coolify does hold
variables with those names, but they have no effect and are left unmanaged. Changing them is a code change to that workflow plus a
rebuild.

## CI

| Workflow | When | What |
| --- | --- | --- |
| `.github/workflows/infra-plan.yml` | PR touching `infra/` | fmt, validate, plan; posts the plan as one PR comment, updated on each push |
| `.github/workflows/infra-apply.yml` | push to master touching `infra/production` or `infra/modules`; manual dispatch | plan to a file, apply that file, then re-plan and fail if any diff remains |

The repository is public, so **plan comments are public**. Plain values are
public anyway (they are in `production.auto.tfvars`); secret values print as
`(sensitive value)`. Fork and dependabot PRs get no secrets and skip the plan.

Secrets to set up once:

| Where | Name | Value |
| --- | --- | --- |
| Repository secret | `COOLIFY_TOKEN_READ` | Coolify token with `read:sensitive`, **no write** (plan only) |
| Repository secret | `B2_KEY_ID` | B2 key ID for bucket `bcc-opentofu-coolify` |
| Repository secret | `B2_APPLICATION_KEY` | B2 application key |
| Repository secret | `SOPS_AGE_KEY` | full contents of the age key file (all three lines) |
| Environment `infra-production` | `COOLIFY_TOKEN` | Coolify token with `read:sensitive` **and write** |

Restrict the `infra-production` environment to the `master` branch
(Settings → Environments → Deployment branches), so the write token is only
reachable from merged code. The environment also has a required reviewer, so
**every apply waits for approval**: after a merge, the `infra apply` run pauses
until a reviewer clicks *Review deployments → Approve* on it. Read the plan
comment on the merged PR before approving. To apply without a merge (e.g. to
correct drift), start `infra apply` by hand from the Actions tab; it waits for
the same approval.

## Running tofu by hand

Normally you do not: applies go through the pipeline, and a hand-run apply is
for emergencies.

```bash
# Locally these live in ~/.config/confitura/infra.env (mode 600): source it.
export COOLIFY_TOKEN=...                       # needs read:sensitive or root
export AWS_ACCESS_KEY_ID=...                   # Backblaze B2 key id
export AWS_SECRET_ACCESS_KEY=...               # Backblaze B2 application key
export AWS_REQUEST_CHECKSUM_CALCULATION=when_required
export AWS_RESPONSE_CHECKSUM_VALIDATION=when_required
export SOPS_AGE_KEY_FILE=~/.config/sops/age/keys.txt  # or SOPS_AGE_KEY=<key file contents>

cd infra/production
tofu init
tofu plan -lock=false
```

### Why `-lock=false` on every command

The state lives in Backblaze B2, which does not implement the conditional write
that OpenTofu's S3 lock needs; it answers `501`. DynamoDB locking is AWS-only,
so there is no second mechanism. The owner decided on 2026-10-05 to stay on B2
and serialise applies in the pipeline instead:
`infra-apply.yml` carries `concurrency: { group: tofu-apply-production,
cancel-in-progress: false }`, and that group is the only thing preventing two
concurrent applies from corrupting state. Bucket versioning is on, which is the
recovery path if it happens anyway. Do not enable Object Lock on that bucket.

**So: never run `tofu apply` by hand while a pipeline apply might be running.**

## Safety rules that are not negotiable

- The three applications are adopted by import, never recreated. The module
  carries `prevent_destroy`, so a plan that would destroy or replace one fails
  at plan time. If you hit that, something is wrong with the config - do not
  remove the guard.
- An empty plan is the proof of correctness. A leftover diff is unfinished
  work, not noise.
- State is never committed. Secret values are never committed in clear.
- DNS, the server OS and database schemas are out of scope for this directory.
