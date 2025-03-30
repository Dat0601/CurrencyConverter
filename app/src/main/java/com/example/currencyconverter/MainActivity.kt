// MainActivity.kt
package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Khai báo các thành phần giao diện
    private lateinit var sourceAmountEditText: EditText
    private lateinit var sourceCurrencySpinner: Spinner
    private lateinit var targetAmountEditText: EditText
    private lateinit var targetCurrencySpinner: Spinner
    private lateinit var exchangeRateTextView: TextView
    private lateinit var sourceCurrencySymbolTextView: TextView
    private lateinit var targetCurrencySymbolTextView: TextView

    // Danh sách các đồng tiền hỗ trợ đã được cập nhật
    private val currencies = listOf(
        "United States - Dollar",
        "Vietnam - Dong",
        "Japan - Yen",
        "Hong Kong - Dollar",
        "Korea - Won"
    )

    // Tỷ giá cố định so với USD (1 USD = x đơn vị tiền tệ)
    private val exchangeRates = mapOf(
        "United States - Dollar" to 1.0,
        "Vietnam - Dong" to 23185.0,
        "Japan - Yen" to 107.5,
        "Hong Kong - Dollar" to 7.76,
        "Korea - Won" to 1135.0
    )

    // Biểu tượng tiền tệ
    private val currencySymbols = mapOf(
        "United States - Dollar" to "$",
        "Vietnam - Dong" to "₫",
        "Japan - Yen" to "¥",
        "Hong Kong - Dollar" to "HK$",
        "Korea - Won" to "₩"
    )

    // Biến để kiểm soát việc cập nhật tự động
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo các thành phần giao diện
        sourceAmountEditText = findViewById(R.id.sourceAmountEditText)
        sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner)
        targetAmountEditText = findViewById(R.id.targetAmountEditText)
        targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner)
        exchangeRateTextView = findViewById(R.id.exchangeRateTextView)
        sourceCurrencySymbolTextView = findViewById(R.id.sourceCurrencySymbol)
        targetCurrencySymbolTextView = findViewById(R.id.targetCurrencySymbol)

        // Thiết lập adapter cho spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sourceCurrencySpinner.adapter = adapter
        targetCurrencySpinner.adapter = adapter

        // Thiết lập giá trị mặc định
        sourceCurrencySpinner.setSelection(0) // USD
        targetCurrencySpinner.setSelection(1) // VND

        // Thiết lập các sự kiện
        setupListeners()

        // Cập nhật tỷ giá hiển thị ban đầu
        updateExchangeRateText()

        // Cập nhật ký hiệu tiền tệ ban đầu
        updateCurrencySymbols()
    }

    private fun setupListeners() {
        // Sự kiện khi thay đổi giá trị trong EditText nguồn
        sourceAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    convertCurrency(true)
                }
            }
        })

        // Sự kiện khi thay đổi giá trị trong EditText đích
        targetAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    convertCurrency(false)
                }
            }
        })

        // Sự kiện khi thay đổi loại tiền tệ nguồn
        sourceCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCurrencySymbols()
                convertCurrency(true)
                updateExchangeRateText()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Sự kiện khi thay đổi loại tiền tệ đích
        targetCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCurrencySymbols()
                convertCurrency(true)
                updateExchangeRateText()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Cập nhật ký hiệu tiền tệ cho cả nguồn và đích
    private fun updateCurrencySymbols() {
        // Cập nhật ký hiệu tiền tệ nguồn
        val sourceCurrency = sourceCurrencySpinner.selectedItem.toString()
        val sourceSymbol = currencySymbols[sourceCurrency] ?: ""
        sourceCurrencySymbolTextView.text = sourceSymbol

        // Cập nhật ký hiệu tiền tệ đích
        val targetCurrency = targetCurrencySpinner.selectedItem.toString()
        val targetSymbol = currencySymbols[targetCurrency] ?: ""
        targetCurrencySymbolTextView.text = targetSymbol
    }

    private fun updateExchangeRateText() {
        val sourceCurrency = sourceCurrencySpinner.selectedItem.toString()
        val targetCurrency = targetCurrencySpinner.selectedItem.toString()

        val sourceRate = exchangeRates[sourceCurrency] ?: 1.0
        val targetRate = exchangeRates[targetCurrency] ?: 1.0

        val rate = targetRate / sourceRate

        exchangeRateTextView.text = "1 ${sourceCurrency.split(" - ")[0]} = ${String.format("%.2f", rate)} ${targetCurrency.split(" - ")[0]}"
    }

    private fun convertCurrency(sourceToTarget: Boolean) {
        isUpdating = true

        try {
            val sourceCurrency = sourceCurrencySpinner.selectedItem.toString()
            val targetCurrency = targetCurrencySpinner.selectedItem.toString()

            val sourceRate = exchangeRates[sourceCurrency] ?: 1.0
            val targetRate = exchangeRates[targetCurrency] ?: 1.0

            if (sourceToTarget) {
                val sourceText = sourceAmountEditText.text.toString()
                if (sourceText.isNotEmpty()) {
                    val sourceAmount = sourceText.toDouble()
                    val targetAmount = sourceAmount * (targetRate / sourceRate)
                    targetAmountEditText.setText(String.format("%.2f", targetAmount))
                } else {
                    targetAmountEditText.setText("")
                }
            } else {
                val targetText = targetAmountEditText.text.toString()
                if (targetText.isNotEmpty()) {
                    val targetAmount = targetText.toDouble()
                    val sourceAmount = targetAmount * (sourceRate / targetRate)
                    sourceAmountEditText.setText(String.format("%.2f", sourceAmount))
                } else {
                    sourceAmountEditText.setText("")
                }
            }
        } catch (e: Exception) {
            // Xử lý lỗi
        }

        isUpdating = false
    }
}