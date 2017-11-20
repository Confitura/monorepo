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
to be an admin use twitter to login 

### volunteer
to be volunteer login with gmail account (google+)
