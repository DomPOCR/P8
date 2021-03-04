package tourGuide.UT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import rewardCentral.RewardCentral;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RewardCentralControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected RewardCentral rewardCentral;

    @MockBean
    protected TourGuideService tourGuideService;

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
    }

    @Test
    public void getAttractionRewardPoints_CorrectUserAndAttraction() throws Exception {

        //GIVEN

        UUID attractionUUID = UUID.randomUUID();
        String attractionId = attractionUUID.toString();

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(user);

        //THEN
        mockMvc.perform(get("/getAttractionRewardPoints")
                .param("attractionId", attractionId)
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getAttractionRewardPoints_IncorrectUser() throws Exception {

        //GIVEN

        UUID attractionUUID = UUID.randomUUID();
        String attractionId = attractionUUID.toString();

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(null);

        //THEN
        mockMvc.perform(get("/getAttractionRewardPoints")
                .param("attractionId", attractionId)
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAttractionRewardPoints_IncorrectAttraction() throws Exception {

        //GIVEN

        String attractionId = "1234";

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        //WHEN
        Mockito.when(tourGuideService.getUser(anyString())).thenReturn(user);

        //THEN
        mockMvc.perform(get("/getAttractionRewardPoints")
                .param("attractionId", attractionId)
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}