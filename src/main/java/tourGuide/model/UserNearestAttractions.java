package tourGuide.model;

import gpsUtil.location.Location;

public class UserNearestAttractions {

    // Name of Tourist attraction,
    private String attractionName;

    // Tourist attractions lat/long,
    private Double attractionLatitude;
    private Double attractionLongitude;

    // The user's location lat/long,
    private double userLatitude;
    private double userLongitude;

    // The distance in miles between the user's location and each of the attractions.
    private double attractionProximity;

    // The reward points for visiting each Attraction.
    private int attractionRewardPoint;

    public UserNearestAttractions(){
    }

    public UserNearestAttractions(String attractionName, Double attractionLatitude, Double attractionLongitude, double userLatitude, double userLongitude, double attractionProximity, int attractionRewardPoint) {
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.attractionProximity = attractionProximity;
        this.attractionRewardPoint = attractionRewardPoint;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public Double getAttractionLatitude() {
        return attractionLatitude;
    }

    public void setAttractionLatitude(Double attractionLatitude) {
        this.attractionLatitude = attractionLatitude;
    }

    public Double getAttractionLongitude() {
        return attractionLongitude;
    }

    public void setAttractionLongitude(Double attractionLongitude) {
        this.attractionLongitude = attractionLongitude;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public double getAttractionProximity() {
        return attractionProximity;
    }

    public void setAttractionProximity(double attractionProximity) {
        this.attractionProximity = attractionProximity;
    }

    public int getAttractionRewardPoint() {
        return attractionRewardPoint;
    }

    public void setAttractionRewardPoint(int attractionRewardPoint) {
        this.attractionRewardPoint = attractionRewardPoint;
    }
}
