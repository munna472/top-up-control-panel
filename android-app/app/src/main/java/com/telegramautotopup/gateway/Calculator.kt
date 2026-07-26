package com.telegramautotopup.gateway

object Calculator {
    fun evaluateExpression(expr: String): Double? {
        val clean = expr.replace("\\s+".toRegex(), "")
        if (!clean.matches("^[0-9+\\-*/().]+$".toRegex())) {
            return null
        }

        return try {
            object : Any() {
                var pos = -1
                var ch = 0

                fun nextChar() {
                    ch = if (++pos < clean.length) clean[pos].code else -1
                }

                fun eat(charToEat: Int): Boolean {
                    while (ch == ' '.code) nextChar()
                    if (ch == charToEat) {
                        nextChar()
                        return true
                    }
                    return false
                }

                fun parse(): Double {
                    nextChar()
                    val x = parseExpression()
                    if (pos < clean.length) throw RuntimeException("Unexpected: " + ch.toChar())
                    return x
                }

                fun parseExpression(): Double {
                    var x = parseTerm()
                    while (true) {
                        if (eat('+'.code)) x += parseTerm() // addition
                        else if (eat('-'.code)) x -= parseTerm() // subtraction
                        else return x
                    }
                }

                fun parseTerm(): Double {
                    var x = parseFactor()
                    while (true) {
                        if (eat('*'.code)) x *= parseFactor() // multiplication
                        else if (eat('/'.code)) x /= parseFactor() // division
                        else return x
                    }
                }

                fun parseFactor(): Double {
                    if (eat('+'.code)) return parseFactor() // unary plus
                    if (eat('-'.code)) return -parseFactor() // unary minus

                    var x: Double
                    val startPos = this.pos
                    if (eat('('.code)) { // parentheses
                        x = parseExpression()
                        eat(')'.code)
                    } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                        while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                        x = clean.substring(startPos, this.pos).toDouble()
                    } else {
                        throw RuntimeException("Unexpected: " + ch.toChar())
                    }

                    return x
                }
            }.parse()
        } catch (e: Exception) {
            null
        }
    }
}
