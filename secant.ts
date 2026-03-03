function f(x: number) {
    // x * cosx * sinx
    // return x * Math.cos(x) * Math.sin(x)

    // e^{x}-5
    return Math.E ** x - 5
}

function secant(a: number, b: number, epsilon: number) {
    let xprev = a, xn = b, xnext
    console.log('x0', a, f(a))
    console.log('x1', b, f(b))
    for (let n = 1; n < 400; n++) {
        const fn = f(xn), fprev = f(xprev)
        xnext = xn - fn * (xn - xprev) / (fn - fprev)
        const fnext = f(xnext)
        console.log(`x${n + 1}`, xnext, fnext)
        console.log(`|f${n + 1}|`, Math.abs(fnext))
        console.log(`|x${n + 1} - x${n}|`, Math.abs(xnext - xn))
        if (Math.abs(fnext) < epsilon || Math.abs(xnext - xn) < epsilon) return xnext
        xprev = xn
        xn = xnext
    }
}

secant(-9, 3, 0.1 ** 10)