package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
        logger.debug("Time elapsed : " + watch.getTime());
        logger.debug("getClosestAttractions end " + user.getUserName());

        return userNearestAttractionsResult;
    }

    // TODO : externaliser dans une classe de test

    /**
     * return the list of the user's preference
     *
     * @param userName
     * @return the user's preference
     */
    public UserPreferencesDTO getUserPreference(String userName) {

        User user = getUser(userName);

        UserPreferencesDTO userPreferenceDTO = new UserPreferencesDTO(
                user.getUserPreferences().getAttractionProximity(),
                user.getUserPreferences().getCurrency().getCurrencyCode(),
                user.getUserPreferences().getLowerPricePoint().getNumber().doubleValue(),
                user.getUserPreferences().getHighPricePoint().getNumber().doubleValue(),
                user.getUserPreferences().getTripDuration(),
                user.getUserPreferences().getTicketQuantity(),
                user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getNumberOfProposalAttraction()
        );
        return userPreferenceDTO;
    }

    /**
     * update the user's preference
     * @param userName
     * @param userPreferences
     * @return the user's preferences updated
     */
    public UserPreferences setUserPreference(String userName, UserPreferencesDTO userPreferences){

        UserPreferences userPreferencesResult = new UserPreferences();
        User user = getUser(userName);

        CurrencyUnit currency = Monetary.getCurrency(userPreferences.getCurrency());
        Money lowerPricePoint = Money.of(userPreferences.getLowerPricePoint(), currency);
        Money highPricePoint = Money.of(userPreferences.getHighPricePoint(), currency);

        userPreferencesResult.setAttractionProximity(userPreferences.getAttractionProximity());
        userPreferencesResult.setCurrency(currency);
        userPreferencesResult.setLowerPricePoint(lowerPricePoint);
        userPreferencesResult.setHighPricePoint(highPricePoint);
        userPreferencesResult.setTripDuration(userPreferences.getTripDuration());
        userPreferencesResult.setTicketQuantity(userPreferences.getTicketQuantity());
        userPreferencesResult.setNumberOfAdults(userPreferences.getNumberOfAdults());
        userPreferencesResult.setNumberOfProposalAttraction(userPreferences.getNumberOfProposalAttraction());

        user.setUserPreferences(userPreferencesResult);

        return userPreferencesResult;
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
