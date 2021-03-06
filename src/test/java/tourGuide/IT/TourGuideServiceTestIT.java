package tourGuide.IT;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.exceptions.UserNameNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.*;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootTest
public class TourGuideServiceTestIT {

    @Test
    public void getUserRewards() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsUtil.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

        // WHEN
        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(1, userRewards.size());
    }

    @Test
    public void getUserLocation() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        // WHEN
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(visitedLocation.userId, user.getUserId());
    }

    @Test
    public void getAllCurrentLocations() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        // WHEN
        tourGuideService.addUser(user);
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        List<UserCurrentLocation> UserCurrentLocation = tourGuideService.getAllCurrentLocations();
        tourGuideService.tracker.stopTracking();

        assertEquals(1, UserCurrentLocation.size());
    }

    @Test
    public void getUser() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        tourGuideService.addUser(user);
        User userTest = tourGuideService.getUser(user.getUserName());

        //THEN
        Assertions.assertEquals(user, userTest);

    }

    @Test
    public void getAllUsers() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //Add users to service
        User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        //WHEN
        tourGuideService.addUser(user1);
        tourGuideService.addUser(user2);

        //Get all users
        List<User> allUsers = tourGuideService.getAllUsers();

        tourGuideService.tracker.stopTracking();

        //THEN
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void addUser() {

        //GIVEN
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        //WHEN
        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User addUser1 = tourGuideService.getUser(user.getUserName());
        User addUser2 = tourGuideService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

        //THEN
        assertEquals(user, addUser1);
        assertEquals(user2, addUser2);
    }

    @Test
    public void getTripDeals() {

        //GIVEN
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        List<Provider> providers = tourGuideService.getTripDeals(user);
        tourGuideService.tracker.stopTracking();

        //THEN
        //assertEquals(10, providers.size());
        assertEquals(5, providers.size()); //getprice ne retourne que 5 providers
    }

    @Test
    public void getClosestAttractions() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        List<UserNearestAttractions> userNearestAttractionsList = tourGuideService.getClosestAttractions(visitedLocation, user);
        tourGuideService.tracker.stopTracking();

        //THEN
        assertEquals(user.getUserPreferences().getNumberOfProposalAttraction(), userNearestAttractionsList.size());
    }

    @Test
    public void getUserPreference() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        tourGuideService.addUser(user);
        UserPreferencesDTO userPreferencesTest = tourGuideService.getUserPreference(user.getUserName());

        //THEN
        assertFalse(userPreferencesTest.getCurrency().isEmpty());
    }

    @Test
    public void setUserPreference() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(1,"USD",0.0d,200.0d,1,2,3,4,5);
        tourGuideService.addUser(user);
        tourGuideService.setUserPreference(user.getUserName(), userPreferencesDTO);
        UserPreferencesDTO userPreferencesTest = tourGuideService.getUserPreference(user.getUserName());

        //THEN
        assertEquals(userPreferencesDTO.getAttractionProximity(),userPreferencesTest.getAttractionProximity());
        assertEquals(userPreferencesDTO.getCurrency(),userPreferencesTest.getCurrency());
        assertEquals(userPreferencesDTO.getLowerPricePoint(),userPreferencesTest.getLowerPricePoint());
        assertEquals(userPreferencesDTO.getHighPricePoint(),userPreferencesTest.getHighPricePoint());
        assertEquals(userPreferencesDTO.getTripDuration(),userPreferencesTest.getTripDuration());
        assertEquals(userPreferencesDTO.getTicketQuantity(),userPreferencesTest.getTicketQuantity());
        assertEquals(userPreferencesDTO.getNumberOfAdults(),userPreferencesTest.getNumberOfAdults());
        assertEquals(userPreferencesDTO.getNumberOfChildren(),userPreferencesTest.getNumberOfChildren());
        assertEquals(userPreferencesDTO.getNumberOfProposalAttraction(),userPreferencesTest.getNumberOfProposalAttraction());
    }

    @Test
    public void trackUser() {

        //GIVEN
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();

        //THEN
        assertEquals(user.getUserId(), visitedLocation.userId);
    }


}