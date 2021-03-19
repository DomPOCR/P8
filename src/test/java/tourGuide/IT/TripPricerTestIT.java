package tourGuide.IT;

import org.junit.jupiter.api.Test;
import tripPricer.TripPricer;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TripPricerTestIT {

    @Test
    void getPrice() {

        //Initialize required instances and variables

        TripPricer tripPricer = new TripPricer();

        String apiKey="test-server-api-key";
        UUID attractionId=UUID.randomUUID();
        int adults=2;
        int children=3;
        int nightsStay=4;
        int rewardsPoints=5;

        //Assert
        assertTrue(tripPricer.getPrice(apiKey,attractionId,adults,children,nightsStay,rewardsPoints).size()>0);
    }

    @Test
    void getProviderName() {

        //Initialize required instances and variables
        TripPricer tripPricer = new TripPricer();

        String apiKey = "test-server-api-key";
        int adults = 2;

        //Assert
        assertFalse(tripPricer.getProviderName(apiKey, adults).isEmpty());
    }
}