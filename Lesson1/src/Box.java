import java.util.ArrayList;

public class Box<T extends Fruit> {

    private ArrayList<T> fruits;

    public Box() {
        fruits = new ArrayList<>();
    }

    public void addFruit(T fruit){
        fruits.add(fruit);
    }

    public void changeBox(Box<T> box){
        box.fruits.addAll(fruits);
        fruits.clear();
    }

    public float getWeight() {
        float boxWeight = 0.0f;
        for (T fruit : fruits) {
            boxWeight += fruit.getWeight();
        }
        return boxWeight;
    }

    public boolean compare(Box<?> box){
        return (getWeight() == box.getWeight());
    }
}
