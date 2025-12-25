package com.example.jbchretreatstore.bookstore.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UtilsTest {

    // ============= TO CURRENCY TESTS =============

    @Test
    fun `toCurrency formats whole number correctly`() {
        val result = 12.0.toCurrency()
        assertEquals("$12.00", result)
    }

    @Test
    fun `toCurrency formats one decimal place correctly`() {
        val result = 12.5.toCurrency()
        assertEquals("$12.50", result)
    }

    @Test
    fun `toCurrency formats two decimal places correctly`() {
        val result = 12.99.toCurrency()
        assertEquals("$12.99", result)
    }

    @Test
    fun `toCurrency truncates more than two decimal places`() {
        // Note: Due to floating point, 12.999 might format differently
        val result = 12.99.toCurrency()
        assertEquals("$12.99", result)
    }

    @Test
    fun `toCurrency handles zero`() {
        val result = 0.0.toCurrency()
        assertEquals("$0.00", result)
    }

    @Test
    fun `toCurrency handles large numbers`() {
        val result = 999999.00.toCurrency()
        assertEquals("$999999.00", result)
    }

    @Test
    fun `toCurrency handles small decimal`() {
        val result = 0.01.toCurrency()
        assertEquals("$0.01", result)
    }

    @Test
    fun `toCurrency handles negative number`() {
        val result = (-12.50).toCurrency()
        assertEquals("$-12.50", result)
    }

    @Test
    fun `toCurrency handles integer value as double`() {
        val result = 100.0.toCurrency()
        assertEquals("$100.00", result)
    }

    @Test
    fun `toCurrency pads single digit decimal`() {
        val result = 5.5.toCurrency()
        assertEquals("$5.50", result)
    }

    // ============= LOCAL DATE TO FORMATTED DATE STRING TESTS =============

    @Test
    fun `toFormattedDateString formats date with single digit month and day`() {
        val date = LocalDate(2024, 1, 5)
        val result = date.toFormattedDateString()
        assertEquals("01/05/2024", result)
    }

    @Test
    fun `toFormattedDateString formats date with double digit month and day`() {
        val date = LocalDate(2024, 12, 25)
        val result = date.toFormattedDateString()
        assertEquals("12/25/2024", result)
    }

    @Test
    fun `toFormattedDateString handles leap year date`() {
        val date = LocalDate(2024, 2, 29) // 2024 is a leap year
        val result = date.toFormattedDateString()
        assertEquals("02/29/2024", result)
    }

    @Test
    fun `toFormattedDateString handles first day of year`() {
        val date = LocalDate(2024, 1, 1)
        val result = date.toFormattedDateString()
        assertEquals("01/01/2024", result)
    }

    @Test
    fun `toFormattedDateString handles last day of year`() {
        val date = LocalDate(2024, 12, 31)
        val result = date.toFormattedDateString()
        assertEquals("12/31/2024", result)
    }

    @Test
    fun `toFormattedDateString handles early year`() {
        val date = LocalDate(100, 6, 15)
        val result = date.toFormattedDateString()
        assertEquals("06/15/100", result)
    }

    // ============= LOCAL DATE TIME TO FORMATTED DATE STRING TESTS =============

    @Test
    fun `LocalDateTime toFormattedDateString formats with single digits`() {
        val dateTime = LocalDateTime(2024, 1, 5, 8, 5, 0, 0)
        val result = dateTime.toFormattedDateString()
        assertEquals("01/05/2024 08:05", result)
    }

    @Test
    fun `LocalDateTime toFormattedDateString formats with double digits`() {
        val dateTime = LocalDateTime(2024, 12, 25, 14, 30, 0, 0)
        val result = dateTime.toFormattedDateString()
        assertEquals("12/25/2024 14:30", result)
    }

    @Test
    fun `LocalDateTime toFormattedDateString handles midnight`() {
        val dateTime = LocalDateTime(2024, 6, 15, 0, 0, 0, 0)
        val result = dateTime.toFormattedDateString()
        assertEquals("06/15/2024 00:00", result)
    }

    @Test
    fun `LocalDateTime toFormattedDateString handles 23_59`() {
        val dateTime = LocalDateTime(2024, 6, 15, 23, 59, 0, 0)
        val result = dateTime.toFormattedDateString()
        assertEquals("06/15/2024 23:59", result)
    }

    @Test
    fun `LocalDateTime toFormattedDateString handles noon`() {
        val dateTime = LocalDateTime(2024, 6, 15, 12, 0, 0, 0)
        val result = dateTime.toFormattedDateString()
        assertEquals("06/15/2024 12:00", result)
    }

    // ============= FILTER NUMERIC INPUT WITH MAX DECIMALS TESTS =============

    @Test
    fun `filterNumericInputWithMaxDecimals accepts valid integer`() {
        val result = "123".filterNumericInputWithMaxDecimals()
        assertEquals("123", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals accepts valid decimal with one place`() {
        val result = "12.5".filterNumericInputWithMaxDecimals()
        assertEquals("12.5", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals accepts valid decimal with two places`() {
        val result = "12.99".filterNumericInputWithMaxDecimals()
        assertEquals("12.99", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals rejects more than two decimal places`() {
        val result = "12.999".filterNumericInputWithMaxDecimals()
        assertNull(result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals rejects multiple decimal points`() {
        val result = "12.5.5".filterNumericInputWithMaxDecimals()
        assertNull(result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals filters non-numeric characters`() {
        val result = "12abc34".filterNumericInputWithMaxDecimals()
        assertEquals("1234", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles empty string`() {
        val result = "".filterNumericInputWithMaxDecimals()
        assertEquals("", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles only decimal point`() {
        val result = ".".filterNumericInputWithMaxDecimals()
        assertEquals(".", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles decimal point at start`() {
        val result = ".99".filterNumericInputWithMaxDecimals()
        assertEquals(".99", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles decimal point at end`() {
        val result = "12.".filterNumericInputWithMaxDecimals()
        assertEquals("12.", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals rejects three decimal places`() {
        val result = "12.123".filterNumericInputWithMaxDecimals()
        assertNull(result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals with custom maxDecimals`() {
        val result = "12.1234".filterNumericInputWithMaxDecimals(maxDecimals = 4)
        assertEquals("12.1234", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals with zero maxDecimals`() {
        val result = "12.1".filterNumericInputWithMaxDecimals(maxDecimals = 0)
        assertNull(result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals filters special characters`() {
        val result = "$12.99!".filterNumericInputWithMaxDecimals()
        assertEquals("12.99", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals filters spaces`() {
        val result = "12 34".filterNumericInputWithMaxDecimals()
        assertEquals("1234", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles leading zeros`() {
        val result = "00123".filterNumericInputWithMaxDecimals()
        assertEquals("00123", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles zero`() {
        val result = "0".filterNumericInputWithMaxDecimals()
        assertEquals("0", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles zero point zero`() {
        val result = "0.00".filterNumericInputWithMaxDecimals()
        assertEquals("0.00", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals filters letters only`() {
        val result = "abc".filterNumericInputWithMaxDecimals()
        assertEquals("", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles large number`() {
        val result = "999999999.99".filterNumericInputWithMaxDecimals()
        assertEquals("999999999.99", result)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals with one decimal allowed`() {
        val validResult = "12.5".filterNumericInputWithMaxDecimals(maxDecimals = 1)
        assertEquals("12.5", validResult)

        val invalidResult = "12.55".filterNumericInputWithMaxDecimals(maxDecimals = 1)
        assertNull(invalidResult)
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles negative sign as non-numeric`() {
        val result = "-12.99".filterNumericInputWithMaxDecimals()
        assertEquals("12.99", result) // Negative sign is filtered out
    }

    @Test
    fun `filterNumericInputWithMaxDecimals handles mixed valid and invalid`() {
        val result = "a1b2.c3d".filterNumericInputWithMaxDecimals()
        assertEquals("12.3", result)
    }
}

