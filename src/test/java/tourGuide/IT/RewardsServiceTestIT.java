package tourGuide.IT;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RewardsServiceTestIT {
    private Logger logger = LoggerFactory.getLogger(RewardsServiceTestIT.class);

    @Test
    public void isWithinAttractionProximity() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        //WHEN
        Attraction attraction = gpsUtil.getAttractions().get(0);

        //THEN
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    // Needs fixed - can throw ConcurrentModificationException
    // DP :correction dans addUserReward dans la classe user pour supprimer la negation

    @Test
    public void nearAttraction() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //WHEN
        // DP : stopTracking move before calculate reward
        tourGuideService.tracker.stopTracking();

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

        logger.debug("getAttractions : " + gpsUtil.getAttractions().size());
        logger.debug("userRewards : " + userRewards.size());

        //THEN
        assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
    }

    @Test
    public void userGetRewards() {

        //GIVEN
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsUtil.getAttractions().get(0);

        //WHEN
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        tourGuideService.tracker.stopTracking();

        //THEN
        assertEquals(userRewards.size(), 1);
    }

}