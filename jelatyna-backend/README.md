[![Stories in Ready](https://badge.waffle.io/Confitura/jelatyna-backend.svg?label=ready&title=Ready)](http://waffle.io/Confitura/jelatyna-backend)
# jelatyna-backend

This project is part of stack for managing [Confitura](confitura.pl) conference 

In order to use this project you will also need an UI - for this there is separate repo [confitura-page](https://github.com/Confitura/confitura-page) 


## how to start backend

The backend needs a local PostgreSQL on `localhost:5432` (database and user `jelatyna`, password `password`).
The root `docker-compose.yml` provides one, pinned to prod's version (`postgres:16.10`). From the repo root:

`docker compose up -d postgres`

Then start the backend with the `fake-db` and `fake-security` profiles:

### *nix:
`./mvnw spring-boot:run -Dspring-boot.run.profiles=fake-db,fake-security`

### Windows:
`mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=fake-db,fake-security`

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
The `fake-db` profile resets the database and seeds fake speakers and presentations on every start. This means that with every restart application state is the same.

