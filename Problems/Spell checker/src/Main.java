import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int size = Integer.parseInt(scanner.nextLine());
        Set<String> words = new HashSet<>(size);

        for (int i = 0; i < size; i++) {
            words.add(scanner.nextLine().toLowerCase());
        }
        Set<String> newWords = new HashSet<>();
        int linesCount = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < linesCount; i++) {
            Arrays.stream(scanner.nextLine().toLowerCase().split("\\s+")).forEach(s -> {
                if (!words.contains(s)) {
                    newWords.add(s);
                }
            });
        }
        newWords.forEach(System.out::println);
    }
}