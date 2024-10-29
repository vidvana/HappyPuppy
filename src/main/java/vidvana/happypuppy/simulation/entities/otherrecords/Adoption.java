package vidvana.happypuppy.simulation.entities.otherrecords;

import vidvana.happypuppy.simulation.Simulator;
import vidvana.happypuppy.simulation.UniqueRandomNumberGenerator;
import vidvana.happypuppy.simulation.entities.Entity;

import java.util.Date;

import static vidvana.happypuppy.simulation.Simulator.DATE_FORMAT;

public class Adoption implements Entity {

    private static final UniqueRandomNumberGenerator UNIQUE_RANDOM_NUMBER_GENERATOR = new UniqueRandomNumberGenerator(18);
    private final String certificateNr;

    private final int adoptionProcessID;

    private final Date adoptionDate;


    public Adoption(int adoptionProcessID) {
        certificateNr = UNIQUE_RANDOM_NUMBER_GENERATOR.generate();
        this.adoptionProcessID = adoptionProcessID;
        adoptionDate = new Date(Simulator.TODAY);
    }

    @Override
    public String get_csv_line(int option) {
        return ("|"+certificateNr+"|"+
                String.format("%02d", adoptionProcessID)+"|"+
                DATE_FORMAT.format(adoptionDate)
        );
    }
}
