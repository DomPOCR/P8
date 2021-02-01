package tourGuide.model;

import gpsUtil.location.Location;

import java.util.UUID;

public class UserCurrentLocation {

    private UUID userId;
    private Location userLocations;


    public UserCurrentLocation(UUID userId, Location visitedLocations) {
        this.userId = userId;
        this.userLocations = visitedLocations;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Location getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(Location userLocations) {
        this.userLocations = userLocations;
    }
}
