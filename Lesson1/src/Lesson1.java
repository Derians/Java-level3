import java.util.ArrayList;
import java.util.Arrays;

/**
 * Java level 3, lesson 1
 *
 * 1. Написать метод, который меняет два элемента массива местами.(массив может быть любого ссылочного типа);
 * 2. Написать метод, который преобразует массив в ArrayList;
 * 3. Большая задача:
 *  a. Есть классы Fruit -> Apple, Orange;(больше фруктов не надо)
 *  b. Класс Box в который можно складывать фрукты, коробки условно сортируются по типу фрукта, поэтому в одну коробку
 *     нельзя сложить и яблоки, и апельсины;
 *  c. Для хранения фруктов внутри коробки можете использовать ArrayList;
 *  d. Сделать метод getWeight() который высчитывает вес коробки, зная количество фруктов и вес одного фрукта
 *     (вес яблока - 1.0f, апельсина - 1.5f, не важно в каких это единицах);
 *  e. Внутри класса коробка сделать метод compare, который позволяет сравнить текущую коробку с той, которую подадут в
 *     compare в качестве параметра, true - если их веса равны, false в противном случае(коробки с яблоками
 *     мы можем сравнивать с коробками с апельсинами);
 *  f. Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую коробку
 *     (помним про сортировку фруктов, нельзя яблоки высыпать в коробку с апельсинами), соответственно в текущей коробке
 *     фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;
 *  g. Не забываем про метод добавления фрукта в коробку.
 *
 * @author Chaykin Ivan
 * @version 10.10.2019
 */

public class Lesson1 {

    public static void main(String[] args) {

        String[] strings = {"Apple", "Orange", "Pear", "Grapes", "Watermelon"};
        Integer[] numbers = {1, 2, 3, 4, 5, 6};

        System.out.println(Arrays.toString(strings));
        swapArray(strings,0, 2);
        System.out.println(Arrays.toString(strings));

        System.out.println(Arrays.toString(numbers));
        swapArray(numbers,1, 3);
        System.out.println(Arrays.toString(numbers));

        System.out.println(strings.getClass());
        System.out.println(convertToList(strings).getClass());

        System.out.println(numbers.getClass());
        System.out.println(convertToList(numbers).getClass());

        Box<Apple> boxOfApples = new Box<>();
        Box<Apple> newBoxOfApples = new Box<>();
        Box<Orange> boxOfOrange = new Box<>();
        Box<Orange> newBoxOfOrange = new Box<>();

        for (int i = 0; i < 10; i++) {
            boxOfApples.addFruit(new Apple());
            boxOfOrange.addFruit(new Orange());
        }

        System.out.println("Weight of Apple box: " + boxOfApples.getWeight());
        System.out.println("Weight of Orange box: " + boxOfOrange.getWeight());
        System.out.println("Compare weight Orange and Orange: " + boxOfOrange.compare(boxOfOrange));
        System.out.println("Compare weight Orange and Apple: " + boxOfOrange.compare(boxOfApples));
        boxOfApples.changeBox(newBoxOfApples);
        System.out.println("Weight of Apple box: " + boxOfApples.getWeight());
        System.out.println("Weight of new Apple box: " + newBoxOfApples.getWeight());
        boxOfOrange.changeBox(newBoxOfOrange);
        System.out.println("Weight of Orange box: " + boxOfOrange.getWeight());
        System.out.println("Weight of new Orange box: " + newBoxOfOrange.getWeight());
    }

   private static <T> void swapArray(T[] array, int firstIndex, int secondIndex){
       T temp = array[firstIndex];
       array[firstIndex] = array[secondIndex];
       array[secondIndex] = temp;
    }

    private static <T> ArrayList<? extends T> convertToList(T[] array){
        return new ArrayList<>(Arrays.asList(array));
    }


}


