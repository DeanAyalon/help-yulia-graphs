double evaluateFunction(double x) {
    // System.out.println("Evaluating e^" + x + " - 5");
    return Math.pow(Math.E, x) - 5;
}

double secantMethod(double a, double b, double epsilon) {
    System.out.println("x0 = " + a + ", f(" + a + ") = " + evaluateFunction(a));
    System.out.println("x1 = " + b + ", f(" + b + ") = " + evaluateFunction(b));
    for (int n = 2; n < 400; n++) {
        final double fb = evaluateFunction(b);
        final double fa = evaluateFunction(a);
        final double c = b - fb * (b - a) / (fb - fa);
        final double fc = evaluateFunction(c);
        System.out.println("x" + n + " = " + c + ", f(" + c + ") = " + evaluateFunction(c));
        System.out.println("|f" + n + "| = " + Math.abs(fc));
        System.out.println("|x" + n + " - x" + (n - 1) + "| = " + Math.abs(c - b));
        if (Math.abs(fc) < epsilon || Math.abs(c - b) < epsilon) return c;
        a = b;
        b = c;
    }
    return Double.NaN;
}

System.out.println(secantMethod(-9, 3, Math.pow(.1, 10)));

// double secantMethod(double a, double b, double epsilon, double delta) {
//     //Iteration Counter Reset
//     iterationCount = 0;

//     // Start with two slightly different points inside interval
//     double x0 = a;
//     double x1 = (b + a) * .5;

//     // If they are too close, shift slightly
//     if (Math.abs(x1 - x0) < epsilon) x1 = x0 + epsilon * 10;

//     double fx0 = evaluateFunction(x0);
//     double fx1 = evaluateFunction(x1);

//     if (Double.isNaN(fx0) || Double.isInfinite(fx0) ||
//             Double.isNaN(fx1) || Double.isInfinite(fx1)) {
//         throw new IllegalArgumentException("Functia trebuie sa fie finita in x0 si x1!");
//     }

//     if (Math.abs(fx1 - fx0) < epsilon) {
//         throw new IllegalArgumentException("f(x0) și f(x1) trebuie să fie diferite!");
//     }

//     //MAX Iterations for Safety
//     int maxIterations = 1000;

//     double xn = x1;
//     double xn_minus_1 = x0;
//     double xn_plus_1;

//     //Secant Method Loop
//     while (iterationCount < maxIterations) {
//         iterationCount++;

//         /*Calculating next approximation using secant formula
//         xn+1 = xn - f(xn) * (xn - xn-1) / (f(xn) - f(xn-1)) */
//         double fxn = evaluateFunction(functionField.getText(), xn);
//         double fxn_minus_1 = evaluateFunction(functionField.getText(), xn_minus_1);

//         if (Double.isNaN(fxn) || Double.isInfinite(fxn) ||
//                 Double.isNaN(fxn_minus_1) || Double.isInfinite(fxn_minus_1))
//             throw new IllegalArgumentException("Secant produced invalid function value");

//         //Check if Denominator is NOT 0 (zero)
//         if (Math.abs(fxn - fxn_minus_1) < epsilon * 0.001) {
//             throw new IllegalArgumentException("f(xn) - f(xn-1) este prea mic (diviziune la zero)!");
//         }

//         xn_plus_1 = xn - fxn * (xn - xn_minus_1) / (fxn - fxn_minus_1);
//         if (Double.isNaN(xn_plus_1) || Double.isInfinite(xn_plus_1))
//             throw new IllegalArgumentException("Secant produced invalid x");

//         double fxn_plus_1 = evaluateFunction(functionField.getText(), xn_plus_1);

//         //Check Stopping Criteria
//         if (Math.abs(fxn_plus_1) < epsilon) {
//             return xn_plus_1;
//         }

//         if (Math.abs(xn_plus_1 - xn) < epsilon) {
//             return xn_plus_1;
//         }

//         //Update for Next Iteration
//         xn_minus_1 = xn;
//         xn = xn_plus_1;
//     }

//     //Return Final Approximation
//     return xn;
// }





/exit // for jshell
