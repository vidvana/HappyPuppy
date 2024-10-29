package vidvana.happypuppy.simulation.entities;

import java.util.Random;

public enum Sex {
    MALE, FEMALE;

    private  static final Random RANDOM = new Random();

    public static Sex getRandom() {
        Sex[] sexes = values();
        return sexes[RANDOM.nextInt(sexes.length)];
    }
}
