package vidvana.happypuppy.simulation.entities.processes;

import vidvana.happypuppy.simulation.Simulator;
import vidvana.happypuppy.simulation.entities.Entity;
import vidvana.happypuppy.simulation.entities.organisms.Animal;
import vidvana.happypuppy.simulation.entities.organisms.Person;
import vidvana.happypuppy.simulation.entities.otherrecords.Adoption;
import vidvana.happypuppy.simulation.entities.otherrecords.Presentation;
import vidvana.happypuppy.simulation.entities.otherrecords.Walk;
import vidvana.happypuppy.simulation.entities.otherrecords.Work;

import java.util.Date;
import java.util.Random;

import static vidvana.happypuppy.simulation.Simulator.DATE_FORMAT;
import static vidvana.happypuppy.simulation.Simulator.TODAY;

public class AdoptionProcess implements Entity {

    private static int id_count = 0;
    private final int id;
    private final String paPesel;
    private final int animalID;
    private final Animal animal;
    private String volunteerPesel;
    private Person volunteer;
    private static final Random RANDOM = new Random();
    private final Date from;
    private Date to;
    private int walks = 0;
    private final Simulator sim;
    private final Person pa;

    public AdoptionProcess(Person pa, Simulator sim) {
        id = id_count++;
        this.pa = pa;
        this.paPesel = pa.getPesel();
        this.sim = sim;
        animal = sim.getAnimalForAdoption(this);
        animalID = animal.getID();
        volunteer = sim.getVolunteer(this, pa);
        volunteerPesel = volunteer.getPesel();
        from = new Date(Simulator.TODAY);
    }

    public void action() {
        if (RANDOM.nextDouble(1.0) <= 0.01) {
            sim.writeToCSV("walks_DB", new Walk(this.id).get_csv_line(0));
            walks++;
        } else if (RANDOM.nextDouble(1.0) <= 0.01) {
            sim.writeToCSV("presentations_DB", new Presentation(this.id).get_csv_line(0));
        } else if  ((RANDOM.nextDouble(1.0) <= 0.1 && walks >= 2) ||
                (RANDOM.nextDouble(1.0) <= 0.2 && walks >= 3) ||
                (RANDOM.nextDouble(1.0) <= 0.4 && walks >= 4) ||
                (RANDOM.nextDouble(1.0) <= 0.8 && walks >= 5)
        ) {
            animal.getAdopted();
            pa.adopted();
            sim.writeToCSV("adoptions_DB", new Adoption(this.id).get_csv_line(0));
        } else if (RANDOM.nextDouble(1.0) <= 0.05)  {
            this.finish();
        }

    }

    public boolean isFinished() {
        return (to != null);
    }

    public void finish() {
        to = new Date(TODAY);
        sim.writeToCSV("adoption_processes_DB", this.get_csv_line(0));
    }

    public void findNewVolunteer(Person p) {
        volunteer = sim.getVolunteer(this, p);
        volunteerPesel = volunteer.getPesel();
        sim.writeToCSV("adoption_processes_DB", this.get_csv_line(0));
    };

    @Override
    public String get_csv_line(int option) {
        return (id+"|"+
                paPesel+"|"+
                animalID+"|"+
                volunteerPesel+"|"+
                DATE_FORMAT.format(from)+"|"+
                (to == null? "" : DATE_FORMAT.format(to)));
    }
}
