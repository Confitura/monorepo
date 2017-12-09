[![Stories in Ready](https://badge.waffle.io/Confitura/jelatyna-backend.svg?label=ready&title=Ready)](http://waffle.io/Confitura/jelatyna-backend)
[![Build Status](https://travis-ci.org/Confitura/jelatyna-backend.svg?branch=master)](https://travis-ci.org/Confitura/jelatyna-backend)
# jelatyna-backend

This project is part of stack for managing [Confitura](confitura.pl) conference 

In order to use this project you will also need an UI - for this there is separate repo [confitura-page](https://github.com/Confitura/confitura-page) 
 

## how to start backend

### *nix:
`./mvnw spring-boot:run`

### Windows:
`mvnw.bat spring-boot:run`

## users
When starting app in development mode application will not use real oAuth providers. 
Instead there are few fake accounts. Details are below

### admin
to be an admin use Google to login 

### volunteer
to be volunteer login with facebook

## access to db

### connect to db from IDEA
You can use h2 server mode to connect from external too like IDEA Database tool.
To do it use same version of driver used in app and connect using `jdbc:h2:tcp://localhost:9092/mem:jelatyna` url and `confitura` as both user and password

### store changes
By default application starts with in memory h2 database. This means that with every restart application state is the same.

If you want to store your changes you can use file base storage. Change url in `application-fake-db.yml` to `jdbc:h2:PATH_TO_YOUR_STORAGE` and use `jdbc:h2:tcp://localhost:9092/PATH_TO_YOUR_STORAGE` to connect via tcp (eg. from IDEA)

