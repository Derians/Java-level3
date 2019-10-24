
/**
 * 1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз (порядок – ABСABСABС).
 *    Используйте wait/notify/notifyAll.
 *
 * @author Chaykin Ivan
 * @version 20.10.2019
 */

public class Lesson4 {

    public static void main(String[] args) {

        PrintChars printChars = new PrintChars();

        new Thread(printChars::printA).start();
        new Thread(printChars::printB).start();
        new Thread(printChars::printC).start();
    }
}
