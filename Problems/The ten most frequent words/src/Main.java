import java.util.*;

public class Main {
    public static void main(String[] args) {
        String line = new Scanner(System.in).nextLine();
        String[] words = line.toLowerCase().split("[^a-zA-Z0-9]");
        Map<String, Long> map = new HashMap<>();
        Arrays.stream(words)
                .filter(s -> !s.isBlank())
                .forEach(s -> {
                    Long count = map.get(s);
                    if (count != null) {
                        count++;
                        map.put(s, count);
                    } else {
                        map.put(s, 1L);
                    }
                });
        map.entrySet().stream()
                .sorted(((Comparator<Map.Entry<String, Long>>) (o1, o2) -> o2.getValue().compareTo(o1.getValue())).thenComparing(Map.Entry::getKey))
                .limit(10)
                .map(Map.Entry::getKey)
                .forEach(System.out::println);
    }
}