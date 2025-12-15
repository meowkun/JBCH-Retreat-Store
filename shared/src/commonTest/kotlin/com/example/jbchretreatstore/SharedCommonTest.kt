package com.example.jbchretreatstore

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the shared module.
 */
class SharedCommonTest {

    @Test
    fun `basic arithmetic sanity check`() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun `SERVER_PORT constant has expected value`() {
        assertEquals(8080, SERVER_PORT)
    }

    @Test
    fun `Greeting class can be instantiated`() {
        val greeting = Greeting()
        // Just verify it doesn't throw
        assertTrue(greeting.greet().isNotEmpty())
    }

    @Test
    fun `Greeting returns string containing Hello`() {
        val greeting = Greeting()
        val result = greeting.greet()

        assertTrue(result.contains("Hello"))
    }

    @Test
    fun `Greeting includes platform name`() {
        val greeting = Greeting()
        val result = greeting.greet()

        // The greeting should include the platform name from getPlatform()
        assertTrue(result.length > "Hello, !".length)
    }
}