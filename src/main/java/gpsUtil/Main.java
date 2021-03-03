package gpsUtil;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        for(int i = 0; i < 100; ++i) {
            double randomLat = ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D);
            randomLat = Double.parseDouble(String.format("%.6f", randomLat));
            double randomLong = ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D);
            randomLong = Double.parseDouble(String.format("%.6f", randomLong));
            System.out.println(randomLat);
            System.out.println(randomLong);
        }

    }
}