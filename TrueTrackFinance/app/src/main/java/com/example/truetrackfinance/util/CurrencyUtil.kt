package com.example.truetrackfinance.util

import android.util.Log
import java.text.NumberFormat
import java.util.*

/**
 * CurrencyUtil handles robust numeric parsing and formatting for the ZAR (R) currency.
 * Essential for "Zero Bug" stability when handling diverse user inputs and regional settings.
 */
object CurrencyUtil {

    private val TAG = "CurrencyUtil"

    private val zarFormat: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
    }

    /** Format a Double as a ZAR currency string, e.g. "R 1 234,56". */
    fun formatZar(amount: Double): String = zarFormat.format(amount)

    /** Format with explicit ZAR code, ensuring professional high-fidelity output. */
    fun format(amount: Double, currencyCode: String = "ZAR"): String {
        return try {
            val fmt = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
            fmt.currency = Currency.getInstance(currencyCode)
            fmt.format(amount)
        } catch (e: Exception) {
            Log.e(TAG, "Formatting fault: falling back to basic string format", e)
            String.format(Locale.US, "R %.2f", amount)
        }
    }

    /** 
     * Robustly parse a user-entered amount string to a Double. 
     * Requirement: Handle invalid inputs without crashing.
     * Logic: Handles both dot and comma as decimal separators and ignores thousands markers.
     */
    fun parseAmount(input: String): Double? {
        if (input.isBlank()) {
            Log.v(TAG, "Parsing: Empty input received")
            return null
        }
        
        try {
            // Replace comma with dot to standardize decimal logic
            val standardized = input.replace(",", ".")
            
            // Complex Regex: Remove everything except digits and the LAST dot 
            // (to accurately handle potential multiple dots or thousands separators)
            val parts = standardized.split(".")
            val cleaned = if (parts.size > 1) {
                val integerPart = parts.subList(0, parts.size - 1).joinToString("").replace(Regex("[^0-9]"), "")
                val decimalPart = parts.last().replace(Regex("[^0-9]"), "")
                "$integerPart.$decimalPart"
            } else {
                standardized.replace(Regex("[^0-9]"), "")
            }
            
            val result = cleaned.toDoubleOrNull()
            Log.v(TAG, "Parsing Logic: input '$input' -> cleaned '$cleaned' -> result '$result'")
            return result

        } catch (e: Exception) {
            Log.e(TAG, "Critical Parsing Fault: unexpected format in '$input'", e)
            return null
        }
    }
}
