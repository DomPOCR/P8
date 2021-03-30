## TourGuide App : TourGuide is a travel app made for families.

## Purpose
 
- You can get :
    - Users and their location
    - All users locations at a same time
    - Add user's trip preferences
    - Get user's trip deals
    - and... user's nearby attractions

## Getting Started

- Endpoint : http://localhost:8080/

## Database Schema (UML)

## Prerequisite to run

- Java 1.8 JDK
- Gradle 5.1
- Spring Boot 2.2.6
- Docker 20.10.5 (Optional)

## Run app (localhost:8080)

java -jar Tourguide-0.0.1-SNAPSHOT.jar

## Containerize (Docker)
To build the image
~~~
docker build --build-arg JAR_FILE=build/libs/*.jar -t tourguide .
~~~

To run the app : 
~~~
docker run -d -p 8080:8080 tourguide
~~~

To stop the app : 
~~~
docker stop tourguide
~~~

To remove the image : 
~~~
docker rmi -f tourguide
~~~
# URL :
 
## Get Location
Example : 
GET http://localhost:8080/getLocation?userName=internalUser2

## Get Near By Attractions
Example : 
http://localhost:8080/getNearbyAttractions?userName=internalUser1

## Get All Current Locations
Example : 
http://localhost:8080/getAllCurrentLocations?userName=internalUser1

## Get Trip Deals
Example : 
http://localhost:8080/getTripDeals?userName=internalUser2

## Get User Preference
Example : 
http://localhost:8080/getUserPreference?userName=internalUser2

## Set User Preference
Example : 
http://localhost:8080/setUserPreference?userName=internalUser2

boby example :
{
    "attractionProximity": 2147483647,
    "currency": "USD",
    "lowerPricePoint": 0.0,
    "highPricePoint": 70000,
    "tripDuration": 1,
    "ticketQuantity": 1,
    "numberOfAdults": 2,
    "numberOfChildren": 4,
    "numberOfProposalAttraction": 5
}

## Get Attraction Reward Points
Example : 
http://localhost:8080//getAttractionRewardPoints?attractionId={{$guid}}&userName=internalUser2
