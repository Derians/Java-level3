import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Chaykin Ivan
 * @version 24.10.2019
 */
public class Race {
    private ArrayList<Stage> stages;
    public ArrayList<Stage> getStages() { return stages; }
    public Race(Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));
    }
}
