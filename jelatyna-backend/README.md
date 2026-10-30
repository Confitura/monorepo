[![Stories in Ready](https://badge.waffle.io/Confitura/jelatyna-backend.svg?label=ready&title=Ready)](http://waffle.io/Confitura/jelatyna-backend)
# jelatyna-backend

This project is part of stack for managing [Confitura](confitura.pl) conference 

In order to use this project you will also need an UI - for this there is separate repo [confitura-page](https://github.com/Confitura/confitura-page) 


## how to start backend

The backend needs a local PostgreSQL on `localhost:5432` (database and user `jelatyna`, password `password`).
`docker/postgres/docker-compose.yml` provides one, pinned to prod's version (`postgres:16.10`). From the repo root:

`docker compose -f docker/postgres/docker-compose.yml up -d`

It keeps its data in the `postgres16_data` volume. It used to run `postgres:14` on a `postgres_data` volume, which Postgres 16 can't open, so it starts empty; the old volume is left untouched (find it with `docker volume ls`).

Then start the backend in one of two modes.

### fresh demo data on every start (default)
A plain run activates the `local`, `fake-db` and `fake-security` profiles, which **wipes and re-seeds the local database on every start**:

*nix: `./mvnw spring-boot:run`

Windows: `mvnw.cmd spring-boot:run`

### keep data between restarts
Leave out `fake-db`:

*nix: `./mvnw spring-boot:run -Dspring-boot.run.profiles=local,fake-security`

Windows: `mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local,fake-security"`

`-Dspring-boot.run.profiles` replaces the default list, so always include `local`: it holds the local settings (database, dummy JWT secret, mail and listmonk keys, CORS origin). Those secrets are dummies, so never activate `local` in prod.

## building the image
The backend image is built one way only, with Paketo buildpacks:

`./mvnw spring-boot:build-image -DskipTests`

`deploy-images.yml` builds and pushes it to GHCR for prod; `check-builds.yml` runs the same build, without pushing, on every PR and master push that touches the backend.

## users
When starting app in development mode application will not use real oAuth providers. 
Instead there are few fake accounts. Details are below

### admin
to be an admin use Google to login 

### volunteer
to be volunteer login with facebook

## access to db

### connect to db from IDEA
Connect an external tool like the IDEA Database tool using the `jdbc:postgresql://localhost:5432/jelatyna` url, `jelatyna` as user and `password` as password.

### store changes
In the default mode the `fake-db` profile resets the database and seeds fake speakers and presentations on every start. This means that with every restart application state is the same.

In keep-data mode nothing is reset. `fake-security` re-saves the three fake accounts on every start (undoing edits to them) and adds the fake speaker's test talk and workshop only while the speaker has no presentations, so restarts don't create duplicates.

