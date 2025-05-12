package com.tico.calculadoracientifica

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvPrimary: TextView
    private lateinit var tvSecondary: TextView
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private var currentExpression = ""
    private var memoryValue: Double = 0.0
    private var isSecondFunctionActive = false
    private var calculationCompleted = false

    // Funções personalizadas
    private val sinFunc = object : Function("sin", 1) {
        override fun apply(vararg args: Double): Double = sin(args[0])
    }
    private val cosFunc = object : Function("cos", 1) {
        override fun apply(vararg args: Double): Double = cos(args[0])
    }
    private val tanFunc = object : Function("tan", 1) {
        override fun apply(vararg args: Double): Double = tan(args[0])
    }
    private val log10Func = object : Function("log10", 1) {
        override fun apply(vararg args: Double): Double = log10(args[0])
    }
    private val lnFunc = object : Function("log", 1) {
        override fun apply(vararg args: Double): Double = ln(args[0])
    }
    private val factFunc = object : Function("fact", 1) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toInt()
            if (n < 0) throw IllegalArgumentException("Fatorial de negativo não existe")
            return (1..n).fold(1.0) { acc, i -> acc * i }
        }
    }
    private val absFunc = object : Function("abs", 1) {
        override fun apply(vararg args: Double): Double = abs(args[0])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPrimary = findViewById(R.id.idTVprimary)
        tvSecondary = findViewById(R.id.idTVSecondary)

        setNumericOnClickListeners()
        setOperatorOnClickListeners()
        setScientificOnClickListeners()
        setAdditionalScientificListeners()
        setMemoryOnClickListeners()
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
                    calculationCompleted = false // Resetar a flag quando um operador é pressionado
                    val buttonText = (it as Button).text.toString()
                    tvPrimary.append(buttonText)

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
                            tvPrimary.text = if (isSecondFunctionActive) "arcsin($currentText)" else "sin($currentText)"
                            currentExpression = if (isSecondFunctionActive) "asin($currentExpression)" else "sin($currentExpression)"
                        }
                        "cos" -> {
                            tvPrimary.text = if (isSecondFunctionActive) "arccos($currentText)" else "cos($currentText)"
                            currentExpression = if (isSecondFunctionActive) "acos($currentExpression)" else "cos($currentExpression)"
                        }
                        "tan" -> {
                            tvPrimary.text = if (isSecondFunctionActive) "arctan($currentText)" else "tan($currentText)"
                            currentExpression = if (isSecondFunctionActive) "atan($currentExpression)" else "tan($currentExpression)"
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
                            tvPrimary.text = "($currentText)!"
                            currentExpression = "fact($currentExpression)"
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

    private fun setAdditionalScientificListeners() {
        findViewById<Button>(R.id.bsecond).setOnClickListener {
            isSecondFunctionActive = !isSecondFunctionActive
            updateSecondFunctionButtons()
        }

        findViewById<Button>(R.id.bpow).setOnClickListener {
            if (tvPrimary.text.isNotEmpty() && !stateError) {
                tvPrimary.append("^")
                currentExpression += "^"
                lastNumeric = false
            }
        }

        findViewById<Button>(R.id.b10pow).setOnClickListener {
            if (tvPrimary.text.isNotEmpty() && !stateError) {
                val currentText = tvPrimary.text.toString()
                tvPrimary.text = "10^($currentText)"
                currentExpression = "10^($currentExpression)"
                lastNumeric = true
            }
        }

        findViewById<Button>(R.id.babs).setOnClickListener {
            if (tvPrimary.text.isNotEmpty() && !stateError) {
                val currentText = tvPrimary.text.toString()
                tvPrimary.text = "|$currentText|"
                currentExpression = "abs($currentExpression)"
                lastNumeric = true
            }
        }
    }

    private fun setMemoryOnClickListeners() {
        findViewById<Button>(R.id.bmc).setOnClickListener {
            memoryValue = 0.0
            Toast.makeText(this, "Memória limpa", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.bmr).setOnClickListener {
            if (memoryValue != 0.0) {
                val memoryText = if (memoryValue % 1 == 0.0) {
                    memoryValue.toInt().toString()
                } else {
                    memoryValue.toString()
                }

                if (stateError) {
                    tvPrimary.text = memoryText
                    currentExpression = memoryText
                    stateError = false
                } else {
                    tvPrimary.append(memoryText)
                    currentExpression += memoryText
                }
                lastNumeric = true
            }
        }

        findViewById<Button>(R.id.bmplus).setOnClickListener {
            try {
                val result = ExpressionBuilder(currentExpression)
                    .build()
                    .evaluate()
                memoryValue += result
                Toast.makeText(this, "Valor adicionado à memória", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(this, "Erro ao adicionar à memória", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.bmminus).setOnClickListener {
            try {
                val result = ExpressionBuilder(currentExpression)
                    .build()
                    .evaluate()
                memoryValue -= result
                Toast.makeText(this, "Valor subtraído da memória", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(this, "Erro ao subtrair da memória", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setControlOnClickListeners() {
        findViewById<Button>(R.id.bac).setOnClickListener {
            tvPrimary.text = ""
            tvSecondary.text = ""
            currentExpression = ""
            lastNumeric = false
            stateError = false
            lastDot = false
        }

        findViewById<Button>(R.id.bc).setOnClickListener {
            val currentText = tvPrimary.text.toString()
            if (currentText.isNotEmpty()) {
                tvPrimary.text = currentText.dropLast(1)
                currentExpression = currentExpression.dropLast(1)

                if (currentExpression.isEmpty()) {
                    tvSecondary.text = ""
                    lastNumeric = false
                } else {
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

        findViewById<Button>(R.id.bequal).setOnClickListener {
            if (currentExpression.isNotEmpty() && !stateError) {
                evaluateExpression()
            }
        }
    }

    private fun onNumericButtonClick(button: Button) {
        if (calculationCompleted || stateError) {
            tvPrimary.text = button.text
            currentExpression = button.text.toString()
            stateError = false
            calculationCompleted = false
        } else {
            tvPrimary.append(button.text)
            currentExpression += button.text
        }
        lastNumeric = true
    }

    private fun evaluateExpression() {
        try {
            var expressionToEvaluate = currentExpression
                .replace("π", "pi")
                .replace("×", "*")
                .replace("÷", "/")
                .replace("√", "sqrt")
                .replace("²", "^2")
                .replace("|", "abs(")

            val result = ExpressionBuilder(expressionToEvaluate)
                .functions(sinFunc, cosFunc, tanFunc, log10Func, lnFunc, factFunc, absFunc)
                .build()
                .evaluate()

            tvSecondary.text = if (result % 1 == 0.0) {
                result.toInt().toString()
            } else {
                result.toString()
            }

            stateError = false
            calculationCompleted = true // Sinaliza que um cálculo foi concluído
        } catch (ex: Exception) {
            tvSecondary.text = "Error"
            stateError = true
        }
    }

    private fun updateSecondFunctionButtons() {
        val color = if (isSecondFunctionActive) "#FF5722" else "#ffa500"

        listOf(R.id.bsin, R.id.bcos, R.id.btan, R.id.blog, R.id.bln,
            R.id.bfact, R.id.bsquare, R.id.bsqrt, R.id.binv).forEach { id ->
            findViewById<Button>(id).setTextColor(Color.parseColor(color))
        }
    }
}