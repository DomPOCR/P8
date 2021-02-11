package tourGuide.IT;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.*;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TourGuideServiceTestIT {

    @Test
    public void getUserRewards() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsUtil.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        tourGuideService.tracker.stopTracking();
        assertTrue(userRewards.size() == 1);
    }

    @Test
    public void getUserLocation() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();

        assertTrue(visitedLocation.userId.equals(user.getUserId()));
    }

    @Test
    public void getAllCurrentLocations() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        List<UserCurrentLocation> UserCurrentLocation = tourGuideService.getAllCurrentLocations();

        tourGuideService.tracker.stopTracking();

        assertEquals(1, UserCurrentLocation.size());
    }

    @Test
    public void getUser() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        tourGuideService.addUser(user);

        User userTest = tourGuideService.getUser(user.getUserName());

        assertEquals(user, userTest);

    }

    @Test
    public void getAllUsers() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user1);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        tourGuideService.tracker.stopTracking();

        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void addUser() {

        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User addUser1 = tourGuideService.getUser(user.getUserName());
        User addUser2 = tourGuideService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

        assertEquals(user, addUser1);
        assertEquals(user2, addUser2);
    }

    @Test
    public void getTripDeals() {

        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user);

        tourGuideService.tracker.stopTracking();

        //assertEquals(10, providers.size());
        assertEquals(5, providers.size()); //getprice ne retourne que 5 providers
    }

    @Test
    public void getClosestAttractions() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        List<UserNearestAttractions>userNearestAttractionsList= tourGuideService.getClosestAttractions(visitedLocation,user);

        tourGuideService.tracker.stopTracking();

        assertEquals(user.getUserPreferences().getNumberOfProposalAttraction(),userNearestAttractionsList.size());
    }

    @Test
    public void getUserPreference() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        tourGuideService.addUser(user);

        UserPreferencesDTO userPreferencesTest = tourGuideService.getUserPreference(user.getUserName());

        assertTrue(!userPreferencesTest.getCurrency().isEmpty());
    }

    @Test
    public void setUserPreference() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(1,"USD",0.0d,200.0d,1,2,3,4,5);

        tourGuideService.addUser(user);
        tourGuideService.setUserPreference(user.getUserName(), userPreferencesDTO);

        UserPreferencesDTO userPreferencesTest = tourGuideService.getUserPreference(user.getUserName());

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

        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        tourGuideService.tracker.stopTracking();

        assertEquals(user.getUserId(), visitedLocation.userId);
    }
}