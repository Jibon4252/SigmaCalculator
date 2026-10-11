package com.sigma.mjcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    private TextView display;
    private String expression = "";
    private boolean justCalculated = false;

    private final String[][] keys = {
        {"sin", "cos", "tan", "√"},
        {"ln", "log", "(", ")"},
        {"π", "e", "AC", "DEL"},
        {"7", "8", "9", "÷"},
        {"4", "5", "6", "×"},
        {"1", "2", "3", "−"},
        {"0", ".", "=", "+"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(18, 24, 38));
        getWindow().setNavigationBarColor(Color.rgb(18, 24, 38));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(12));
        root.setBackgroundColor(Color.rgb(12, 17, 29));

        TextView title = new TextView(this);
        title.setText("MJ Calculator");
        title.setTextColor(Color.WHITE);
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(
            -1, dp(55)
        ));

        TextView subtitle = new TextView(this);
        subtitle.setText("BASIC  •  SCIENTIFIC");
        subtitle.setTextColor(Color.rgb(130, 151, 183));
        subtitle.setTextSize(12);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(
            -1, dp(28)
        ));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        display = new TextView(this);
        display.setText("0");
        display.setTextColor(Color.WHITE);
        display.setTextSize(32);
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        display.setPadding(dp(12), dp(16), dp(12), dp(16));
        display.setBackgroundColor(Color.rgb(25, 34, 51));

        LinearLayout.LayoutParams displayParams =
            new LinearLayout.LayoutParams(-1, dp(125));
        displayParams.setMargins(0, dp(18), 0, dp(18));
        content.addView(display, displayParams);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setUseDefaultMargins(false);

        for (int r = 0; r < keys.length; r++) {
            for (int c = 0; c < keys[r].length; c++) {
                String key = keys[r][c];

                Button button = new Button(this);
                button.setText(key);
                button.setTextSize(17);
                button.setAllCaps(false);
                button.setTextColor(Color.WHITE);
                button.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                        getButtonColor(key)
                    )
                );

                GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams(
                        GridLayout.spec(r),
                        GridLayout.spec(c, 1f)
                    );

                params.width = 0;
                params.height = dp(58);
                params.setMargins(dp(3), dp(3), dp(3), dp(3));
                grid.addView(button, params);

                button.setOnClickListener(v -> press(key));
            }
        }

        content.addView(grid);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
            -1, 0, 1f
        ));

        TextView footer = new TextView(this);
        footer.setText("Made with care • MJ");
        footer.setTextColor(Color.rgb(115, 130, 155));
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        root.addView(footer, new LinearLayout.LayoutParams(
            -1, dp(35)
        ));

        setContentView(root);
    }

    private int getButtonColor(String key) {
        if (key.equals("=")) return Color.rgb(0, 160, 125);
        if (key.equals("AC") || key.equals("DEL"))
            return Color.rgb(172, 65, 76);
        if ("÷×−+".contains(key))
            return Color.rgb(54, 92, 151);
        if (key.equals("sin") || key.equals("cos")
                || key.equals("tan") || key.equals("√")
                || key.equals("ln") || key.equals("log")
                || key.equals("π") || key.equals("e")
                || key.equals("(") || key.equals(")"))
            return Color.rgb(43, 55, 76);
        return Color.rgb(31, 41, 59);
    }

    private int dp(int value) {
        return (int) (value * getResources()
            .getDisplayMetrics().density + 0.5f);
    }

    private void press(String key) {
        if (key.equals("AC")) {
            expression = "";
            justCalculated = false;
            updateDisplay();
            return;
        }

        if (key.equals("DEL")) {
            if (justCalculated) {
                expression = "";
                justCalculated = false;
            } else if (!expression.isEmpty()) {
                expression = expression.substring(
                    0, expression.length() - 1
                );
            }
            updateDisplay();
            return;
        }

        if (key.equals("=")) {
            if (expression.isEmpty()) return;
            try {
                double result = new Parser(expression).parse();
                if (!Double.isFinite(result))
                    throw new ArithmeticException();

                expression = format(result);
                justCalculated = true;
                updateDisplay();
            } catch (Exception ex) {
                Toast.makeText(this, "হিসাবটি সঠিক নয়",
                    Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (justCalculated) {
            if ("+−×÷".contains(key)) {
                justCalculated = false;
            } else {
                expression = "";
                justCalculated = false;
            }
        }

        switch (key) {
            case "sin":
            case "cos":
            case "tan":
            case "ln":
            case "log":
                expression += key + "(";
                break;
            case "√":
                expression += "sqrt(";
                break;
            case "π":
                expression += "pi";
                break;
            default:
                expression += key;
        }

        updateDisplay();
    }

    private void updateDisplay() {
        display.setText(expression.isEmpty() ? "0" : expression);
    }

    private String format(double value) {
        if (value == Math.rint(value)
                && Math.abs(value) < 1.0e15) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.format(Locale.US, "%.10g", value);
    }

    private static class Parser {
        private final String s;
        private int pos = -1;
        private int ch;

        Parser(String input) {
            s = input.replace("×", "*")
                     .replace("÷", "/")
                     .replace("−", "-");
            nextChar();
        }

        private void nextChar() {
            ch = (++pos < s.length()) ? s.charAt(pos) : -1;
        }

        private boolean eat(int c) {
            while (ch == ' ') nextChar();
            if (ch == c) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            double x = expression();
            if (ch != -1) throw new RuntimeException();
            return x;
        }

        private double expression() {
            double x = term();
            while (true) {
                if (eat('+')) x += term();
                else if (eat('-')) x -= term();
                else return x;
            }
        }

        private double term() {
            double x = factor();
            while (true) {
                if (eat('*')) x *= factor();
                else if (eat('/')) x /= factor();
                else return x;
            }
        }

        private double factor() {
            if (eat('+')) return factor();
            if (eat('-')) return -factor();

            double x;
            int start = pos;

            if (eat('(')) {
                x = expression();
                if (!eat(')')) throw new RuntimeException();
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') {
                    nextChar();
                }
                x = Double.parseDouble(s.substring(start, pos));
            } else if (ch >= 'a' && ch <= 'z') {
                while (ch >= 'a' && ch <= 'z') nextChar();
                String name = s.substring(start, pos);

                if (name.equals("pi")) return Math.PI;
                if (name.equals("e")) return Math.E;

                if (!eat('(')) throw new RuntimeException();
                double a = expression();
                if (!eat(')')) throw new RuntimeException();

                switch (name) {
                    case "sin": x = Math.sin(Math.toRadians(a)); break;
                    case "cos": x = Math.cos(Math.toRadians(a)); break;
                    case "tan": x = Math.tan(Math.toRadians(a)); break;
                    case "sqrt": x = Math.sqrt(a); break;
                    case "ln": x = Math.log(a); break;
                    case "log": x = Math.log10(a); break;
                    default: throw new RuntimeException();
                }
            } else {
                throw new RuntimeException();
            }

            return x;
        }
    }
}
