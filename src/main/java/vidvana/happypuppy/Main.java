package vidvana.happypuppy;

import vidvana.happypuppy.simulation.CSVWriter;
import vidvana.happypuppy.simulation.Simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;

public class Main {


    public static void main(String[] args) {
        Simulator sim = new Simulator("T1_");
        sim.simulate(150, "T1_");
        sim.simulate(50, "T2_");

        /*try {
            Files.copy(Path.of("output/T1_animals_DB.csv"), Path.of("output/kupa.csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        //sim.simulate(100, "T3_");
        System.out.println("THE END");
    }
}