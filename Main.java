import java.util.Scanner;

import calculator.Calculator;
import calculator.CalculatorException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str;
        str = scanner.nextLine();
        Calculator calculator = new Calculator();
        try {
            System.out.println(calculator.calculate(str));
        } catch (CalculatorException e) {
            System.out.println(e.getMessage());
        }
        scanner.close();
    }
}
