package tourGuide.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import rewardCentral.RewardCentral;
import tourGuide.exceptions.UUIDException;
import tourGuide.exceptions.UserNameNotFoundException;
import tourGuide.service.TourGuideService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class RewardCentralController {

    @Autowired
    private RewardCentral rewardCentral;

    @Autowired
    private TourGuideService tourGuideService;

    /**
     *
     * @param attractionId
     * @param userName
     * @return Attractions Reward point for user
     * @throws UUIDException
     * @throws UserNameNotFoundException
     */
    @GetMapping(value = "getAttractionRewardPoints")
    @ResponseStatus(HttpStatus.OK)
    public int getAttractionRewardPoints(@RequestParam @Valid String attractionId, @RequestParam @Valid String userName) throws UUIDException, UserNameNotFoundException {

        if (tourGuideService.getUser(userName) == null) {
            String message = " this username does not exist : " + userName;
            throw new UserNameNotFoundException(message);
        }
        try {
            UUID attractionIdUUID = UUID.fromString(attractionId);
            UUID userIdUUID = tourGuideService.getUser(userName).getUserId();
            return rewardCentral.getAttractionRewardPoints(attractionIdUUID, userIdUUID);
        } catch (Exception e){
            throw new UUIDException("UUID input error: attractionId = "+ attractionId + "  userName = " + userName + "  message :" + e.getMessage());
        }
    }

}
