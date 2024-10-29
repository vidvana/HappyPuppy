package vidvana.happypuppy.simulation.entities;

import java.util.Random;

public enum LivingConditions {
    INDOOR, OUTDOOR, ANY;

    private  static final Random RANDOM = new Random();

    public static LivingConditions getRandom() {
        LivingConditions[] places_to_live = values();
        return places_to_live[RANDOM.nextInt(places_to_live.length)];
    }
}
