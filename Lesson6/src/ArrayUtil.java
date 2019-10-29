import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Chaykin Ivan
 * @version 27.10.2019
 */
public class ArrayUtil {

    public int[] extractFromArray(int[] array, int key){
        int index = -1;
        if (array.length > 1){
            for (int i = 0; i < array.length; i++) {
                if (array[i] == key)
                    index = i;
            }
            if (index >= 0) {
                return Arrays.copyOfRange(array, index+1, array.length);
            } else {
                throw new RuntimeException();
            }
        }
        return null;
    }

    public boolean validateArray(int[] array, int key1, int key2) {
        return IntStream.of(array).anyMatch(x -> x == key1)
                && IntStream.of(array).anyMatch(x -> x == key2);
    }
}
