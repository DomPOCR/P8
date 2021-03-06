package tourGuide.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.model.User;
import tourGuide.model.UserReward;

@Service
public class RewardsService {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	/***
	 *
	 * @param gpsUtil
	 * @param rewardCentral
	 */
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}

	/***
	 *
	 * @param proximityBuffer
	 */
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	/***
	 *
	 */
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	/***
	 *
	 * @param user
	 */
	public void calculateRewards(User user) {

		// List<VisitedLocation> userLocations = user.getVisitedLocations();
		// List<Attraction> attractions = gpsUtil.getAttractions();

		// DP Mise en place de listes sous la forme de CopyOnWriteArrayList pour figer les listes le temps du calcul des rewards points

		List<Attraction> attractions = new CopyOnWriteArrayList<>() ;
		List<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();

		// DP ajout add pour renseigner les listes userLocation et attractions (vides dans le code d'origine)
		attractions.addAll(gpsUtil.getAttractions());
		userLocations.addAll(user.getVisitedLocations());

		// DP ajout .stream()

		userLocations.forEach(visitedLocation -> {
			attractions.forEach(attraction -> {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			});
		});
	}

	/***
	 *
	 * @param attraction
	 * @param location
	 * @return distance between user location and attraction
	 */
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	/***
	 *
	 * @param visitedLocation
	 * @param attraction
	 * @return distance between user location and near visited attraction
	 */
	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	/***
	 *
	 * @param attraction
	 * @param user
	 * @return reward attraction points
	 */
	public int getRewardPoints(Attraction attraction, User user)  {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	/***
	 *
	 * @param loc1
	 * @param loc2
	 * @return distance in miles between loc 1 and loc2
	 */
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
