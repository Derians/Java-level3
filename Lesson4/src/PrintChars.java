
/**
 * @author Chaykin Ivan
 * @version 20.10.2019
 */

public class PrintChars {

    public volatile char currentChar = 'A';

    public synchronized void printA() {
        try {
            for (int i = 0; i < 5; i++) {
                while (currentChar != 'A') {
                    this.wait();
                }
                System.out.print('A');
                currentChar = 'B';
                this.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printB() {
        try {
            for (int i = 0; i < 5; i++) {
                while (currentChar != 'B') {
                    this.wait();
                }
                System.out.print('B');
                currentChar = 'C';
                this.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printC() {
        try {
            for (int i = 0; i < 5; i++) {
                while (currentChar != 'C') {
                    this.wait();
                }
                System.out.print('C');
                currentChar = 'A';
                this.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
