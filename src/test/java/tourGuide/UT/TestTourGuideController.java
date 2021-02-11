package tourGuide.UT;

import com.fasterxml.jackson.databind.ObjectMapper;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserPreferencesDTO;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestTourGuideController {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected TourGuideService tourGuideService;

    // Données de test

    int attractionProximity = 1;
    private String currency = "USD";
    private Double lowerPricePoint = 0d;
    private Double highPricePoint = 7000d;
    private int tripDuration = 3;
    private int ticketQuantity = 4;
    private int numberOfAdults = 5;
    private int numberOfChildren = 6;
    private int numberOfProposalAttraction = 7;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void init() {

        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

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
        date.setTime(System.currentTimeMillis());

        VisitedLocation visitedLocationMock = new VisitedLocation(user.getUserId(), locationMock, date);

        //WHEN
        Mockito.when(tourGuideService.getUserLocation(tourGuideService.getUser(anyString()))).thenReturn(visitedLocationMock);


        //THEN
        mockMvc.perform(get("/getLocation")
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getUserPreferenceWithExistingUserTest() throws Exception {

        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(attractionProximity, currency,
                lowerPricePoint, highPricePoint,
                tripDuration, ticketQuantity,
                numberOfAdults, numberOfChildren, numberOfProposalAttraction);

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(user);
        Mockito.when(tourGuideService.getUserPreference(anyString())).thenReturn(userPreferencesDTO);

        //THEN
        mockMvc.perform(get("/getUserPreference")
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getUserPreferenceWithNonExistingUserTest() throws Exception {

        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(attractionProximity, currency,
                lowerPricePoint, highPricePoint,
                tripDuration, ticketQuantity,
                numberOfAdults, numberOfChildren, numberOfProposalAttraction);

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(null);
        Mockito.when(tourGuideService.getUserPreference(anyString())).thenReturn(userPreferencesDTO);

        //THEN
        mockMvc.perform(get("/getUserPreference")
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void setUserPreferenceTest() throws Exception {

        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(user);

        //THEN
        mockMvc.perform(post("/setUserPreference")
                .param("userName", user.getUserName())
                .content(asJsonString(new UserPreferencesDTO(
                        attractionProximity, currency,
                        lowerPricePoint, highPricePoint,
                        tripDuration, ticketQuantity,
                        numberOfAdults, numberOfChildren,
                        numberOfProposalAttraction)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void setUserPreferenceWithEmptyUserPreferencesTest() throws Exception {

        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(user);
        Mockito.when(tourGuideService.getUserPreference(anyString())).thenReturn(null);

        //THEN
        mockMvc.perform(post("/setUserPreference")
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void setUserPreferenceWithUnknownUser() throws Exception {

        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO(attractionProximity, currency,
                lowerPricePoint, highPricePoint,
                tripDuration, ticketQuantity,
                numberOfAdults, numberOfChildren, numberOfProposalAttraction);

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(null);
        Mockito.when(tourGuideService.getUserPreference(anyString())).thenReturn(userPreferencesDTO);

        //THEN
        mockMvc.perform(post("/setUserPreference")
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}