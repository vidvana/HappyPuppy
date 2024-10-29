package vidvana.happypuppy.simulation;

import vidvana.happypuppy.simulation.entities.organisms.Animal;
import vidvana.happypuppy.simulation.entities.organisms.Person;
import vidvana.happypuppy.simulation.entities.processes.AdoptionProcess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Simulator {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static long A_DAY = TimeUnit.DAYS.toMillis(1);
    public static long TODAY = new Date().getTime() - 1000 * A_DAY;
    public static int MIN_VOLUNTEERS = 3;

    private final ArrayList<Animal> animals = new ArrayList<>();
    private final ArrayList<Animal> availableAnimals = new ArrayList<>();
    private final ArrayList<Person> people = new ArrayList<>();
    private final ArrayList<Person> availableVolunteers = new ArrayList<>();

    private final ArrayList<AdoptionProcess> adoptionProcesses = new ArrayList<>();
    // only the available are stored, bc unlike animals and ppl, once "dead" they cannot return

    private final ArrayList<String> filenamePrefixes = new ArrayList<>();
    private final Map<String, CSVWriter> csvWriters = new HashMap<>();
    private boolean simulateT1 = true;

    private final static Random RANDOM = new Random();
    String[] filenames = {
            "adoption_processes_DB",
            "adoptions_DB",
            "animals_DB",
            "animals_EXCEL",
            "PAs_DB",
            "preferences_DB",
            "presentations_DB",
            "volunteers_DB",
            "volunteers_EXCEL",
            "walks_DB",
            "works_DB"
    };

    public Simulator(String prefix) {
        for (String s : filenames) {
            csvWriters.put(s, new CSVWriter(s, "T1_", "T2_", true,
                    (s.endsWith("EXCEL") ?
                            (s.startsWith("animals")?
                                    "ID|Name|Species|Sex|Date of birth|Disabilities|House without kids|Experienced owners|Preferred place to live|Date of admission|Death"
                                    : "PESEL|Volunteer's name|Volunteer's surname|Date of birth|Has experience" )
                            : null))
            );
            filenamePrefixes.add(prefix);
        }
        for (int i = 0; i < 10; i++) {
            Animal a = new Animal(this);
            a.getAdmitted();
            animals.add(a); // <- this should write to file ANIMALS_EXCEL (DB is only for those in the process)
        }
        for (int i = 0; i < MIN_VOLUNTEERS; i++) {
            Person p = new Person(this);
            p.gotHired(); // <-this should write to file VOLUNTEERS_EXCEL (if hasn't written yet after changing is_owner), VOLUNTEERS_DB (if hasn't written yet), and WORKS
            people.add(p);
        }
    }

    public void simulate(int days, String prefix) {
        if (!simulateT1) {
            csvWriters.clear();
            for (String s : filenames) {
                csvWriters.put(s, new CSVWriter(
                        s,
                        "T2_",
                        true,
                        null
                        ));
                filenamePrefixes.add(prefix);
            }
        } else {
            simulateT1 = false;
        }
        filenamePrefixes.add(prefix);
        for (int d = 0; d < days; d++) {
            availableAnimals.clear();
            int newAnimals = RANDOM.nextInt(-5, 5);
            while (newAnimals-- > 0) {
                Animal a = new Animal(this);
                a.getAdmitted();
                animals.add(a);
            }
            for (Animal a: animals) {
                a.action(); // <-this should write to ANIMALS_EXCEL (if the animal's state changes)
                if (a.score() < Animal.DEATH_SCORE) {
                    availableAnimals.add(a);
                }
            }
            availableAnimals.sort(Comparator.comparing(Animal::score));
            for (Person p: people) {
                p.actionVolunteers(); // <- ADD NEW adproc, write to WORKS if hired/fired to VOLUNTEERS_EXCEL if hired for the first time (pa will be handled by adoptionProcess)
            }

            int newVolunteers = RANDOM.nextInt(-10, 1);
            while (newVolunteers-- > 0) {
                Person p = new Person(this);
                p.gotHired(); // <- write to WORKS if hired/fired to VOLUNTEERS_EXCEL if hired for the first time
                people.add(p);
            }
            availableVolunteers.clear();
            for (Person p: people) {
                if (p.worksCurrently()) {
                    availableVolunteers.add(p);
                }
            }

            for (Person p: people) {
                p.actionAdopt(); // <- ADD NEW adproc, write to WORKS if hired/fired to VOLUNTEERS_EXCEL if hired for the first time (pa will be handled by adoptionProcess)
            }
            int newPAs = RANDOM.nextInt(-2, 7);
            while (newPAs-- > 0) {
                Person p = new Person(this);
                p.beganAdoptionProcess(); // <- ADD NEW adproc
                people.add(p);
            }
            for (AdoptionProcess ap : adoptionProcesses) {
                ap.action(); // <- write to WALK, PRES, ADOPTION, AP, QUESTIONAIRE, ANIMALS_DB (the ones that were never in ap are stored in EXCEL) if any of that happens;
            }
            adoptionProcesses.removeIf(AdoptionProcess::isFinished);
            TODAY = TODAY + A_DAY;
        }
        for (String key : csvWriters.keySet()) {
            csvWriters.get(key).flush();
        }
    }

    public  Animal getAnimalForAdoption(AdoptionProcess ap) {
        Animal a = availableAnimals.get(RANDOM.nextInt(availableAnimals.size()));
        a.startAdoptionProcess(ap);
        return a;
    }

    public Person getVolunteer(AdoptionProcess ap, Person p) {
        Person v;
        do {
            v = availableVolunteers.get(RANDOM.nextInt(availableVolunteers.size()));
        } while (v==p);
        v.startAdoptionProcess(ap);
        return v;
    }

    public void addNewAdoptionProcess(Person pa) {
        this.adoptionProcesses.add(new AdoptionProcess(pa, this));
    }

    public int getVolunteersSize() {
        return availableVolunteers.size();
    }

    public int getAnimalsAvailableSize() {
        return availableAnimals.size();
    }

    public void writeToCSV(String name, String line) {
        csvWriters.get(name).addRecord(line);
    }
}
