package tourGuide.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.UserCurrentLocation;
import tourGuide.model.UserNearestAttractions;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tripPricer.Provider;

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
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }

    /**
     * list the nearby attraction for the user. The number of attraction is a user's preference
     * @param userName
     * @return list of nearestAttraction
     */
    // DP :

    @RequestMapping("/getNearbyAttractions")
    public List<UserNearestAttractions> getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getClosestAttractions(visitedLocation,getUser(userName));
    }

    /*** OLD VERSION
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
    }
    ***/
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    /***
     * @param
     * @return all user current locations
     */

    @RequestMapping("/getAllCurrentLocations")
    public List<UserCurrentLocation> getAllCurrentLocations(){
        logger.info("getAllCurrentLocations OK");
        return tourGuideService.getAllCurrentLocations();

    }

       @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}