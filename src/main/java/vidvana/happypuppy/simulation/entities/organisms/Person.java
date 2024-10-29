package vidvana.happypuppy.simulation.entities.organisms;

import vidvana.happypuppy.simulation.Between;
import vidvana.happypuppy.simulation.PeselGenerator;
import vidvana.happypuppy.simulation.RandomFileLineChooser;
import vidvana.happypuppy.simulation.Simulator;
import vidvana.happypuppy.simulation.entities.Entity;
import vidvana.happypuppy.simulation.entities.Sex;
import vidvana.happypuppy.simulation.entities.otherrecords.Preferences;
import vidvana.happypuppy.simulation.entities.otherrecords.Work;
import vidvana.happypuppy.simulation.entities.processes.AdoptionProcess;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static vidvana.happypuppy.simulation.Simulator.DATE_FORMAT;
import static vidvana.happypuppy.simulation.Simulator.TODAY;

public class Person implements Entity {

    public int volunteerId;
    public static int volunteerIdCount = 1;
    public int paId;
    public static int paIdCount = 1;
    public static final PeselGenerator pg = new PeselGenerator();
    private final String pesel;
    private static final Random RANDOM = new Random();
    private static final RandomFileLineChooser MALE_NAME_CHOOSER = new RandomFileLineChooser("src/main/resources/strings_for_random_gens/human_male_names.txt");
    private static final RandomFileLineChooser FEMALE_NAME_CHOOSER = new RandomFileLineChooser("src/main/resources/strings_for_random_gens/human_female_names.txt");
    private static final RandomFileLineChooser SURNAME_CHOOSER = new RandomFileLineChooser("src/main/resources/strings_for_random_gens/human_surnames.txt");
    private final String name;
    private final String surname;

    private final Date born;

    private boolean wasAnOwner;
    private boolean worksCurrently;
    private Date lastHired = null;
    private final Simulator sim;
    private boolean everHired = false;
    private boolean everTookPartInAdoptionProcess = false;

    private final ArrayList<AdoptionProcess> currentAdoptionProcesses = new ArrayList<>();

    public Person(Simulator sim){
        this.sim = sim;
        Date eighteenYearsAgo = new Date(Simulator.TODAY - Simulator.A_DAY * 365 * 18);
        Date eightyYearsAgo = new Date(Simulator.TODAY - Simulator.A_DAY * 365 * 80);
        born = Between.between(eightyYearsAgo, eighteenYearsAgo);

        if (RANDOM.nextBoolean()) {
            name = MALE_NAME_CHOOSER.choose1();
            pesel = pg.generate(born, Sex.MALE);
        } else {
            name = FEMALE_NAME_CHOOSER.choose1();
            pesel = pg.generate(born, Sex.FEMALE);
        }
        surname = SURNAME_CHOOSER.choose1();
        wasAnOwner = RANDOM.nextBoolean();
    }

    public String getPesel() {
        return pesel;
    }

    public void adopted() {
        System.out.println("ADOPTION!!!");
        if (wasAnOwner == false) {
            wasAnOwner = true;
            if (everHired) {
                sim.writeToCSV("volunteers_EXCEL", this.get_csv_line(1));
            }
            sim.writeToCSV("PAs_DB", this.get_csv_line(2));
        }
    }

    public void startAdoptionProcess(AdoptionProcess ap) {
        currentAdoptionProcesses.add(ap);
    }

    public void gotHired() {
        worksCurrently = true;
        volunteerId = volunteerIdCount++;
        lastHired = new Date(Simulator.TODAY);
        if (!everHired) {
            sim.writeToCSV("volunteers_EXCEL", this.get_csv_line(1));
            sim.writeToCSV("volunteers_DB", this.get_csv_line(0));
            sim.writeToCSV("works_DB", new Work(this.volunteerId, this.lastHired, null).get_csv_line(0));
        }
        everHired = true;
    }

    public void leftJob() {
        worksCurrently = false;
        for (AdoptionProcess ap: currentAdoptionProcesses) {
            ap.findNewVolunteer(this);
        }
        currentAdoptionProcesses.clear();
        sim.writeToCSV("works_DB", new Work(this.volunteerId, this.lastHired, new Date(Simulator.TODAY)).get_csv_line(0));
    }

    public void beganAdoptionProcess() {
        if (!everTookPartInAdoptionProcess) {
            paId = paIdCount++;
            everTookPartInAdoptionProcess = true;
            sim.writeToCSV("PAs_DB", this.get_csv_line(2));
        }
        sim.writeToCSV("preferences_DB", new Preferences(this.paId).get_csv_line(0));
    }

    public void actionVolunteers() {
        if (RANDOM.nextDouble(1.0) <= 0.05 && sim.getVolunteersSize() > Simulator.MIN_VOLUNTEERS && this.worksCurrently) {
            leftJob();
        } else if (RANDOM.nextDouble(1.0) <= 0.0175) {
            gotHired();
        }
    }

    public void actionAdopt() {
        if (RANDOM.nextDouble(1.0) <= 0.10 && sim.getAnimalsAvailableSize() > 0) {
            this.beganAdoptionProcess();
            sim.addNewAdoptionProcess(this);
        }
    }

    public boolean worksCurrently() {
        return worksCurrently;
    }

    @Override
    public String get_csv_line(int option) {
        if (option == 0) {
            // Volunteer DB
            return (volunteerId+"|"+
                            pesel+"|"+
                            name+"|"+
                            surname
            );
        } else if (option == 1) {
            // Volunteer EXCEL
            return (
                    pesel+"|"+
                            name+"|"+
                            surname+"|"+
                            DATE_FORMAT.format(born)+"|"+
                            (wasAnOwner?"TAK":"NIE")
            );
        } else {
            // PA
            return (
                    "|"+pesel+"|"+
                            name+"|"+
                            surname+"|"+
                            (wasAnOwner?"TAK":"NIE")
            );
        }
    }
}

// TODO: zmień "is an owner" w wolontariuszu na "was and owner" (bo potrzebujemy informacji, czy osoba ma obecnie doświadczenie ze zwierzętami