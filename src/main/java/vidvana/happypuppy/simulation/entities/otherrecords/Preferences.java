package vidvana.happypuppy.simulation.entities.otherrecords;

import vidvana.happypuppy.simulation.Simulator;
import vidvana.happypuppy.simulation.entities.Entity;
import vidvana.happypuppy.simulation.entities.LivingConditions;
import vidvana.happypuppy.simulation.entities.Species;

import java.util.Date;
import java.util.Random;

import static vidvana.happypuppy.simulation.Simulator.DATE_FORMAT;

public class Preferences implements Entity {
    private final int id;
    private final Species species;
    private static final Random RANDOM = new Random();
    private final int animalAgeMin;
    private final int animalAgeMax;
    private final boolean goodWithKids;
    private final LivingConditions housingConds;
    private final boolean disabilities;
    private final Date date;

    public Preferences(int id) {
        this.id = id;
        species = Species.getRandom();
        animalAgeMax = RANDOM.nextInt(20);
        animalAgeMin = (Math.max(RANDOM.nextInt(-40, 20), 0));
        goodWithKids = RANDOM.nextBoolean();
        housingConds = LivingConditions.getRandom();
        disabilities = RANDOM.nextBoolean();
        this.date = new Date(Simulator.TODAY);
    }

    @Override
    public String get_csv_line(int option) {
        return "|"+id+"|"+
                species+"|"+
                //animalAgeMin+"|"+
                animalAgeMax+"|"+
                (goodWithKids? "TAK" : "NIE")+"|"+
                housingConds+"|"+
                (disabilities? "TAK" : "NIE")+"|"+
                DATE_FORMAT.format(date);
    }
}
