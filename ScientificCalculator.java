import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// =========================
// Operations Module
// =========================
class CalculatorOperations {

    public static double calculate(double num1, double num2, String op) {
        return switch (op) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "*" -> num1 * num2;
            case "/" -> num1 / num2;
            case "^" -> Math.pow(num1, num2);
            default -> 0;
        };
    }

    // Scientific functions
    public static double sin(double val) { return Math.sin(Math.toRadians(val)); }
    public static double cos(double val) { return Math.cos(Math.toRadians(val)); }
    public static double tan(double val) { return Math.tan(Math.toRadians(val)); }

    public static double arcsin(double val) { return Math.toDegrees(Math.asin(val)); }
    public static double arccos(double val) { return Math.toDegrees(Math.acos(val)); }
    public static double arctan(double val) { return Math.toDegrees(Math.atan(val)); }

    public static double sqrt(double val) { return Math.sqrt(val); }
    public static double nthRoot(double val, double n) { return Math.pow(val, 1.0 / n); }
    public static double log(double val) { return Math.log10(val); }
    public static double ln(double val) { return Math.log(val); }

    public static double factorial(int n) {
        if (n < 0) return Double.NaN;
        double fact = 1;
        for (int i = 2; i <= n; i++) fact *= i;
        return fact;
    }
}

// =========================
// Controller Module
// =========================
class CalculatorController implements ActionListener {
    private final JTextField display;
    private String pendingFunction = ""; // store function pressed first

    public CalculatorController(JTextField display) {
        this.display = display;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = e.getActionCommand();

        // Function pressed first
        if (text.equals("sin") || text.equals("cos") || text.equals("tan") 
                || text.equals("arcsin") || text.equals("arccos") || text.equals("arctan")
                || text.equals("sqrt") || text.equals("log") || text.equals("ln") 
                || text.equals("!") ) {
            pendingFunction = text;      
            if (text.equals("!")) display.setText("fact(");
            else display.setText(text + "(");  // show function in display
        }
        // Number input
        else if ("0123456789.".contains(text)) {
            display.setText(display.getText() + text);
        }
        // Constants
        else if (text.equals("π")) display.setText(display.getText() + Math.PI);
        else if (text.equals("e")) display.setText(display.getText() + Math.E);
        // "=" pressed
        else if (text.equals("=")) {
    try {
        if (!pendingFunction.isEmpty()) {
            String currentText = display.getText(); // e.g., "cos(2"
            
            // Extract number after "("
            int idx = currentText.indexOf("(");
            double value;
            if (idx != -1 && idx < currentText.length() - 1) {
                value = Double.parseDouble(currentText.substring(idx + 1));
            } else {
                value = 0; // default if no number typed
            }

            double result = 0;

            if (pendingFunction.equals("sin")) result = CalculatorOperations.sin(value);
            else if (pendingFunction.equals("cos")) result = CalculatorOperations.cos(value);
            else if (pendingFunction.equals("tan")) result = CalculatorOperations.tan(value);
            else if (pendingFunction.equals("arcsin")) result = CalculatorOperations.arcsin(value);
            else if (pendingFunction.equals("arccos")) result = CalculatorOperations.arccos(value);
            else if (pendingFunction.equals("arctan")) result = CalculatorOperations.arctan(value);
            else if (pendingFunction.equals("sqrt")) result = CalculatorOperations.sqrt(value);
            else if (pendingFunction.equals("log")) result = CalculatorOperations.log(value);
            else if (pendingFunction.equals("ln")) result = CalculatorOperations.ln(value);
            else if (pendingFunction.equals("!")) result = CalculatorOperations.factorial((int)value);

            display.setText("" + result);
            pendingFunction = ""; // reset
        }
        // Else, handle basic arithmetic like 2 + 3
        else {
            String[] parts = display.getText().split(" ");
            if (parts.length == 3) {
                double num1 = Double.parseDouble(parts[0]);
                double num2 = Double.parseDouble(parts[2]);
                String op = parts[1];
                double result = CalculatorOperations.calculate(num1, num2, op);
                display.setText("" + result);
            }
        }
    } catch (Exception ex) {
        display.setText("Error");
        pendingFunction = "";
    }
}

        // Arithmetic operators
        else if ("+-*/^".contains(text)) {
            display.setText(display.getText() + " " + text + " ");
        }
        else if (text.equals("C")) {
            display.setText("");
            pendingFunction = "";
        }
        else if (text.equals("CE")) {
            String current = display.getText();
            if (!current.isEmpty()) display.setText(current.substring(0, current.length() - 1));
        }
    }
}

// =========================
// UI Module
// =========================
public class ScientificCalculator {
    private final JTextField display;
    private final JLabel modeLabel;
    private final JPanel buttonPanel;
    private final CalculatorController controller;
    private boolean isScientific = false;

    public ScientificCalculator() {
        JFrame frame = new JFrame("Advanced Scientific Calculator");
        frame.setSize(430, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Mode label
        modeLabel = new JLabel("Mode: Simple", SwingConstants.CENTER);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(modeLabel, BorderLayout.NORTH);

        // Display
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 30));
        display.setBackground(Color.BLACK);
        display.setForeground(Color.WHITE);
        frame.add(display, BorderLayout.CENTER);

        // Panel for buttons
        buttonPanel = new JPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Controller
        controller = new CalculatorController(display);

        // Bottom panel for buttons + toggle
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        JButton toggle = new JButton("Switch to Scientific");
        toggle.addActionListener(e -> toggleMode(toggle));
        bottomPanel.add(toggle, BorderLayout.SOUTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loadSimple(); // start in simple mode
        frame.setVisible(true);
    }

    private void toggleMode(JButton toggle) {
        isScientific = !isScientific;
        if (isScientific) {
            loadScientific();
            modeLabel.setText("Mode: Scientific");
            toggle.setText("Switch to Standard");
        } else {
            loadSimple();
            modeLabel.setText("Mode: Standard");
            toggle.setText("Switch to Scientific");
        }
    }

    private void addButtons(String[] buttons) {
        buttonPanel.removeAll();
        buttonPanel.setLayout(new GridLayout(buttons.length / 4 + 1, 4, 10, 10));
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.addActionListener(controller);
            buttonPanel.add(btn);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void loadSimple() {
        String[] buttons = {
            "C","CE","","/",
            "7","8","9","*",
            "4","5","6","-",
            "1","2","3","+",
            "0",".","","="
        };
        addButtons(buttons);
    }

    private void loadScientific() {
        String[] buttons = {
        "C","CE","π","e",
        "sin","cos","tan","log",
        "arcsin","arccos","arctan","ln",
        "sqrt","^","!","/",
        "7","8","9","*",
        "4","5","6","-",
        "1","2","3","+",
        "0",".","","="

    };
        addButtons(buttons);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScientificCalculator::new);
    }
}
