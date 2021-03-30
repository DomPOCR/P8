## TourGuide App TourGuide is a travel app made for families.

## purpose
 
- You can get :
    - Users and their location
    - All users locations at a same time
    - Add user's trip preferences
    - Get user's trip deals
    - and... user's nearby attractions in real time!

## Database Schema (UML)

## Prerequisite to run

- Java 1.8 JDK
- Gradle 6.6.1
- Docker

## Run app (localhost:8080)

Spring Boot
~~~
mvn spring-boot:run (run app)
mvn spring-boot:stop (stop app)
~~~~

Gradle
~~~
gradle bootRun
~~~

## Containerize
to build the image
~~~
docker build - t tourguide .
~~~

To run the app : 
~~~
docker run -d -p 8080:8080 -- name tourguide tourguide
~~~


## Documentation
 Documentation Postman [here]()
