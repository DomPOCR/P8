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

    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {

        logger.info("getAttractionRewardPoints started");

//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//      try {
//           TimeUnit.MILLISECONDS.sleep((long) ThreadLocalRandom.current().nextInt(1, 1000));
//        } catch (InterruptedException var4) {
//        }

        int randomInt = ThreadLocalRandom.current().nextInt(1, 1000);
//        stopWatch.stop();
//        logger.debug("Time elapsed to get Attraction Reward Points : " + stopWatch.getTime(TimeUnit.MILLISECONDS) + " ms");
        logger.info("getAttractionRewardPoints ended");
        return randomInt;
    }
}
