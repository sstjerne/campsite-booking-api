# Campsite Booking REST - API #


### Development requisites

* JDK 1.8
* Docker
* Maven


### Development setup & Build

1. Use `sstjerne.campsite.booking.api.database.local` as a template.
2. Build the docker image by executing `mvn clean install dockerfile:build`. This will create an image named *sstjerne.campsite.booking.api/campsite-booking-api*.
6. Start the local environment by running `docker-compose up`
8. To stop the environment (containers) press `CTRL + C`
9. Running `docker-compose down` will clean up the environment by removing the services (containers) and networks specified on `docker-compose.yml`


#### Run from maven/console
* On the project root folder, run the following command: `mvn clean install -Dmaven.test.skip=true` . That will generate the different maven modules involved that are needed by the API.
* If the previous operation was successful, run ` mvn spring-boot:run`. Will start the spring boot application for the API. Please, have in mind that this has a dependency on the Postgres server 


### Development deployment explanation

Running `docker-compose up` creates 2 services:

#### sstjerne.campsite.booking.api/campsite-booking-api

This project's image. Contains the REST API application.

#### campsite-booking-database

A PostgreSQL database to use as persistant storage by campsite-booking-database.


#### Exposed ports

Each service exposes at least one port to individually acccess it if necessary. 

| Service | Port | Notes |
| - |:-:|
| campsite-booking-api | 8080 |
| campsite-booking-api | 8001 | Not used now, but will be used for remote debugging |
| campsite-booking-database | 5432 | Just for development support. Allows connecting to the database on localhost:5432 |


### References:
[Spring](http://projects.spring.io/spring-framework/)



### *ie. Request*

Service Booking: 


*Campsite is available*
GET /booking/availability?{from=yyyy-MM-dd}&{to=yyyy-MM-dd}&{month=MM}

*List all booking*
GET
http://localhost:8080/booking?page={page}&size={size}

*Create a booking*
POST
http://localhost:8080/booking


Booking model 
{
"customer": {
	"fullname": "Seba",
	"email": "seba@gmail.com.2018-02-19"
	},
"checkin": "2018-02-22",
"checkout": "2018-02-24",
}


*List a booking by id*
GET
http://localhost:8080/booking/{id}

*Update by id & Model*
UPDATE
http://localhost:8080/booking/{id}

Booking model= {
"checkin": "2018-02-22",
"checkout": "2018-02-24",
}


*Cancel booking by id*
DELETE
http://localhost:8080/booking/{id}


