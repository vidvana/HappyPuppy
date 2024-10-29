package vidvana.happypuppy.simulation.entities.organisms;

import vidvana.happypuppy.simulation.Between;
import vidvana.happypuppy.simulation.RandomFileLineChooser;
import vidvana.happypuppy.simulation.Simulator;
import vidvana.happypuppy.simulation.UniqueRandomNumberGenerator;
import vidvana.happypuppy.simulation.entities.Entity;
import vidvana.happypuppy.simulation.entities.LivingConditions;
import vidvana.happypuppy.simulation.entities.Sex;
import vidvana.happypuppy.simulation.entities.Species;
import vidvana.happypuppy.simulation.entities.processes.AdoptionProcess;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import static vidvana.happypuppy.simulation.Simulator.DATE_FORMAT;

public class Animal implements Entity {
    
    private static int id_count = 0;
    private final int id;

    private static final UniqueRandomNumberGenerator UNIQUE_RANDOM_NUMBER_GENERATOR = new UniqueRandomNumberGenerator(15);
    private final String chip_number;

    private static final RandomFileLineChooser FEMALE_NAME_CHOOSER = new RandomFileLineChooser("src/main/resources/strings_for_random_gens/animal_names_female.txt");
    private static final RandomFileLineChooser MALE_NAME_CHOOSER= new RandomFileLineChooser("src/main/resources/strings_for_random_gens/animal_names_male.txt");
    private final String name;

    private final Species species;
    private final Sex sex;

    private final Date born;

    private final boolean house_without_kids;
    private final boolean experienced_owners_only;
    private final LivingConditions livingConditions;

    // changeable fields
    private static final RandomFileLineChooser FELINE_DISABILITIES_CHOOSER = new RandomFileLineChooser("src/main/resources/strings_for_random_gens/feline_diseases.txt");
    private static final RandomFileLineChooser CANINE_DISABILITIES_CHOOSER = new RandomFileLineChooser("src/main/resources/strings_for_random_gens/canine_diseases.txt");
    private static final Random RANDOM = new Random();
    private final HashSet<String> disabilities = new HashSet<>();
    private boolean adopted = false;
    private boolean dead = false;
    private Date mostRecentAdmissionDate;
    private Date death;

    private final ArrayList<AdoptionProcess> currentAdoptionProcesses = new ArrayList<>();

    public static final int DEATH_SCORE = 1000000;
    private boolean inDB = false;

    private final Simulator sim;

    // we're gonna write this as a response for an event
    //private Date death;

    public Animal(Simulator sim) {
        this.sim = sim;
        id = id_count++;
        chip_number = UNIQUE_RANDOM_NUMBER_GENERATOR .generate();
        sex = Sex.getRandom();
        if (sex == Sex.FEMALE) {
            name = FEMALE_NAME_CHOOSER.choose1();
        } else {
            name = MALE_NAME_CHOOSER.choose1();
        }
        species = Species.getRandom();
        int numOfDis = RANDOM.nextInt(0, 4);
        if (species == Species.CAT) {
            for (int i = 0; i<numOfDis; i++) {
                disabilities.add(FELINE_DISABILITIES_CHOOSER.choose1());
            }
        } else {
            for (int i = 0; i<numOfDis; i++) {
                disabilities.add(CANINE_DISABILITIES_CHOOSER.choose1());
            }
        }
        Date fifteenYearsAgo = new Date(Simulator.TODAY - Simulator.A_DAY * 365 * 15);
        Date today = new Date(Simulator.TODAY);
        born = Between.between(fifteenYearsAgo, today);
        house_without_kids = RANDOM.nextBoolean();
        experienced_owners_only = RANDOM.nextBoolean();
        livingConditions = LivingConditions.getRandom();
    }

    public String get_csv_line(int option) {
        if (option == 0) {
            return (id+"|"+
                    chip_number+"|"+
                    name
            );
        } else {
            return (
                    id+"|"+
                            name+"|"+
                            species+"|"+
                            sex+"|"+
                            DATE_FORMAT.format(born)+"|"+
                            disabilities+"|"+
                            (house_without_kids?1:0)+"|"+
                            (experienced_owners_only?1:0)+"|"+
                            livingConditions+"|"+
                            DATE_FORMAT.format(mostRecentAdmissionDate)+"|"+
                            (!dead ? "" : DATE_FORMAT.format(death))
            );
        }
    }

    public void die() {
        dead = true;
        death = new Date(Simulator.TODAY);
        for (AdoptionProcess ap: currentAdoptionProcesses) {
            ap.finish();
        }
        currentAdoptionProcesses.clear();
        sim.writeToCSV("animals_EXCEL", this.get_csv_line(1));
    }

    public void getAdmitted() {
        adopted = false;
        mostRecentAdmissionDate = new Date(Simulator.TODAY);
        sim.writeToCSV("animals_EXCEL", this.get_csv_line(1));
        if (!inDB) {
            sim.writeToCSV("animals_DB", this.get_csv_line(0));
        }
        inDB = true;
    }

    public void startAdoptionProcess(AdoptionProcess ap) {
        currentAdoptionProcesses.add(ap);
    }

    public void getAdopted() {
        adopted = true;
        for (AdoptionProcess ap: currentAdoptionProcesses) {
            ap.finish();
        }
        currentAdoptionProcesses.clear();
    }

    public void action() {
        if (! dead) {
            if (!adopted) {
                if (RANDOM.nextDouble(1.0) <= 0.005 && disabilities.size() == 1) {
                    die();
                } else if (RANDOM.nextDouble(1.0) < 0.01 && disabilities.size() == 2) {
                    die();
                } else if ((RANDOM.nextDouble(1.0) < 0.02 && disabilities.size() == 3) || age() > 15) {
                    die();
                } else if (age() > 20) {
                    die();
                }
                if (RANDOM.nextDouble(1.0) <= 0.005) {
                    int len = disabilities.size();
                    if (species == Species.CAT) {
                        disabilities.add(FELINE_DISABILITIES_CHOOSER.choose1());
                    } else {
                        disabilities.add(CANINE_DISABILITIES_CHOOSER.choose1());
                    }
                    if (len < disabilities.size()) {
                        sim.writeToCSV("animals_EXCEL", this.get_csv_line(1));
                    }
                }
            } else {
                if (RANDOM.nextDouble(1.0) <= 0.005) {
                    this.getAdmitted();
                }
            }
        }
    }

    public int age() {
        return (int)(((Simulator.TODAY - born.getTime()) / (1000 * 60 * 60 * 24)) / 365.25);
    }

    public int score() { return this.age() + disabilities.size() * 3 + (dead? DEATH_SCORE : 0); };

    public int getID() {
        return id;
    }

    public boolean isInDB() {
        return inDB;
    }
}

