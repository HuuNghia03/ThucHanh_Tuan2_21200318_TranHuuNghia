package com.example.tuan2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayDeque;
import java.util.Deque;

public class main_activity extends AppCompatActivity {
    private TextView tvPhepTinh, tvKetQua;
    private StringBuilder expression = new StringBuilder();
    private boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        tvPhepTinh = findViewById(R.id.tvPhepTinh);
        tvKetQua = findViewById(R.id.tvKetQua);

        // Khôi phục dữ liệu khi xoay màn hình
        if (savedInstanceState != null) {
            expression = new StringBuilder(savedInstanceState.getString("expression", ""));
            isResultDisplayed = savedInstanceState.getBoolean("isResultDisplayed", false);
            tvPhepTinh.setText(expression.toString());
            tvKetQua.setText(savedInstanceState.getString("result", "0"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("expression", expression.toString());
        outState.putString("result", tvKetQua.getText().toString());
        outState.putBoolean("isResultDisplayed", isResultDisplayed);
    }

    public void onClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        if (isResultDisplayed && buttonText.matches("[0-9]")) {
            expression.setLength(0);
            isResultDisplayed = false;
        }

        switch (buttonText) {
            case "AC":
                expression.setLength(0);
                tvKetQua.setText("0");
                isResultDisplayed = false;
                break;
            case "DE":
                if (expression.length() > 0) expression.deleteCharAt(expression.length() - 1);
                break;
            case "=":
                tvKetQua.setText(calculate(expression.toString()));
                isResultDisplayed = true;
                break;
            case "%":
                applyPercentage();
                break;
            default:
                if (isResultDisplayed && "+-×÷".contains(buttonText)) {
                    expression.setLength(0);
                    expression.append(tvKetQua.getText().toString());
                    isResultDisplayed = false;
                } else if (isResultDisplayed) {
                    expression.setLength(0);
                    isResultDisplayed = false;
                }
                expression.append(buttonText);
                break;
        }

        tvPhepTinh.setText(expression.toString());
    }

    private void applyPercentage() {
        if (expression.length() == 0) return;

        int lastIndex = expression.length() - 1;
        while (lastIndex >= 0 && (Character.isDigit(expression.charAt(lastIndex)) || expression.charAt(lastIndex) == '.')) {
            lastIndex--;
        }

        if (lastIndex < expression.length() - 1) {
            String number = expression.substring(lastIndex + 1);
            double value = Double.parseDouble(number) / 100;
            expression.replace(lastIndex + 1, expression.length(), String.valueOf(value));
        }
    }

    private String calculate(String expr) {
        try {
            double result = evaluateExpression(expr);
            if (result == (int) result) {
                return String.valueOf((int) result);
            } else {
                return String.valueOf(result);
            }
        } catch (Exception e) {
            return "Error";
        }
    }


    private double evaluateExpression(String expr) {
        expr = expr.replace("×", "*").replace("÷", "/");
        Deque<Double> numbers = new ArrayDeque<>();
        Deque<Character> operators = new ArrayDeque<>();

        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i++));
                }
                numbers.push(Double.parseDouble(num.toString()));
                i--;
            } else if ("+-*/".indexOf(c) != -1) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
            i++;
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private int precedence(char op) {
        return (op == '+' || op == '-') ? 1 : (op == '*' || op == '/') ? 2 : 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            default: throw new IllegalArgumentException("Invalid Operator");
        }
    }
}
