package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import calculator.Calculator;

public class CalculatorTester {

    @Test
    public void testSimpleCalculation() {
        String answer;
        Calculator calculator = new Calculator();

        answer = calculator.calculate("1 + 1");
        assertEquals("2", answer);

        answer = calculator.calculate("1 - 3");
        assertEquals("-2", answer);

        answer = calculator.calculate("1.23 + 2.555555");
        assertEquals("3.786", answer);

        answer = calculator.calculate("0 * 12.22323");
        assertEquals("0", answer);

        answer = calculator.calculate("0 / 12.22323");
        assertEquals("0", answer);

        answer = calculator.calculate("12.22323 / 0");
        assertEquals("Can not divided by zero", answer);
    }

    @Test
    public void testOperationOrder() {
        String answer;
        Calculator calculator = new Calculator();

        answer = calculator.calculate("1 + 2 * 5 - 2 / 4 * 6 + 7 / 2 / 5 - 4");
        assertEquals("4.7", answer);

        answer = calculator.calculate("1 + (2 * 5) - (2 / 4 * 6 + 7) / (2 / 5 - 4)");
        assertEquals("13.778", answer);

        answer = calculator.calculate("-2 + 5(2 * 4)");
        assertEquals("Invalid operation order", answer);

        answer = calculator.calculate("1 + 1 - 2*( 5 / 5*)");
        assertEquals("Invalid operation order", answer);

        answer = calculator.calculate("1 + ()");
        assertEquals("Invalid operation order", answer);

        answer = calculator.calculate("2 3 +");
        assertEquals("Invalid operation order", answer);
    }

    @Test
    public void testInputExpression() {
        String answer;
        Calculator calculator = new Calculator();

        answer = calculator.calculate("");
        assertEquals("Expression is empty", answer);

        answer = calculator.calculate(" ");
        assertEquals("Expression is empty", answer);

        answer = calculator.calculate("This is expression.");
        assertEquals("Invalid operation", answer);

        answer = calculator.calculate("a + b");
        assertEquals("Invalid operation", answer);

        answer = calculator.calculate("a + 5");
        assertEquals("Invalid operation", answer);

        answer = calculator.calculate("3/3*4+1-1");
        assertEquals("4", answer);

        answer = calculator.calculate("3    /    3  * 4  +1  -1");
        assertEquals("4", answer);

        answer = calculator.calculate("2");
        assertEquals("2", answer);

        answer = calculator.calculate("         2          ");
        assertEquals("2", answer);

        answer = calculator.calculate("  -       2          ");
        assertEquals("-2", answer);
    }

    @Test
    public void testParenthesis() {
        String answer;
        Calculator calculator = new Calculator();

        answer = calculator.calculate("2 + ((3)) + ((((5))))");
        assertEquals("10", answer);

        answer = calculator.calculate("2 + ((3)) + (((((5))))");
        assertEquals("Invalid parenthesis", answer);

        answer = calculator.calculate("2 + )((3)) + ((((5))))");
        assertEquals("Invalid parenthesis", answer);

        answer = calculator.calculate("2*(3*(4*(5*7)))) ");
        assertEquals("Invalid parenthesis", answer);
    }

    @Test
    public void testNegetiveSign() {
        String answer;
        Calculator calculator = new Calculator();

        answer = calculator.calculate("2 -- 5 ");
        assertEquals("7", answer);

        answer = calculator.calculate("2 +- 5 ");
        assertEquals("-3", answer);

        answer = calculator.calculate("-2 -- 5 ");
        assertEquals("3", answer);

        answer = calculator.calculate("-2*(2 + -3) ");
        assertEquals("2", answer);
    }

}
