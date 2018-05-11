# airport-query-application

This is a Play application that uses Java, and communicates with a h2 in memory database using Ebean.

3 tables; airports, countries and runways are provided as input. The application lets users search airports by country name or code. Also, 3 different reports can be seen.

## Play

Play documentation:

[https://playframework.com/documentation/latest/Home](https://playframework.com/documentation/latest/Home)

## Ebean

EBean is a Java ORM library that uses SQL:

[https://www.playframework.com/documentation/latest/JavaEbean](https://www.playframework.com/documentation/latest/JavaEbean)

and the documentation of Ebean:

[https://ebean-orm.github.io/](https://ebean-orm.github.io/)

## Features

### Queries

- Query for the airports in the database by country name or country code.
- Shows runways of the airport when an airport is selected.
- Query strings can be partial.

### Reports

- Shows 10 countries with the highest number of airports & 10 countries with the lowest number of airports (with airport count).
- Shows type of runways per country.
- Shows the top 10 most common runway identifications.

## ToDos

- Improve readme.md file, explain installing, starting, etc.
- Make tests work. Tests are currently not working because evolutions do not work for the test cases.
