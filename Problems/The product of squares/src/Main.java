import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class CollectorProduct {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String[] values = scanner.nextLine().split(" ");

        List<Integer> numbers = new ArrayList<>();
        for (String val : values) {
            numbers.add(Integer.parseInt(val));
        }

        long val = numbers.stream().map(aLong -> aLong * aLong).reduce(1, (number, number2) -> number * number2);

        System.out.println(val);
    }
}