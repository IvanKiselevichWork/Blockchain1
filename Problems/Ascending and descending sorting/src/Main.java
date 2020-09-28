import java.util.Arrays;
import java.util.Scanner;
import java.util.function.BiFunction;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isAscending = scanner.nextLine().equals("ascending");
        int[] array = Arrays.stream(scanner.nextLine().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
        BiFunction<Integer, Integer, Integer> comparator = null;

        comparator = (a1, a2) -> isAscending ? a1 > a2 ? a2 : a1 : a1 > a2 ? a1 : a2;

        sort(array, comparator);
        Arrays.stream(array).forEach(e -> System.out.print(e + " "));
    }

    public static void sort(int[] array, BiFunction<Integer, Integer, Integer> comparator) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (comparator.apply(array[j], array[j + 1]) == array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
}