import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Организуем гонки:
 * Все участники должны стартовать одновременно, несмотря на то, что на подготовку
 *    у каждого из них уходит разное время.
 * В туннель не может заехать одновременно больше половины участников (условность).
 * Попробуйте всё это синхронизировать.
 * Только после того как все завершат гонку, нужно выдать объявление об окончании.
 * Можете корректировать классы (в т.ч. конструктор машин) и добавлять объекты
 *    классов из пакета util.concurrent.
 *
 * @author Chaykin Ivan
 * @version 24.10.2019
 */
public class MainClass {
    public static final int CARS_COUNT = 4;
    public static final CyclicBarrier cyclicBarrier = new CyclicBarrier(CARS_COUNT + 1);
    public static final AtomicInteger finishCars = new AtomicInteger(0);

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), cyclicBarrier, finishCars);
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }

        cyclicBarrier.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        cyclicBarrier.await();
        cyclicBarrier.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}

