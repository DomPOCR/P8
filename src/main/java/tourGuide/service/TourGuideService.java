package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import tourGuide.model.*;
import tourGuide.tracker.Tracker;
import tourGuide.helper.InternalTestHelper;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    boolean testMode = true;
    private List<UserReward> userRewards;

    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

    /**
     * @param
     * @return all user current locations
     */

    public List<UserCurrentLocation> getAllCurrentLocations() {

        List<UserCurrentLocation> userCurrentLocations = new ArrayList<>();
        List<User> userList = getAllUsers();
        for (User user : userList) {

            VisitedLocation lastVisitedLocation = user.getLastVisitedLocation();
            Location location = new Location(lastVisitedLocation.location.latitude, lastVisitedLocation.location.longitude);
            UserCurrentLocation userCurrentLocation = new UserCurrentLocation(user.getUserId(), location.longitude, location.latitude);
            userCurrentLocations.add(userCurrentLocation);
        }
        return userCurrentLocations;
    }

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    /**
     *
     * @param user
     * @return visitedLocation
     */

    public VisitedLocation trackUserLocation(User user) {

        logger.debug("trackUserLocation start " + user.getUserName());

        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);

        logger.debug("trackUserLocation end " + user.getUserName());

        return visitedLocation;
    }

    /**
     * DP
     * list the nearby attraction for the user. The number of attraction is a user's preference
     *
     * @param visitedLocation
     * @param user
     * @return list of nearestAttraction
     */
    public List<UserNearestAttractions> getClosestAttractions(VisitedLocation visitedLocation, User user) {

        logger.debug("getClosestAttractions start " + user.getUserName());

        List<Attraction> attractions = gpsUtil.getAttractions();

        StopWatch watch = new StopWatch();
        watch.start();

        List<UserNearestAttractions> userNearestAttractionsList = attractions.parallelStream()
                .map(a -> new UserNearestAttractions(
                                a.attractionName,
                                a.latitude,
                                a.longitude,
                                visitedLocation.location.latitude,
                                visitedLocation.location.longitude,
                                rewardsService.getDistance(a,visitedLocation.location),
                                rewardsService.getRewardPoints(a,user)))
                .sorted(Comparator.comparingDouble(UserNearestAttractions::getAttractionProximity))
                .collect(Collectors.toList());

        List<UserNearestAttractions> userNearestAttractionsResult = userNearestAttractionsList.parallelStream()
                .limit(user.getUserPreferences().getNumberOfProposalAttraction())
                .collect(Collectors.toList());

        watch.stop();
        logger.debug("Time elapsed : " + watch.getTime());
        logger.debug("getClosestAttractions end " + user.getUserName());

        return userNearestAttractionsResult;
    }

    /*** OLD METHODE NOT USED
    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtil.getAttractions()) {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                nearbyAttractions.add(attraction);
            }
        }
        return nearbyAttractions;
    }
    **/

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    // TODO : externaliser dans une classe de test
    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);
            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
