package samurai.util;

import java.util.Comparator;
import java.util.function.Function;

public class ArrayUtil {

    public static <T, V> int binarySearch(T[] array, V value, Function<T, V> extractor, Comparator<V> comparator) {
        int low = 0;
        int high = array.length - 1;
        while (low <= high) {
            int mid = low + ((high - low) / 2);
            final int compare = comparator.compare(extractor.apply(array[mid]), value);
            if (compare > 0)
                high = mid - 1;
            else if (compare < 0)
                low = mid + 1;
            else
                return mid;
        }
        return -1;
    }
}
