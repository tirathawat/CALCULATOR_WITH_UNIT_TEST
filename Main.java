import java.util.Scanner;

import calculator.Calculator;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str;
        str = scanner.nextLine();
        Calculator calculator = new Calculator();
        System.out.println(calculator.calculate(str));
        scanner.close();
    }
}
