import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    static void changeList(List<String> list) {
        String maxString = "";
        for (String s : list) {
            if (maxString.length() < s.length()) {
                maxString = s;
            }
        }

        for (int i = 0; i < list.size(); i++) {
            list.set(i, maxString);
        }
    }

    /* Do not change code below */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        List<String> lst = Arrays.asList(s.split(" "));
        changeList(lst);
        lst.forEach(e -> System.out.print(e + " "));
    }
}