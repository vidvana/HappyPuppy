package vidvana.happypuppy.simulation;

import vidvana.happypuppy.simulation.entities.Sex;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;


public class PeselGenerator {
    private static final Random RANDOM = new Random();
    private final HashSet<String> previous_records = new HashSet<>();

    public String generate(Date birthdate, Sex sex) {
        int m = birthdate.getMonth();
        int y = birthdate.getYear();
        if (y <= 1899) {
            m += 80;
        } else if (y > 1999) {
            m += 20;
        };
        y = y % 100;
        int d = birthdate.getDay();

        int num;
        int attempts = 0;
        String pesel;
        do {
            num = 2 * RANDOM.nextInt(0,4999);
            if (sex == Sex.MALE) {
                num += 1;
            }
            pesel = assemblePesel(y, m, d, num);
        } while (previous_records.contains(pesel) && attempts++ < 10);

        if (attempts >= 5) {
            num = 0;
            if (sex == Sex.MALE) {
                num += 1;
            }
            pesel = assemblePesel(y, m, d, num);
            while (previous_records.contains(pesel)) {
                num += 2;
                pesel = assemblePesel(y, m, d, num);
            }
        }
        previous_records.add(pesel);
        return pesel;
    }

    String assemblePesel(int y, int m, int d, int num) {
        return String.format("%02d", y)+""+
                String.format("%02d", m)+""+
                String.format("%02d", d)+""+
                String.format("%04d", num)+""+
                String.format("%01d", controlSum(y, m, d, num));
    }

    int controlSum(int y, int m, int d, int num) {
        return (
                (y / 10) +
                        3 * (y % 10) +
                        7 * (m/10) +
                        9 * (m%10) +
                        (d/10) +
                        3 * (d%10) +
                        7 * (num / 1000) +
                        9 * ((num / 100) % 10) +
                        ((num / 10) % 10) +
                        3 * (num % 10)) % 10;
    }
}
