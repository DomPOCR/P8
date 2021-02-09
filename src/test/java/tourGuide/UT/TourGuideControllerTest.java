package tourGuide.UT;

import gpsUtil.GpsUtil;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TourGuideControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected TourGuideService tourGuideService;

    @BeforeEach
    void init() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
        tourGuideService.tracker.stopTracking();
    }

    @Test
    public void getIndexControllerTest() throws Exception {

        //GIVEN
        //WHEN //THEN return Index page
        mockMvc.perform(get("/")
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getUserLocationTest() throws Exception {

        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Location locationMock = new Location(1.0d, 1.0d);
        Date date = new Date();
        date.setTime( System.currentTimeMillis());

        VisitedLocation visitedLocationMock = new VisitedLocation(user.getUserId(),locationMock,date);

        //WHEN
        Mockito.when( tourGuideService.getUserLocation(tourGuideService.getUser(anyString()))).thenReturn(visitedLocationMock);

        //THEN
        this.mockMvc.perform(get("/getLocation")
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }


}
