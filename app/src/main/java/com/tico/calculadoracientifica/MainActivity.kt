package com.tico.calculadoracientifica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.*
import android.widget.Button
import android.widget.TextView
import net.objecthunter.exp4j.function.Function

class MainActivity : AppCompatActivity() {

    private lateinit var tvPrimary: TextView
    private lateinit var tvSecondary: TextView
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private var currentExpression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPrimary = findViewById(R.id.idTVprimary)
        tvSecondary = findViewById(R.id.idTVSecondary)

        setNumericOnClickListeners()
        setOperatorOnClickListeners()
        setScientificOnClickListeners()
        setControlOnClickListeners()
    }

    private fun setNumericOnClickListeners() {
        val numericButtons = listOf(
            R.id.b0, R.id.b1, R.id.b2, R.id.b3, R.id.b4,
            R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9, R.id.bdot
        )

        numericButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                onNumericButtonClick(it as Button)
            }
        }

        // Botão π (tratamento especial)
        findViewById<Button>(R.id.bpi).setOnClickListener {
            if (stateError) {
                tvPrimary.text = "π"
                currentExpression = "π"
                stateError = false
            } else {
                tvPrimary.append("π")
                currentExpression += "π"
            }
            lastNumeric = true
        }
    }

    private fun setOperatorOnClickListeners() {
        val operatorButtons = listOf(
            R.id.bplus, R.id.bminus, R.id.bmul, R.id.bdiv,
            R.id.bbrac1, R.id.bbrac2
        )

        operatorButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                if (tvPrimary.text.isNotEmpty() && !stateError) {
                    val buttonText = (it as Button).text.toString()
                    tvPrimary.append(buttonText)

                    // Converte operadores para o formato exp4j
                    when (buttonText) {
                        "×" -> currentExpression += "*"
                        else -> currentExpression += buttonText
                    }

                    lastNumeric = false
                    lastDot = false
                }
            }
        }
    }

    private fun setScientificOnClickListeners() {
        val scientificButtons = listOf(
            R.id.bsin, R.id.bcos, R.id.btan, R.id.blog, R.id.bln,
            R.id.bfact, R.id.bsquare, R.id.bsqrt, R.id.binv
        )

        scientificButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                if (tvPrimary.text.isNotEmpty() && !stateError) {
                    val text = (it as Button).text.toString()
                    val currentText = tvPrimary.text.toString()

                    when (text) {
                        "sin" -> {
                            tvPrimary.text = "sin($currentText)"
                            currentExpression = "sin($currentExpression)"
                        }
                        "cos" -> {
                            tvPrimary.text = "cos($currentText)"
                            currentExpression = "cos($currentExpression)"
                        }
                        "tan" -> {
                            tvPrimary.text = "tan($currentText)"
                            currentExpression = "tan($currentExpression)"
                        }
                        "log" -> {
                            tvPrimary.text = "log($currentText)"
                            currentExpression = "log10($currentExpression)"
                        }
                        "ln" -> {
                            tvPrimary.text = "ln($currentText)"
                            currentExpression = "log($currentExpression)"
                        }
                        "x!" -> {
                            tvPrimary.text = "($currentText)!" // Exibe (5)!
                            currentExpression = "fact($currentExpression)" // Converte para fact(5)
                        }
                        "x²" -> {
                            tvPrimary.text = "($currentText)²"
                            currentExpression = "($currentExpression)^2"
                        }
                        "√" -> {
                            tvPrimary.text = "√($currentText)"
                            currentExpression = "sqrt($currentExpression)"
                        }
                        "1/x" -> {
                            tvPrimary.text = "1/($currentText)"
                            currentExpression = "1/($currentExpression)"
                        }
                    }

                    lastNumeric = true
                }
            }
        }
    }

    private fun setControlOnClickListeners() {
        // Botão AC (limpar tudo)
        findViewById<Button>(R.id.bac).setOnClickListener {
            tvPrimary.text = ""
            tvSecondary.text = ""
            currentExpression = ""
            lastNumeric = false
            stateError = false
            lastDot = false
        }

        // Botão C (apagar último caractere)
        findViewById<Button>(R.id.bc).setOnClickListener {
            val currentText = tvPrimary.text.toString()
            if (currentText.isNotEmpty()) {
                tvPrimary.text = currentText.dropLast(1)
                currentExpression = currentExpression.dropLast(1)

                if (currentExpression.isEmpty()) {
                    tvSecondary.text = ""
                    lastNumeric = false
                } else {
                    // Verifica se a expressão ainda é válida
                    try {
                        val lastChar = currentExpression.last()
                        if (lastChar.isDigit()) {
                            evaluateExpression()
                        }
                    } catch (e: Exception) {
                        tvSecondary.text = ""
                        lastNumeric = false
                    }
                }
            }
        }

        // Botão = (calcular)
        findViewById<Button>(R.id.bequal).setOnClickListener {
            if (currentExpression.isNotEmpty() && !stateError) {
                evaluateExpression()
            }
        }
    }

    private fun onNumericButtonClick(button: Button) {
        if (stateError) {
            tvPrimary.text = button.text
            currentExpression = button.text.toString()
            stateError = false
        } else {
            tvPrimary.append(button.text)
            currentExpression += button.text
        }
        lastNumeric = true
    }

    private fun evaluateExpression() {
        try {
            // Converte a expressão para o formato exp4j
            var expressionToEvaluate = currentExpression
                .replace("π", "pi")
                .replace("×", "*")
                .replace("÷", "/")
                .replace("√", "sqrt")
                .replace("²", "^2")

            // Funções personalizadas
            val sinFunc = object : Function("sin", 1) {
                override fun apply(vararg args: Double): Double = sin(args[0])
            }
            val cosFunc = object : Function("cos", 1) {
                override fun apply(vararg args: Double): Double = cos(args[0])
            }
            val tanFunc = object : Function("tan", 1) {
                override fun apply(vararg args: Double): Double = tan(args[0])
            }
            val log10Func = object : Function("log10", 1) {
                override fun apply(vararg args: Double): Double = log10(args[0])
            }
            val lnFunc = object : Function("log", 1) {
                override fun apply(vararg args: Double): Double = ln(args[0])
            }
            val factFunc = object : Function("fact", 1) {
                override fun apply(vararg args: Double): Double {
                    val n = args[0].toInt()
                    if (n < 0) throw IllegalArgumentException("Fatorial de negativo não existe")
                    return (1..n).fold(1.0) { acc, i -> acc * i }
                }
            }

            // Avalia a expressão
            val result = ExpressionBuilder(expressionToEvaluate)
                .functions(sinFunc, cosFunc, tanFunc, log10Func, lnFunc, factFunc)
                .build()
                .evaluate()

            // Formata o resultado (remove .0 se for inteiro)
            tvSecondary.text = if (result % 1 == 0.0) {
                result.toInt().toString()
            } else {
                result.toString()
            }

            stateError = false
        } catch (ex: Exception) {
            tvSecondary.text = "Error"
            stateError = true
        }
    }
}