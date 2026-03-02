//IULIA LUPANCIUC - IA2402 - CNMO-lab1
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainFrame extends JFrame {

    //UI Components List
    private JTextField functionField, aField, bField, epsilonField;
    private JComboBox<String> methodComboBox;
    private JButton executeButton;
    private ButtonGroup searchGroup;
    private JRadioButton collectionRadio, interactiveRadio;
    private GraphPanel graphPanel;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private int iterationCount = 0;

    public MainFrame() {
        setTitle("Metode Numerice de Calcul al Radacinilor Ecuatiilor Neliniare");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        //Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Top Panel aka Input Panel
        JPanel topPanel = createInputPanel();

        //Graph Panel
        graphPanel = new GraphPanel();
        TitledBorder borderGraph = BorderFactory.createTitledBorder("Graficul Functiei si Radacinele Determinate");
        borderGraph.setTitleJustification(TitledBorder.CENTER);
        graphPanel.setBorder(borderGraph);
        graphPanel.setPreferredSize(new Dimension(400, 300));

        //Results Panel
        JPanel resultsPanel = createResultsPanel();

        //Bottom Panel | Graph to the Left | Results Table to the Right
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.add(graphPanel);
        bottomPanel.add(resultsPanel);

        //Merging Panel Components into a Main Frame
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        add(mainPanel);

        //Execute Button
        executeButton.addActionListener(e -> executeCalculation());
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        TitledBorder border1 = BorderFactory.createTitledBorder("Precizarea Membrului Stang al Ecuatiei");
        border1.setTitleJustification(TitledBorder.CENTER);
        panel.setBorder(border1);

        // ------- Row 1: Collection + ComboBox -------
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        collectionRadio = new JRadioButton("din colectie");
        row1.add(collectionRadio);
        row1.add(new JLabel("f(x)="));

        JComboBox<String> functionCombo = new JComboBox<>(new String[]{
                "SELECTATI FUNCTIA",
                "2*sin(3*x)-ln(x^3-1)+4",
                "sin*(pi*x/6)-cos(x-1)",
                "e^(-x)-x^3+8*cos(4*x)",
                "x^6-5.5*x^5+6.18*x^4+18.54*x^3-56.9592*x^2+55.9872*x-19.3156",
                "x^6-0.7*x^5-8.7*x^4+5.58*x^3+22.356*x^2-8.39808*x-19.3156",
                "x^6-2.4*x^5-18.27*x^4+23.216*x^3+115.7*x^2-19.5804*x-164.818"
        });
        functionCombo.setPreferredSize(new Dimension(400, 25));
        functionCombo.addActionListener(e -> {
            String selected = (String) functionCombo.getSelectedItem();
            if(!selected.equals("SELECTATI FUNCTIA")){
                functionField.setText(selected);
                setFunctionParameters(selected);
            }
        });
        row1.add(functionCombo);
        panel.add(row1);

        // ------- Row 2: Interactive + TextField -------
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        interactiveRadio = new JRadioButton("interactiv");
        interactiveRadio.setSelected(true); //selected by defaul
        row2.add(interactiveRadio);
        row2.add(new JLabel("f(x)="));
        functionField = new JTextField("x*cos(x)*sin(x)", 35); //default equation
        row2.add(functionField);
        panel.add(row2);

        // Button Group
        searchGroup = new ButtonGroup();
        searchGroup.add(collectionRadio);
        searchGroup.add(interactiveRadio);

        collectionRadio.addActionListener(e -> {
            // When "din colectie" is Selected >> Enable Combo Box
            functionCombo.setEnabled(true);
            functionField.setEnabled(false);
        });

        interactiveRadio.addActionListener(e -> {
            // When "interactiv" is Selected >> Enable Text Field
            functionCombo.setEnabled(false);
            functionField.setEnabled(true);
            // Reset to default interactive values
            aField.setText("-9");
            bField.setText("8");
            epsilonField.setText("6");
        });

        if (interactiveRadio.isSelected()) {
            functionCombo.setEnabled(false);
            functionField.setEnabled(true);
        } else {
            functionCombo.setEnabled(true);
            functionField.setEnabled(false);
        }

        // ------- Row 3: Interval Panel + Method Panel -------
        JPanel row3 = new JPanel(new GridLayout(1, 2, 10, 5));

        // Interval Panel
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        TitledBorder border2 = BorderFactory.createTitledBorder("Extremitatile Intervalului");
        border2.setTitleJustification(TitledBorder.CENTER);
        intervalPanel.setBorder(border2);
        intervalPanel.add(new JLabel("a="));
        aField = new JTextField("-9", 5); //default value
        intervalPanel.add(aField);
        intervalPanel.add(Box.createHorizontalStrut(20));
        intervalPanel.add(new JLabel("b="));
        bField = new JTextField("8", 5); //default value
        intervalPanel.add(bField);

        // Method Panel
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        TitledBorder border3 = BorderFactory.createTitledBorder("Precizarea Metodei de Calcul");
        border3.setTitleJustification(TitledBorder.CENTER);
        methodPanel.setBorder(border3);
        methodComboBox = new JComboBox<>(new String[]{
                "SELECTATI METODA",
                "Metoda Bisectiei",
                "Metoda Tangentei (Newton)",
                "Metoda Secantelor",
                "Metoda Coardelor",
                "Metoda YULIA :)"
        });
        methodPanel.add(methodComboBox);
        methodPanel.add(Box.createHorizontalStrut(10));
        methodPanel.add(new JLabel("eps=1e-"));
        epsilonField = new JTextField("6", 3); //default value
        methodPanel.add(epsilonField);

        row3.add(intervalPanel);
        row3.add(methodPanel);
        panel.add(row3);

        // ------- Row 4: Execute Button -------
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        executeButton = new JButton("EXECUTA!");
        executeButton.setPreferredSize(new Dimension(150, 35));
        executeButton.setFont(new Font("Arial", Font.BOLD, 12));
        row4.add(executeButton);
        panel.add(row4);

        return panel;
    }

    private JPanel createResultsPanel(){
        JPanel panel = new JPanel(new BorderLayout());

        //Results Table
        String[] columns = {"radacina x", "f(x)", "nr. iteratii k", "timp de calcul"};
        tableModel = new DefaultTableModel(columns, 0);
        resultsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(600, 150));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    //Functions Paramets for Autofill when Selected
    private void setFunctionParameters(String functionName) {
        switch (functionName) {
            case "2*sin(3*x)-ln(x^3-1)+4":
                aField.setText("2");
                bField.setText("9");
                epsilonField.setText("4");
                break;
            case "sin*(pi*x/6)-cos(x-1)":
                aField.setText("-7");
                bField.setText("8");
                epsilonField.setText("4");
                break;
            case "e^(-x)-x^3+8*cos(4*x)":
                aField.setText("-4");
                bField.setText("4");
                epsilonField.setText("4");
                break;
            case "x^6-5.5*x^5+6.18*x^4+18.54*x^3-56.9592*x^2+55.9872*x-19.3156":
                aField.setText("-3");
                bField.setText("4");
                epsilonField.setText("4");
                break;
            case "x^6-0.7*x^5-8.7*x^4+5.58*x^3+22.356*x^2-8.39808*x-19.3156":
                aField.setText("-3");
                bField.setText("4");
                epsilonField.setText("4");
                break;
            case "x^6-2.4*x^5-18.27*x^4+23.216*x^3+115.7*x^2-19.5804*x-164.818":
                aField.setText("-3");
                bField.setText("4");
                epsilonField.setText("4");
                break;
            default:
                break;
        }
    }

    private void executeCalculation(){
        //Clear Table Before New Calculation
        tableModel.setRowCount(0);

        //Disabled Button During Calculation
        executeButton.setEnabled(false);

        //Run Calculation in Background Thread
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private java.util.List<Double> roots;
            private java.util.List<Double> fRoots;
            private java.util.List<Integer> iterations;
            private double timeMs;
            private String errorMessage = null;

            @Override
            protected Void doInBackground() throws Exception {
                try{
                    //Getting Input Values
                    String function = functionField.getText();
                    double a = Double.parseDouble(aField.getText());
                    double b = Double.parseDouble(bField.getText());
                    String method = (String) methodComboBox.getSelectedItem();
                    double epsilon = Math.pow(10, -Double.parseDouble(epsilonField.getText()));

                    //Validating
                    if (method.equals("SELECTATI METODA")) {
                        errorMessage = "SELECTATI METODA DE CALCUL!";
                        return null;
                    }

                    //Validating Function Selection when "din colectie" is Selected
                    if (collectionRadio.isSelected()) {
                        if (function.equals("SELECTATI FUNCTIA") || function.trim().isEmpty()) {
                            errorMessage = "SELECTATI O FUNCTIE DIN COLECTIE!";
                            return null;
                        }
                    }

                    //Validating that a Function has been Entered for Interactive Mode
                    if (interactiveRadio.isSelected()) {
                        if (function.trim().isEmpty()) {
                            errorMessage = "INTRODUCETI O FUNCTIE!";
                            return null;
                        }
                    }

                    //Must Find ALL Roots
                    long startTime = System.nanoTime();
                    roots = findAllRoots(a, b, epsilon, method);

                    //Calculate f(root) for Each Root & Store Iterations
                    fRoots = new java.util.ArrayList<>();
                    iterations = new java.util.ArrayList<>();

                    for (Double root : roots) {
                        fRoots.add(evaluateFunction(function, root));
                        iterations.add(iterationCount);  //Storing the Iteration Count for this Root
                    }

                    long endTime = System.nanoTime();
                    timeMs = (endTime - startTime) / 1_000_000.0;

                    if (roots.isEmpty()) {
                        errorMessage = "Nu s-au gasit radacini pe intervalul dat!";
                    }
                }
                catch (Exception ex){
                    errorMessage = "Eroare: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done(){
                //Re-enable Button
                executeButton.setEnabled(true);

                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(MainFrame.this, errorMessage);
                    return;
                }

                //Adding ALL Roots to the Results Table
                for (int i = 0; i < roots.size(); i++) {
                    tableModel.addRow(new Object[]{
                            String.format("%.7f", roots.get(i)),
                            String.format("%.7e", fRoots.get(i)),
                            String.valueOf(iterations.get(i)),
                            String.format("%.2f ms", timeMs / roots.size())  // Divide time among roots
                    });
                }

                //Updating the Graph with ALL Roots
                double a = Double.parseDouble(aField.getText());
                double b = Double.parseDouble(bField.getText());
                graphPanel.setFunctionWithMultipleRoots(functionField.getText(), a, b, roots);
                graphPanel.repaint();
            }
        };
        worker.execute();
    }

    //Bisection Method
    private double bisectionMethod(double a, double b, double epsilon) {
        //Iteration Counter Reset
        iterationCount = 0;

        //Must check if f(a) * f(b) < 0 (roots exist in interval)
        double fa = evaluateFunction(functionField.getText(), a);
        double fb = evaluateFunction(functionField.getText(), b);

        if (fa * fb >= 0) {
            throw new IllegalArgumentException("f(a) si f(b) trebuie sa aiba semne opuse!");
        }

        //Calculating the MAX nr. of Iterations
        int maxIterations = (int) Math.ceil(Math.log((b - a) / epsilon) / Math.log(2));

        //Bisection Algorithm Loop
        while (iterationCount < maxIterations) {
            iterationCount++;

            //Calculating Midpoint
            double xn = (a + b) / 2.0;

            //Evaluating f(xn)
            double fxn = evaluateFunction(functionField.getText(), xn);

            //Checking for Exact Solution
            if (Math.abs(fxn) < epsilon) {
                return xn;
            }

            //if f(a) * f(xn) < 0, root is in [a, xn], so b = xn
            fa = evaluateFunction(functionField.getText(), a);
            if (fa * fxn < 0) {
                b = xn;
            }
            //if f(xn) * f(b) < 0, root is in [xn, b], so a = xn
            else {
                a = xn;
            }

            //Checking the Stopping Criteria
            //Precision satisfied..???
            if (Math.abs(b - a) < epsilon) {
                return (a + b) / 2.0;
            }

            //Function Value Close Enough to 0 (zero)
            double fmid = evaluateFunction(functionField.getText(), (a + b) / 2.0);
            if (Math.abs(fmid) < epsilon) {
                return (a + b) / 2.0;
            }
        }

        //Returning Final Values/Approximation
        return (a + b) / 2.0;
    }

    //False Position Method
    private double chordMethod(double a, double b, double epsilon) {
        //Iteration Counter Reset
        iterationCount = 0;

        double fa = evaluateFunction(functionField.getText(), a);
        double fb = evaluateFunction(functionField.getText(), b);

        if (fa * fb >= 0) {
            throw new IllegalArgumentException("f(a) și f(b) trebuie să aibă semne opuse!");
        }

        //MAX Iterations for Safety
        int maxIterations = 1000;

        //False Position Method Loop
        double xn = a;  // Starting point
        double xn_plus_1;

        while (iterationCount < maxIterations) {
            iterationCount++;

            double fxn = evaluateFunction(functionField.getText(), xn);
            fa = evaluateFunction(functionField.getText(), a);
            fb = evaluateFunction(functionField.getText(), b);

            xn_plus_1 = b - fb * (b - a) / (fb - fa);

            //Updating Interval
            double fxn_plus_1 = evaluateFunction(functionField.getText(), xn_plus_1);

            //Checking for Exact Solution
            if (Math.abs(fxn_plus_1) < epsilon) {
                return xn_plus_1;
            }

            //Updating Interval Based on Sign of f(xn+1)
            fa = evaluateFunction(functionField.getText(), a);
            if (fa * fxn_plus_1 < 0) {
                // Root is in [a, xn+1], so b = xn+1
                b = xn_plus_1;
            } else if (fb * fxn_plus_1 < 0) {
                // Root is in [xn+1, b], so a = xn+1
                a = xn_plus_1;
            }

            //Check Stopping Criteria
            if (Math.abs(fxn_plus_1) < epsilon) {
                return xn_plus_1;
            }

            if (Math.abs(xn_plus_1 - xn) < epsilon) {
                return xn_plus_1;
            }

            //Updating xn for Next Iteration
            xn = xn_plus_1;
        }
        return xn;
    }

    //Secant Method
    private double secantMethod(double a, double b, double epsilon) {
        //Iteration Counter Reset
        iterationCount = 0;

        // Start with two slightly different points inside interval
        double x0 = a;
        double x1 = a + (b - a) * 0.5;

        // If they are too close, shift slightly
        if (Math.abs(x1 - x0) < epsilon)
            x1 = x0 + epsilon * 10;

        double fx0 = evaluateFunction(functionField.getText(), x0);
        double fx1 = evaluateFunction(functionField.getText(), x1);

        if (Double.isNaN(fx0) || Double.isInfinite(fx0) ||
                Double.isNaN(fx1) || Double.isInfinite(fx1)) {
            throw new IllegalArgumentException("Functia trebuie sa fie finita in x0 si x1!");
        }

        if (Math.abs(fx1 - fx0) < epsilon) {
            throw new IllegalArgumentException("f(x0) și f(x1) trebuie să fie diferite!");
        }

        //MAX Iterations for Safety
        int maxIterations = 1000;

        double xn = x1;
        double xn_minus_1 = x0;
        double xn_plus_1;

        //Secant Method Loop
        while (iterationCount < maxIterations) {
            iterationCount++;

            /*Calculating next approximation using secant formula
            xn+1 = xn - f(xn) * (xn - xn-1) / (f(xn) - f(xn-1)) */
            double fxn = evaluateFunction(functionField.getText(), xn);
            double fxn_minus_1 = evaluateFunction(functionField.getText(), xn_minus_1);

            if (Double.isNaN(fxn) || Double.isInfinite(fxn) ||
                    Double.isNaN(fxn_minus_1) || Double.isInfinite(fxn_minus_1))
                throw new IllegalArgumentException("Secant produced invalid function value");

            //Check if Denominator is NOT 0 (zero)
            if (Math.abs(fxn - fxn_minus_1) < epsilon * 0.001) {
                throw new IllegalArgumentException("f(xn) - f(xn-1) este prea mic (diviziune la zero)!");
            }

            xn_plus_1 = xn - fxn * (xn - xn_minus_1) / (fxn - fxn_minus_1);
            if (Double.isNaN(xn_plus_1) || Double.isInfinite(xn_plus_1))
                throw new IllegalArgumentException("Secant produced invalid x");

            double fxn_plus_1 = evaluateFunction(functionField.getText(), xn_plus_1);

            //Check Stopping Criteria
            if (Math.abs(fxn_plus_1) < epsilon) {
                return xn_plus_1;
            }

            if (Math.abs(xn_plus_1 - xn) < epsilon) {
                return xn_plus_1;
            }

            //Update for Next Iteration
            xn_minus_1 = xn;
            xn = xn_plus_1;
        }

        //Return Final Approximation
        return xn;
    }

    //Newton Method
    private double newtonMethod(double a, double b, double epsilon) {
        //Iteration Counter Reset
        iterationCount = 0;

        // For simplicity, we'll use midpoint or one of the endpoints
        double x0 = (a + b) / 2.0;  // Start with midpoint

        double fx0 = evaluateFunction(functionField.getText(), x0);

        if (Double.isNaN(fx0) || Double.isInfinite(fx0)) {
            throw new IllegalArgumentException("Functia trebuie sa fie finita in x0!");
        }

        //MAX Iterations for Safety
        int maxIterations = 1000;

        double xn = x0;
        double xn_plus_1;

        //Newton Method Loop
        while (iterationCount < maxIterations) {
            iterationCount++;

            /* Step 2: Calculate next approximation using Newton's formula
            xn+1 = xn - f(xn) / f'(xn)*/

            double fxn = evaluateFunction(functionField.getText(), xn);

            if (Double.isNaN(fxn) || Double.isInfinite(fxn))
                throw new IllegalArgumentException("f(xn) invalid");

            double f_prime_xn = evaluateDerivative(functionField.getText(), xn, epsilon);

            if (Double.isNaN(f_prime_xn) || Double.isInfinite(f_prime_xn))
                throw new IllegalArgumentException("Derivative invalid");

            if (Math.abs(f_prime_xn) < 1e-12)
                throw new IllegalArgumentException("Derivative too small");

            xn_plus_1 = xn - fxn / f_prime_xn;

            if (Double.isNaN(xn_plus_1) || Double.isInfinite(xn_plus_1))
                throw new IllegalArgumentException("Newton produced invalid value");

            // If Newton jumps outside interval, stop this subinterval
            if (xn_plus_1 < a || xn_plus_1 > b) {
                return xn;  // return last valid approximation
            }

            double fxn_plus_1 = evaluateFunction(functionField.getText(), xn_plus_1);

            //Check Stopping Criteria
            if (Math.abs(fxn_plus_1) < epsilon) {
                return xn_plus_1;
            }

            if (Math.abs(xn_plus_1 - xn) < epsilon) {
                return xn_plus_1;
            }

            //Update for Next Iteration
            xn = xn_plus_1;
        }

        //Return Final Approximation
        return xn;
    }

    //Helper Method to Calculate Numerical Derivative f'(x)
    private double evaluateDerivative(String funcStr, double x, double epsilon) {
        /* Using numerical differentiation (forward difference)
        f'(x) ≈ (f(x + h) - f(x)) / h */
        double h = epsilon * 10;  // Small step size

        double fx = evaluateFunction(funcStr, x);
        double fx_plus_h = evaluateFunction(funcStr, x + h);

        return (fx_plus_h - fx) / h;
    }

    //Looking to Find ALL Roots
    private java.util.List<Double> findAllRoots(double a, double b, double epsilon, String methodName) {
        java.util.List<Double> roots = new java.util.ArrayList<>();

        //Dividing the Interval into Smaller Sub-Intervals
        int numSubIntervals = 100;  //Number of Sub-Intervals to Check
        double step = (b - a) / numSubIntervals;

        for (int i = 0; i < numSubIntervals; i++) {
            double subA = a + i * step;
            double subB = a + (i + 1) * step;

            double fSubA = evaluateFunction(functionField.getText(), subA);
            double fSubB = evaluateFunction(functionField.getText(), subB);

            // Checking for a Potential Root
            try {
                double root;

                // Bracketing methods require sign change
                if (methodName.equals("Metoda Bisectiei") ||
                        methodName.equals("Metoda Coardelor")) {

                    if (fSubA * fSubB >= 0)
                        continue;  // skip interval if no sign change

                    if (methodName.equals("Metoda Bisectiei"))
                        root = bisectionMethod(subA, subB, epsilon);
                    else
                        root = chordMethod(subA, subB, epsilon);
                }

                // Open methods (Newton, Secant) do NOT require sign change
                else if (methodName.equals("Metoda Secantelor")) {
                    root = secantMethod(subA, subB, epsilon);
                }
                else if (methodName.equals("Metoda Tangentei (Newton)")) {
                    root = newtonMethod(subA, subB, epsilon);
                    if (Double.isNaN(root) || Double.isInfinite(root))
                        continue;
                }
                else {
                    continue;
                }

                    // Check Root for Duplicates, just so we can avoid them
                    boolean isDuplicate = false;
                    for (Double existingRoot : roots) {
                        if (Math.abs(existingRoot - root) < epsilon * 10) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (!isDuplicate) {
                        roots.add(root);
                    }
                } catch (Exception e) {
                    //Skiping Sub-Interval if Method Fails
                }
            }


        return roots;
    }

    private double evaluateFunction(String funcStr, double x) {

        if (Double.isNaN(x) || Double.isInfinite(x))
            return Double.NaN;

        try {
            String expression = funcStr.trim().replace(" ", "");
            expression = replaceVariable(expression, x);
            return evaluateExpression(expression);

        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private String replaceVariable(String expression, double x) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < expression.length()) {
            if (i < expression.length() && expression.charAt(i) == 'x') {
                // Make sure 'x' is not part of 'exp'
                boolean partOfExp = (i >= 2 && i < expression.length() &&
                        expression.substring(Math.max(0, i-2), Math.min(expression.length(), i+1)).equals("exp"));

                if (!partOfExp) {
                    // Replace x with its value in parentheses
                    result.append("(").append(x).append(")");
                } else {
                    result.append('x');
                }
            } else {
                result.append(expression.charAt(i));
            }
            i++;
        }

        return result.toString();
    }

    private double evaluateExpression(String expr) throws Exception {
        // Handle mathematical functions first
        expr = evaluateFunctions(expr);

        // Now evaluate the arithmetic expression
        return evaluateArithmetic(expr);
    }

    private String evaluateFunctions(String expr) throws Exception {
        // Evaluate trigonometric functions: sin, cos, tan
        expr = evaluateMathFunction(expr, "sin", (arg) -> Math.sin(arg));
        expr = evaluateMathFunction(expr, "cos", (arg) -> Math.cos(arg));
        expr = evaluateMathFunction(expr, "tan", (arg) -> Math.tan(arg));

        // Evaluate logarithmic functions: ln, log
        expr = evaluateMathFunction(expr, "ln", (arg) -> Math.log(arg));
        expr = evaluateMathFunction(expr, "log", (arg) -> Math.log10(arg));

        // Evaluate exponential function: e^x or exp(x)
        expr = evaluateMathFunction(expr, "exp", (arg) -> Math.exp(arg));

        // Evaluate square root
        expr = evaluateMathFunction(expr, "sqrt", (arg) -> Math.sqrt(arg));

        // Replace mathematical constants
        expr = expr.replace("pi", String.valueOf(Math.PI));
        expr = expr.replace("e", String.valueOf(Math.E));

        return expr;
    }

    // Functional interface for math operations
    @FunctionalInterface
    private interface MathFunction {
        double apply(double arg);
    }

    private String evaluateMathFunction(String expr, String funcName, MathFunction func) throws Exception {
        while (expr.contains(funcName + "(")) {
            int startIdx = expr.indexOf(funcName + "(");
            int openParen = startIdx + funcName.length();
            int closeParen = findMatchingParen(expr, openParen);

            if (closeParen == -1) {
                throw new Exception("Mismatched parentheses in function " + funcName);
            }

            // Extract the argument
            String argument = expr.substring(openParen + 1, closeParen);
            double argValue = evaluateArithmetic(argument);

            // Apply the function
            double result = func.apply(argValue);

            // Replace the function call with its result
            expr = expr.substring(0, startIdx) + result + expr.substring(closeParen + 1);
        }

        return expr;
    }

    private int findMatchingParen(String expr, int openIdx) {
        int count = 1;
        for (int i = openIdx + 1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') count++;
            else if (expr.charAt(i) == ')') {
                count--;
                if (count == 0) return i;
            }
        }
        return -1;
    }

    private double evaluateArithmetic(String expr) throws Exception {
        expr = expr.trim();

        // Handle negative numbers at the start
        if (expr.startsWith("-")) {
            expr = "0" + expr;
        }

        // Order of operations: parentheses, exponents, multiply/divide, add/subtract

        // 1. Evaluate parentheses first
        while (expr.contains("(")) {
            int closeIdx = expr.indexOf(")");
            if (closeIdx == -1) throw new Exception("Mismatched parentheses");

            // Find the matching opening parenthesis
            int openIdx = closeIdx;
            int count = 1;
            for (int i = closeIdx - 1; i >= 0; i--) {
                if (expr.charAt(i) == ')') count++;
                else if (expr.charAt(i) == '(') {
                    count--;
                    if (count == 0) {
                        openIdx = i;
                        break;
                    }
                }
            }

            String inner = expr.substring(openIdx + 1, closeIdx);
            double result = evaluateArithmetic(inner);
            expr = expr.substring(0, openIdx) + result + expr.substring(closeIdx + 1);
        }

        // 2. Handle exponents (^)
        while (expr.contains("^")) {
            int opIdx = expr.indexOf("^");

            // Find left operand
            int leftStart = opIdx - 1;
            while (leftStart > 0 && (Character.isDigit(expr.charAt(leftStart - 1)) ||
                    expr.charAt(leftStart - 1) == '.' ||
                    expr.charAt(leftStart - 1) == '-')) {
                leftStart--;
            }

            // Find right operand
            int rightEnd = opIdx + 1;
            if (rightEnd < expr.length() && expr.charAt(rightEnd) == '-') {
                rightEnd++; // Include negative sign
            }
            while (rightEnd < expr.length() && (Character.isDigit(expr.charAt(rightEnd)) ||
                    expr.charAt(rightEnd) == '.')) {
                rightEnd++;
            }

            double left = Double.parseDouble(expr.substring(leftStart, opIdx));
            double right = Double.parseDouble(expr.substring(opIdx + 1, rightEnd));
            double result = Math.pow(left, right);

            expr = expr.substring(0, leftStart) + result + expr.substring(rightEnd);
        }

        // 3. Handle multiplication and division (left to right)
        expr = evaluateMultiplicationDivision(expr);

        // 4. Handle addition and subtraction (left to right)
        expr = evaluateAdditionSubtraction(expr);

        return Double.parseDouble(expr);
    }

    private String evaluateMultiplicationDivision(String expr) throws Exception {
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);

            if (c == '*' || c == '/') {
                // Find left operand
                int leftStart = i - 1;
                while (leftStart > 0 && (Character.isDigit(expr.charAt(leftStart - 1)) ||
                        expr.charAt(leftStart - 1) == '.' ||
                        (expr.charAt(leftStart - 1) == '-' && leftStart == 1))) {
                    leftStart--;
                }

                // Find right operand
                int rightEnd = i + 1;
                if (rightEnd < expr.length() && expr.charAt(rightEnd) == '-') {
                    rightEnd++; // Include negative sign
                }
                while (rightEnd < expr.length() && (Character.isDigit(expr.charAt(rightEnd)) ||
                        expr.charAt(rightEnd) == '.')) {
                    rightEnd++;
                }

                double left = Double.parseDouble(expr.substring(leftStart, i));
                double right = Double.parseDouble(expr.substring(i + 1, rightEnd));

                double result;
                if (c == '*') {
                    result = left * right;
                } else {
                    if (right == 0) throw new Exception("Division by zero");
                    result = left / right;
                }

                expr = expr.substring(0, leftStart) + result + expr.substring(rightEnd);
                i = leftStart;
            } else {
                i++;
            }
        }

        return expr;
    }

    private String evaluateAdditionSubtraction(String expr) throws Exception {
        // Skip the first character if it's a sign
        int i = (expr.startsWith("-") || expr.startsWith("+")) ? 1 : 0;

        while (i < expr.length()) {
            char c = expr.charAt(i);

            if ((c == '+' || c == '-') && i > 0) {
                // Find left operand
                int leftStart = i - 1;
                while (leftStart > 0 && (Character.isDigit(expr.charAt(leftStart - 1)) ||
                        expr.charAt(leftStart - 1) == '.')) {
                    leftStart--;
                }

                // Check if there's a sign before the left operand
                if (leftStart > 0 && (expr.charAt(leftStart - 1) == '-' || expr.charAt(leftStart - 1) == '+')) {
                    leftStart--;
                }

                // Find right operand
                int rightEnd = i + 1;
                if (rightEnd < expr.length() && expr.charAt(rightEnd) == '-') {
                    rightEnd++; // Include negative sign
                }
                while (rightEnd < expr.length() && (Character.isDigit(expr.charAt(rightEnd)) ||
                        expr.charAt(rightEnd) == '.')) {
                    rightEnd++;
                }

                double left = Double.parseDouble(expr.substring(leftStart, i));
                double right = Double.parseDouble(expr.substring(i + 1, rightEnd));

                double result;
                if (c == '+') {
                    result = left + right;
                } else {
                    result = left - right;
                }

                expr = expr.substring(0, leftStart) + result + expr.substring(rightEnd);
                i = leftStart;
            } else {
                i++;
            }
        }

        return expr;
    }

    //Inner Class for Graph Panel
    class GraphPanel extends JPanel {
        private String function;
        private double a, b;
        private java.util.List<Double> roots;
        private boolean hasData = false;
        private double minY, maxY;

        //Keeping the Old Method for backward compatibility (in case, if needed)
        public void setFunction(String function, double a, double b, double root) {
            java.util.List<Double> singleRoot = new java.util.ArrayList<>();
            singleRoot.add(root);
            setFunctionWithMultipleRoots(function, a, b, singleRoot);
        } //maybe won't need it, but IDK yet if it works without

        public void setFunctionWithMultipleRoots(String function, double a, double b, java.util.List<Double> roots) {
            this.function = function;
            this.a = a;
            this.b = b;
            this.roots = roots;
            this.hasData = true;

            //Y MIN & MAX for Proper Scaling
            calculateYRange();
        }

        private void calculateYRange() {
            minY = Double.MAX_VALUE;
            maxY = Double.MIN_VALUE;

            int samples = 200;
            for (int i = 0; i <= samples; i++) {
                double x = a + (b - a) * i / samples;
                double y = MainFrame.this.evaluateFunction(function, x);
                if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            }

            double range = maxY - minY;
            minY -= range * 0.1;
            maxY += range * 0.1;

            if (minY > 0) minY = Math.min(minY, 0);
            if (maxY < 0) maxY = Math.max(maxY, 0);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int paddingLeft = 60;
            int paddingRight = 30;
            int paddingTop = 40;
            int paddingBottom = 50;

            int graphWidth = width - paddingLeft - paddingRight;
            int graphHeight = height - paddingTop - paddingBottom;

            //White BG
            g2.setColor(Color.WHITE);
            g2.fillRect(paddingLeft, paddingTop, graphWidth, graphHeight);

            //Border
            g2.setColor(Color.BLACK);
            g2.drawRect(paddingLeft, paddingTop, graphWidth, graphHeight);

            if (!hasData) {
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                g2.drawString("Asteptare date...", width / 2 - 50, height / 2);
                return;
            }

            //Grid
            g2.setColor(new Color(230, 230, 230));
            g2.setStroke(new BasicStroke(1));

            //Vertical Grid Lines
            for (int i = 0; i <= 10; i++) {
                int x = paddingLeft + (graphWidth * i) / 10;
                g2.drawLine(x, paddingTop, x, paddingTop + graphHeight);
            }

            //Horizontal Grid Lines
            for (int i = 0; i <= 10; i++) {
                int y = paddingTop + (graphHeight * i) / 10;
                g2.drawLine(paddingLeft, y, paddingLeft + graphWidth, y);
            }

            //Axes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));

            //Position of y=0 Axis
            int zeroY = paddingTop + graphHeight - (int)((0 - minY) / (maxY - minY) * graphHeight);

            // X-axis (y=0 line)
            if (zeroY >= paddingTop && zeroY <= paddingTop + graphHeight) {
                g2.drawLine(paddingLeft, zeroY, paddingLeft + graphWidth, zeroY);
            }

            // Y-axis (x=0 line) - only if 0 is in range
            if (a <= 0 && b >= 0) {
                int zeroX = paddingLeft + (int)((0 - a) / (b - a) * graphWidth);
                g2.drawLine(zeroX, paddingTop, zeroX, paddingTop + graphHeight);
            }

            //X-axis Labels
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(Color.BLACK);
            for (int i = 0; i <= 10; i++) {
                double xVal = a + (b - a) * i / 10;
                int x = paddingLeft + (graphWidth * i) / 10;
                String label = String.format("%.2f", xVal);
                g2.drawString(label, x - 15, paddingTop + graphHeight + 20);
            }

            //Y-axis Labels
            for (int i = 0; i <= 10; i++) {
                double yVal = minY + (maxY - minY) * (10 - i) / 10;
                int y = paddingTop + (graphHeight * i) / 10;
                String label = String.format("%.2f", yVal);
                g2.drawString(label, paddingLeft - 45, y + 5);
            }

            //Axis Labels
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("x", width - paddingRight + 5, paddingTop + graphHeight / 2);
            g2.drawString("f(x)", paddingLeft / 2 - 10, paddingTop - 10);

            //Function Curve
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(2));

            int prevScreenX = -1, prevScreenY = -1;

            for (int i = 0; i <= graphWidth; i++) {
                double x = a + (b - a) * i / graphWidth;
                double y = MainFrame.this.evaluateFunction(function, x);

                if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                    int screenX = paddingLeft + i;
                    int screenY = paddingTop + graphHeight - (int)((y - minY) / (maxY - minY) * graphHeight);

                    if (prevScreenX != -1 && Math.abs(screenY - prevScreenY) < graphHeight) {
                        g2.drawLine(prevScreenX, prevScreenY, screenX, screenY);
                    }

                    prevScreenX = screenX;
                    prevScreenY = screenY;
                }
            }

            //ALL Root Points
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 11));

            if (roots != null && !roots.isEmpty()) {
                for (int i = 0; i < roots.size(); i++) {

                    double root = roots.get(i);

                    // Convert x to screen coordinates
                    int rootScreenX = paddingLeft +
                            (int)((root - a) / (b - a) * graphWidth);

                    // Root lies on x-axis (y = 0)
                    double rootY = 0;

                    int rootScreenY = paddingTop + graphHeight -
                            (int)((rootY - minY) / (maxY - minY) * graphHeight);

                    // Draw root circle
                    g2.fillOval(rootScreenX - 5, rootScreenY - 5, 10, 10);

                    // Draw label
                    int labelY = (i % 2 == 0) ? rootScreenY - 10 : rootScreenY + 20;
                    g2.drawString(String.format("x=%.3f", root),
                            rootScreenX + 10, labelY);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame gui = new MainFrame();
            gui.setVisible(true);
        });
    }
}
