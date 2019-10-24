/**
 * @author Chaykin Ivan
 * @version 24.10.2019
 */
public abstract class Stage {
    protected int length;
    protected String description;
    public String getDescription() {
        return description;
    }
    public abstract void go(Car c);
}
