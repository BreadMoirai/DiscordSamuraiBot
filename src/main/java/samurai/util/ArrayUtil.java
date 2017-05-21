package samurai.util;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ArrayUtil {

    public static <T, V> int binarySearch(T[] array, V value, Function<T, V> extractor, Comparator<V> comparator, Predicate<T> equals) {
        final int idx = binarySearch(array, value, extractor, comparator);
        return bidirectionalSearch(array, equals, idx, value, extractor, comparator);
    }

    public static <T, V> int binarySearch(T[] array, V value, Function<T, V> extractor, Comparator<V> comparator) {
        int low = 0;
        int high = array.length - 1;
        while (low <= high) {
            int mid = low + ((high - low) / 2);
            final int compare = comparator.compare(extractor.apply(array[mid]), value);
            if (compare < 0)
                high = mid - 1;
            else if (compare > 0)
                low = mid + 1;
            else
                return mid;
        }
        return -1;
    }

    private static <T, V> int bidirectionalSearch(T[] array, Predicate<T> equals, int idx, V value, Function<T, V> extractor, Comparator<V> comparator) {
        if (equals.test(array[idx])) return idx;
        final int size = array.length;
        boolean left = true, right = true;
        for (int i = 1; i < Math.max(size - idx, idx); i++) {
            if (right) {
                final int iR = idx + i;
                if (iR < size) {
                    if (equals.test(array[iR])) return iR;
                    else if (comparator.compare(value, extractor.apply(array[iR])) != 0) right = false;
                } else right = false;
            }
            if (left) {
                final int iL = idx - i;
                if (iL >= 0) {
                    if (equals.test(array[iL])) return iL;
                    else if (comparator.compare(value, extractor.apply(array[iL])) != 0) left = false;
                } else left = false;
            }
            if (!left && !right) break;
        }
        return -1;
    }
}
