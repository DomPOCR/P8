package tourGuide.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.exceptions.UserNameNotFoundException;
import tourGuide.exceptions.UserPreferenceEmptyException;
import tourGuide.model.*;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import javax.validation.Valid;

@RestController
public class TourGuideController {

    private final Logger logger = LoggerFactory.getLogger(TourGuideController.class);

    @Autowired
    TourGuideService tourGuideService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /***
     *
     * @param userName
     * @return location of userName
     */
    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam @Valid String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    /***
     * @param
     * @return all user current locations
     */

    @RequestMapping("/getAllCurrentLocations")
    public List<UserCurrentLocation> getAllCurrentLocations() {

        return tourGuideService.getAllCurrentLocations();
    }

    /**
     * list the nearby attraction for the user. The number of attraction is a user's preference
     *
     * @param userName
     * @return list of nearestAttraction
     */
    // DP :
    @RequestMapping("/getNearbyAttractions")
    public List<UserNearestAttractions> getNearbyAttractions(@RequestParam @Valid String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getClosestAttractions(visitedLocation, getUser(userName));
    }

    /*** OLD VERSION
     @RequestMapping("/getNearbyAttractions") public String getNearbyAttractions(@RequestParam String userName) {
     VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
     return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
     }
     ***/

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam @Valid String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }



    /**
     * @param userName
     * @return proposition of trip
     */

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam @Valid String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    /**
     * @param userName
     * @return list of the user's preference
     * @throws UserNameNotFoundException
     */
    @RequestMapping("/getUserPreference")
    public UserPreferencesDTO getUserPreference(@RequestParam @Valid String userName) throws UserNameNotFoundException {

       if (tourGuideService.getUser(userName) == null) {
            String message = "UserName not found : " + userName;
            throw new UserNameNotFoundException(message);
        }
       return tourGuideService.getUserPreference(userName);
    }

    /**
     * @param userName
     * @param userPreference
     * @return list of the user's preferences updated
     * @throws UserNameNotFoundException
     * @throws UserPreferenceEmptyException
     */
    @PostMapping("/setUserPreference")
    public UserPreferencesDTO setUserPreference(@RequestParam @Valid String userName, @RequestBody UserPreferencesDTO userPreference) throws UserNameNotFoundException, UserPreferenceEmptyException {

       if (tourGuideService.getUser(userName) == null ) {
            String message = " this username does not exist : "+ userName;
            throw new UserNameNotFoundException(message);
        }
        if (userPreference == null ) {
            String message = " userPreference is empty  ";
            throw new UserPreferenceEmptyException(message);
        }
        return tourGuideService.setUserPreference(userName,userPreference);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }


}