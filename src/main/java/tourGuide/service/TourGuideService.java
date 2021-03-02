package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.apache.commons.lang3.time.StopWatch;

import tourGuide.helper.InternalTestHelper;
import tourGuide.model.*;
import tourGuide.tracker.Tracker;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    public final Tracker tracker;
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();
    boolean testMode = true;
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
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

        logger.info("getAllCurrentLocations started");
        List<UserCurrentLocation> userCurrentLocations = new ArrayList<>();
        List<User> userList = getAllUsers();
        for (User user : userList) {

            VisitedLocation lastVisitedLocation = user.getLastVisitedLocation();
            Location location = new Location(lastVisitedLocation.location.latitude, lastVisitedLocation.location.longitude);
            UserCurrentLocation userCurrentLocation = new UserCurrentLocation(user.getUserId(), location.longitude, location.latitude);
            userCurrentLocations.add(userCurrentLocation);
        }
        logger.info("getAllCurrentLocations ended");
        return userCurrentLocations;
    }

    /**
     *
     * @param userName
     * @return user
     */
    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        logger.info("getAllUsers started");
        return internalUserMap.values().stream().collect(Collectors.toList());
          }

    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    /**
     *
     * @param user
     * @return list of providers
     */
    public List<Provider> getTripDeals(User user) {

        logger.info("getTripDeals started");

        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);

        logger.info("getTripDeals ended");

        return providers;
    }

    /**
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
                        rewardsService.getDistance(a, visitedLocation.location),
                        rewardsService.getRewardPoints(a, user)))
                .sorted(Comparator.comparingDouble(UserNearestAttractions::getAttractionProximity))
                .collect(Collectors.toList());

        List<UserNearestAttractions> userNearestAttractionsResult = userNearestAttractionsList.parallelStream()
                .limit(user.getUserPreferences().getNumberOfProposalAttraction())
                .collect(Collectors.toList());

        watch.stop();
        logger.debug("Time elapsed to get closest attraction : " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
        logger.debug("getClosestAttractions end " + user.getUserName());

        return userNearestAttractionsResult;
    }

    /**
     * return the list of the user's preference
     *
     * @param userName
     * @return the user's preference
     */
    public UserPreferencesDTO getUserPreference(String userName) {

        logger.info("getUserPreference started");
        User user = getUser(userName);
        UserPreferences userPreferences = user.getUserPreferences();

        UserPreferencesDTO userPreferenceDTO = new UserPreferencesDTO(
                userPreferences.getAttractionProximity(),
                userPreferences.getCurrency().getCurrencyCode(),
                userPreferences.getLowerPricePoint().getNumber().doubleValue(),
                userPreferences.getHighPricePoint().getNumber().doubleValue(),
                userPreferences.getTripDuration(),
                userPreferences.getTicketQuantity(),
                userPreferences.getNumberOfAdults(),
                userPreferences.getNumberOfChildren(),
                userPreferences.getNumberOfProposalAttraction()
        );
        logger.info("getUserPreference ended");
        return userPreferenceDTO;
    }

    /**
     * update the user's preference
     * @param userName
     * @param userPreferences
     * @return the user's preferences updated
     */
    public UserPreferencesDTO setUserPreference(String userName, UserPreferencesDTO userPreferences){

        logger.info("setUserPreference started");

        User user = getUser(userName);

        CurrencyUnit currency = Monetary.getCurrency(userPreferences.getCurrency());
        Money lowerPricePoint = Money.of(userPreferences.getLowerPricePoint(), currency);
        Money highPricePoint = Money.of(userPreferences.getHighPricePoint(), currency);

        UserPreferences userPreferencesResult = new UserPreferences();

        userPreferencesResult.setAttractionProximity(userPreferences.getAttractionProximity());
        userPreferencesResult.setCurrency(currency);
        userPreferencesResult.setLowerPricePoint(lowerPricePoint);
        userPreferencesResult.setHighPricePoint(highPricePoint);
        userPreferencesResult.setTripDuration(userPreferences.getTripDuration());
        userPreferencesResult.setTicketQuantity(userPreferences.getTicketQuantity());
        userPreferencesResult.setNumberOfAdults(userPreferences.getNumberOfAdults());
        userPreferencesResult.setNumberOfChildren(userPreferences.getNumberOfChildren());
        userPreferencesResult.setNumberOfProposalAttraction(userPreferences.getNumberOfProposalAttraction());

        user.setUserPreferences(userPreferencesResult);

        logger.info("setUserPreference ended");

        return userPreferences;
    }
    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

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
