import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class MinMax {
    public static <T> void findMinMax(
            Stream<? extends T> stream,
            Comparator<? super T> order,
            BiConsumer<? super T, ? super T> minMaxConsumer) {
        List<T> list = stream.collect(Collectors.toList());
        T min = list.stream().min(order).orElse(null);
        T max = list.stream().max(order).orElse(null);
        minMaxConsumer.accept(min, max);
    }
}