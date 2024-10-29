package vidvana.happypuppy.simulation.entities;

import java.util.Random;

public enum Species {
    CAT, DOG;

    private  static final Random RANDOM = new Random();

    public static Species getRandom() {
        Species[] species = values();
        return species[RANDOM.nextInt(species.length)];
    }
}
