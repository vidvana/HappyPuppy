package vidvana.happypuppy.simulation.entities.otherrecords;

import vidvana.happypuppy.simulation.RandomFileLineChooser;
import vidvana.happypuppy.simulation.Simulator;
import vidvana.happypuppy.simulation.entities.Entity;

import java.util.Date;

import static vidvana.happypuppy.simulation.Simulator.DATE_FORMAT;

public class Walk implements Entity {

    private final Date date;
    private static final RandomFileLineChooser RANDOM_FILE_LINE_CHOOSER = new RandomFileLineChooser("src/main/resources/strings_for_random_gens/observations.txt");
    private final String observations;
    private final int adProcId;

    public Walk(int adProcId) {
        this.date = new Date(Simulator.TODAY);
        observations = RANDOM_FILE_LINE_CHOOSER.choose1();
        this.adProcId = adProcId;
    }

    @Override
    public String get_csv_line(int option) {
        return ("|"+adProcId+"|"+
                DATE_FORMAT.format(date)+"|"+
                observations);
    }

}
