package samurai.util;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ArrayUtil {

    /**
     * This searches through an array of type T to find an object that matches V value using a value extractor and comparator for values. The array must have been sorted before hand by the same comparator as provided to this function. If there are elements with the same extracted value, a Predicate equals function may be provided to match a single value. Otherwise, null may be passes as equals and the first value found will be returned.
     *
     * @param array      of objects T
     * @param value      value to search for
     * @param extractor  Function that takes in object T and returns value V
     * @param comparator Comparator that compares V
     * @param equals     Predicate that may be null. Used to find a specific object if array is not unique
     * @param <T>
     * @param <V>
     * @return the index of object found
     */
    public static <T, V> int binarySearch(T[] array, V value, Function<T, V> extractor, Comparator<V> comparator, Predicate<T> equals) {
        final int idx = binarySearch(array, value, extractor, comparator);
        return equals == null ? idx : bidirectionalSearch(array, equals, idx, value, extractor, comparator);
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
