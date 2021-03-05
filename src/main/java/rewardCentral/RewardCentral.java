package rewardCentral;



import org.apache.commons.lang3.time.StopWatch;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.TourGuideService;

public class RewardCentral {

    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

    public RewardCentral() {
    }

    /**
     *
     * @param attractionId
     * @param userId
     * @return randomInt
     */
    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {

        return ThreadLocalRandom.current().nextInt(1, 1000);
    }
}
