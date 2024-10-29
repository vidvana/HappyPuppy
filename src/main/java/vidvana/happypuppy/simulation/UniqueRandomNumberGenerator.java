package vidvana.happypuppy.simulation;

import java.util.HashSet;
import java.util.Random;

public class UniqueRandomNumberGenerator {

    private static final Random rand = new Random();
    private HashSet<Long> previous_records = new HashSet<>();
    private final long max;
    private final int length;

    public UniqueRandomNumberGenerator(int length) {
        HashSet<Long> previous_records = new HashSet<>();
        max = (long) (Math.pow(10, length) - 1);
        this.length = length;
    }

    public String generate() {
        long num;
        int attempts = 0;
        do {
            num = rand.nextLong(max);
        } while (previous_records.contains(num) && attempts++ < 10);
        if (attempts >= 10) {
            num = (long) 0;
            while (previous_records.contains(num)) {
                num += 1;
            }
        }
        return String.format("%0"+length+"d", num);
        //return String.valueOf(num);
    }


}
