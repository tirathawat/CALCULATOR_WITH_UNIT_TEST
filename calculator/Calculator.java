package calculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Calculator {

    private enum State {
        START, ADD_SUBTRACT, MULTIPLY_DIVIDE, NEGATIVE_SIGN, LEFT_PARENTHESIS, RIGHT_PARENTHESIS, NUMBER,
    };

    private ArrayList<String> postfix = new ArrayList<>();
    private Stack<String> operationStack = new Stack<>();
    private Stack<Double> numStack = new Stack<>();

    private final Map<State, Integer> priority = new HashMap<State, Integer>() {
        {
            put(State.NUMBER, 1);
            put(State.ADD_SUBTRACT, 2);
            put(State.MULTIPLY_DIVIDE, 3);
            put(State.NEGATIVE_SIGN, 4);
            put(State.LEFT_PARENTHESIS, 5);
            put(State.RIGHT_PARENTHESIS, 6);
        }
    };

    private String formatAnswer(double answer) {
        NumberFormat numberFormat = new DecimalFormat("##.###");

        if (Double.isNaN(answer))
            return "Nan";
        else if (Double.isInfinite(answer))
            return "Infinity";
        else
            return numberFormat.format(answer);
    }

    public String calculate(String expression) {
        double answer;
        String formatedAnswer;
        String[] elements;

        if (expression.isBlank() || expression.isEmpty())
            return "Expression is empty";

        elements = separateExpression(expression);

        try {
            validateExpressionSyntax(elements);
            transformInfixToPostfix(elements);
            answer = calculatePostfix();
        } catch (CalculatorException e) {
            return e.getMessage();
        }

        formatedAnswer = formatAnswer(answer);
        return formatedAnswer;
    }

    public double add(double number1, double number2) {
        return number1 + number2;
    }

    public double subtract(double number1, double number2) {
        return number1 - number2;
    }

    public double multiply(double number1, double number2) {
        return number1 * number2;
    }

    public double divide(double numerator, double denominator) throws CalculatorException {
        if (denominator == 0)
            throw new CalculatorException("Can not divided by zero");
        else
            return (numerator / denominator);
    }

    private void changeNegativeSign(String[] elements) {
        for (int i = 0; i < elements.length - 1; i++)
            if (elements[i + 1].equals("-") && elements[i].matches("[-+/(*]"))
                elements[i + 1] = "!";
    }

    private String[] separateExpression(String expression) {
        String[] elements;
        expression = "(" + expression + ")";
        expression = addSpaceBetweenOperator(expression);
        elements = expression.split(" ");
        changeNegativeSign(elements);
        return elements;
    }

    private boolean validOperationOrder(State current, State next) {
        boolean validOperationOrder = true;

        if (current == State.START && (isMathOperator(next) || next == State.RIGHT_PARENTHESIS))
            validOperationOrder = false;
        else if (current == State.NUMBER && (next == State.NUMBER || next == State.LEFT_PARENTHESIS))
            validOperationOrder = false;
        else if (isMathOperator(current) && (isMathOperator(next) || next == State.RIGHT_PARENTHESIS))
            validOperationOrder = false;
        else if (current == State.NEGATIVE_SIGN && (isMathOperator(next) || next == State.RIGHT_PARENTHESIS))
            validOperationOrder = false;
        else if (current == State.LEFT_PARENTHESIS && (isMathOperator(next) || next == State.RIGHT_PARENTHESIS))
            validOperationOrder = false;

        return validOperationOrder;
    }

    private void validateExpressionSyntax(String[] elements) throws CalculatorException {
        State current = State.START;
        State next = State.START;
        int countParenthesis = 0;

        for (int i = 1; i < elements.length - 1; i++) {
            current = next;
            next = getState(elements[i]);

            if (next == State.LEFT_PARENTHESIS)
                countParenthesis++;
            else if (next == State.RIGHT_PARENTHESIS)
                countParenthesis--;

            if (countParenthesis < 0)
                throw new CalculatorException("Invalid parenthesis");

            if (!validOperationOrder(current, next))
                throw new CalculatorException("Invalid operation order");
        }

        if (countParenthesis != 0)
            throw new CalculatorException("Invalid parenthesis");
    }

    private State getState(String str) throws CalculatorException {
        if (isNumber(str))
            return State.NUMBER;
        else if (str.matches("[-+]"))
            return State.ADD_SUBTRACT;
        else if (str.matches("[*/]"))
            return State.MULTIPLY_DIVIDE;
        else if (str.equals("!"))
            return State.NEGATIVE_SIGN;
        else if (str.equals("("))
            return State.LEFT_PARENTHESIS;
        else if (str.equals(")"))
            return State.RIGHT_PARENTHESIS;
        else
            throw new CalculatorException("Invalid operation");
    }

    private boolean isNumber(String str) {
        boolean isNumber = true;
        try {
            Double.parseDouble(str);
        } catch (Exception e) {
            isNumber = false;
        }
        return isNumber;
    }

    private String addSpaceBetweenOperator(String expression) {
        expression = expression.replace("+", " + ");
        expression = expression.replace("-", " - ");
        expression = expression.replace("*", " * ");
        expression = expression.replace("/", " / ");
        expression = expression.replace("(", " ( ");
        expression = expression.replace(")", " ) ");
        expression = expression.trim();
        expression = expression.replaceAll("\\s+", " ");
        return expression;
    }

    private boolean isParenthesis(State state) {
        return state == State.LEFT_PARENTHESIS || state == State.RIGHT_PARENTHESIS;
    }

    private boolean isMathOperator(State state) {
        return state == State.ADD_SUBTRACT || state == State.MULTIPLY_DIVIDE;
    }

    private boolean morePriority(State s1, State s2) {
        return priority.get(s1) >= priority.get(s2) && !isParenthesis(s1);
    }

    private void transformMathOperator(String element) throws CalculatorException {
        State currentState;
        State bufferState;
        String buffer;

        do {
            currentState = getState(element);
            buffer = operationStack.peek();
            bufferState = getState(buffer);
            if (morePriority(bufferState, currentState)) {
                buffer = operationStack.pop();
                postfix.add(buffer);
            }
        } while (morePriority(bufferState, currentState));

        operationStack.push(element);
    }

    private void transformRightParenthesis() {
        String buffer;

        do {
            buffer = operationStack.pop();
            if (!buffer.equals("("))
                postfix.add(buffer);
        } while (!buffer.equals("("));
    }

    private void transformInfixToPostfix(String[] elements) throws CalculatorException {
        State state;

        postfix.clear();
        operationStack.clear();

        for (int i = 0; i < elements.length; i++) {
            state = getState(elements[i]);
            if (state == State.NUMBER)
                postfix.add(elements[i]);
            else if (state == State.LEFT_PARENTHESIS)
                operationStack.push(elements[i]);
            else if (isMathOperator(state) || state == State.NEGATIVE_SIGN)
                transformMathOperator(elements[i]);
            else if (state == State.RIGHT_PARENTHESIS)
                transformRightParenthesis();
        }
    }

    private void calculateMathOperator(State state, String element) throws CalculatorException {
        double number2 = numStack.pop();
        double number1 = numStack.pop();

        if (state == State.ADD_SUBTRACT && element.equals("+"))
            numStack.push(add(number1, number2));
        else if (state == State.ADD_SUBTRACT && element.equals("-"))
            numStack.push(subtract(number1, number2));
        else if (state == State.MULTIPLY_DIVIDE && element.equals("*"))
            numStack.push(multiply(number1, number2));
        else if (state == State.MULTIPLY_DIVIDE && element.equals("/"))
            numStack.push(divide(number1, number2));
    }

    private double calculatePostfix() throws CalculatorException {
        double answer;
        State state;
        String element;

        for (int i = 0; i < postfix.size(); i++) {
            element = postfix.get(i);
            state = getState(element);

            if (state == State.NUMBER)
                numStack.push(Double.parseDouble(element));
            else if (isMathOperator(state))
                calculateMathOperator(state, element);
            else if (state == State.NEGATIVE_SIGN)
                numStack.push(-numStack.pop());
        }

        answer = numStack.pop();
        return answer;
    }
}
