package vidvana.happypuppy.simulation.entities.otherrecords;

import vidvana.happypuppy.simulation.entities.Entity;

import java.util.Date;

import static vidvana.happypuppy.simulation.Simulator.DATE_FORMAT;

public class Work implements Entity {

    private final int volunteerId;

    private final Date from;
    private final Date to;

    public Work(int volunteerId, Date lastEmployed, Date to) {
        this.volunteerId = volunteerId;
        from = lastEmployed;
        this.to = to;
    }

    @Override
    public String get_csv_line(int option) {
        return ("|"+volunteerId+"|"+
                DATE_FORMAT.format(from)+"|"+
                (to != null ? DATE_FORMAT.format(to) : "")
        );
    }
}
