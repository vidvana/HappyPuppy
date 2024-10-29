package vidvana.happypuppy.simulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.max;

public class RandomFileLineChooser {

    private String[] array;
    private static final Random RANDOM = new Random();

    public RandomFileLineChooser(String path) {
        ArrayList<String> strings = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            int lines = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines++;
                strings.add(line);
            }
            reader.close();
            array = new String[lines];
            for (int i = 0; i < lines; i++) {
                array[i] = "1";
                array[i] = strings.get(i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String choose1() {
        return array[RANDOM.nextInt(array.length)];
    };

    public String choose_many(int n, String separator) {
        Set<String> phrases = new HashSet<>();
        int x = Math.min(n, array.length);
        while(phrases.size() < x) {
            phrases.add(array[RANDOM.nextInt(array.length)]);
        }
        StringBuilder s = new StringBuilder();
        boolean a = true;
        for (String phrase : phrases) {
            if (a) {
                s.append(phrase);
                a = false;
            } else {
                s.append(separator).append(phrase);
            }
        }
        System.out.println(s.toString());
        return s.toString();
    }
}