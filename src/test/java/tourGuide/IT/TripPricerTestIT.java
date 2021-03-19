package tourGuide.IT;

import org.junit.jupiter.api.Test;
import tripPricer.TripPricer;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TripPricerTestIT {

    @Test
    void getPrice() {

        TripPricer tripPricer = new TripPricer();

        //GIVEN
        String apiKey="test-server-api-key";
        UUID attractionId=UUID.randomUUID();
        int adults=2;
        int children=3;
        int nightsStay=4;
        int rewardsPoints=5;

        //WHEN THEN
        assertTrue(tripPricer.getPrice(apiKey,attractionId,adults,children,nightsStay,rewardsPoints).size()>0);
    }

    @Test
    void getProviderName() {

        //GIVEN
        TripPricer tripPricer = new TripPricer();

        String apiKey = "test-server-api-key";
        int adults = 2;

        //WHEN THEN
        assertFalse(tripPricer.getProviderName(apiKey, adults).isEmpty());
    }
}