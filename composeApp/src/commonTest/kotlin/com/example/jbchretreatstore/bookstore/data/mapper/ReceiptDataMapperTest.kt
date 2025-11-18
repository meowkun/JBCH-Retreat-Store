package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.CheckoutItemDto
import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ReceiptDataMapperTest {

    // ========== toDomain Tests ==========

    @Test
    fun `toDomain should map all fields correctly`() {
        // Given
        val id = Uuid.random()
        val itemId = Uuid.random()
        val dto = ReceiptDataDto(
            id = id,
            buyerName = "John Doe",
            checkoutList = listOf(
                CheckoutItemDto(itemId, "Coffee", 2, mapOf("size" to "Large"), 10.0)
            ),
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2025-11-17T10:30:00"
        )

        // When
        val domain = ReceiptDataMapper.toDomain(dto)

        // Then
        assertEquals(id, domain.id)
        assertEquals("John Doe", domain.buyerName)
        assertEquals(1, domain.checkoutList.size)
        assertEquals("Coffee", domain.checkoutList[0].itemName)
        assertEquals(PaymentMethod.CASH, domain.paymentMethod)
        assertEquals(CheckoutStatus.CHECKED_OUT, domain.checkoutStatus)
        assertEquals(LocalDateTime(2025, 11, 17, 10, 30, 0), domain.dateTime)
    }

    @Test
    fun `toDomain with empty checkout list should work`() {
        // Given
        val dto = ReceiptDataDto(
            id = Uuid.random(),
            buyerName = "Jane",
            checkoutList = emptyList(),
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2025-11-17T10:30:00"
        )

        // When
        val domain = ReceiptDataMapper.toDomain(dto)

        // Then
        assertEquals(0, domain.checkoutList.size)
        assertEquals(PaymentMethod.CASH, domain.paymentMethod)
        assertEquals(CheckoutStatus.PENDING, domain.checkoutStatus)
    }

    @Test
    fun `toDomain with SAVE_FOR_LATER status should work`() {
        // Given
        val dto = ReceiptDataDto(
            id = Uuid.random(),
            buyerName = "Bob",
            checkoutList = emptyList(),
            paymentMethod = "CASH",
            checkoutStatus = "SAVE_FOR_LATER",
            dateTime = "2025-11-17T10:30:00"
        )

        // When
        val domain = ReceiptDataMapper.toDomain(dto)

        // Then
        assertEquals(CheckoutStatus.SAVE_FOR_LATER, domain.checkoutStatus)
    }

    @Test
    fun `toDomain with multiple checkout items should map all`() {
        // Given
        val dto = ReceiptDataDto(
            id = Uuid.random(),
            buyerName = "Dave",
            checkoutList = listOf(
                CheckoutItemDto(Uuid.random(), "Coffee", 2, emptyMap(), 10.0),
                CheckoutItemDto(Uuid.random(), "Tea", 1, emptyMap(), 5.0),
                CheckoutItemDto(Uuid.random(), "Cake", 3, emptyMap(), 15.0)
            ),
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2025-11-17T10:30:00"
        )

        // When
        val domain = ReceiptDataMapper.toDomain(dto)

        // Then
        assertEquals(3, domain.checkoutList.size)
        assertEquals("Coffee", domain.checkoutList[0].itemName)
        assertEquals("Tea", domain.checkoutList[1].itemName)
        assertEquals("Cake", domain.checkoutList[2].itemName)
    }

    // ========== toDto Tests ==========

    @Test
    fun `toDto should map all fields correctly`() {
        // Given
        val id = Uuid.random()
        val itemId = Uuid.random()
        val domain = ReceiptData(
            id = id,
            buyerName = "John Doe",
            checkoutList = listOf(
                CheckoutItem(itemId, "Coffee", 2, mapOf("size" to "Large"), 10.0)
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2025, 11, 17, 10, 30, 0)
        )

        // When
        val dto = ReceiptDataMapper.toDto(domain)

        // Then
        assertEquals(id, dto.id)
        assertEquals("John Doe", dto.buyerName)
        assertEquals(1, dto.checkoutList.size)
        assertEquals("Coffee", dto.checkoutList[0].itemName)
        assertEquals("CASH", dto.paymentMethod)
        assertEquals("CHECKED_OUT", dto.checkoutStatus)
        assertEquals("2025-11-17T10:30", dto.dateTime)
    }

    @Test
    fun `toDto with empty checkout list should work`() {
        // Given
        val domain = ReceiptData(
            id = Uuid.random(),
            buyerName = "Jane",
            checkoutList = emptyList(),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.PENDING,
            dateTime = LocalDateTime(2025, 11, 17, 10, 30, 0)
        )

        // When
        val dto = ReceiptDataMapper.toDto(domain)

        // Then
        assertEquals(0, dto.checkoutList.size)
        assertEquals("CASH", dto.paymentMethod)
        assertEquals("PENDING", dto.checkoutStatus)
    }

    @Test
    fun `toDto should serialize all payment methods correctly`() {
        val testCases: List<Pair<PaymentMethod, String>> = listOf(
            PaymentMethod.CASH to "CASH",
            PaymentMethod.CREDIT_CARD to "CREDIT_CARD",
            PaymentMethod.DEBIT_CARD to "DEBIT_CARD",
            PaymentMethod.E_WALLET to "E_WALLET"
        )

        testCases.forEach { (paymentMethod, expectedString) ->
            // Given
            val domain = ReceiptData(
                id = Uuid.random(),
                buyerName = "Test",
                checkoutList = emptyList(),
                paymentMethod = paymentMethod,
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                dateTime = LocalDateTime(2025, 11, 17, 10, 30, 0)
            )

            // When
            val dto = ReceiptDataMapper.toDto(domain)

            // Then
            assertEquals(expectedString, dto.paymentMethod)
        }
    }

    @Test
    fun `toDto should serialize all checkout statuses correctly`() {
        val testCases: List<Pair<CheckoutStatus, String>> = listOf(
            CheckoutStatus.PENDING to "PENDING",
            CheckoutStatus.CHECKED_OUT to "CHECKED_OUT",
            CheckoutStatus.SAVE_FOR_LATER to "SAVE_FOR_LATER"
        )

        testCases.forEach { (status, expectedString) ->
            // Given
            val domain = ReceiptData(
                id = Uuid.random(),
                buyerName = "Test",
                checkoutList = emptyList(),
                paymentMethod = PaymentMethod.CASH,
                checkoutStatus = status,
                dateTime = LocalDateTime(2025, 11, 17, 10, 30, 0)
            )

            // When
            val dto = ReceiptDataMapper.toDto(domain)

            // Then
            assertEquals(expectedString, dto.checkoutStatus)
        }
    }

    // ========== Round-trip Tests ==========

    @Test
    fun `round trip toDomain then toDto should preserve data`() {
        // Given
        val originalDto = ReceiptDataDto(
            id = Uuid.random(),
            buyerName = "Test User",
            checkoutList = listOf(
                CheckoutItemDto(Uuid.random(), "Item", 1, mapOf("key" to "value"), 10.0)
            ),
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2025-11-17T10:30:00"
        )

        // When
        val domain = ReceiptDataMapper.toDomain(originalDto)
        val resultDto = ReceiptDataMapper.toDto(domain)

        // Then
        assertEquals(originalDto.id, resultDto.id)
        assertEquals(originalDto.buyerName, resultDto.buyerName)
        assertEquals(originalDto.checkoutList.size, resultDto.checkoutList.size)
        assertEquals(originalDto.paymentMethod, resultDto.paymentMethod)
        assertEquals(originalDto.checkoutStatus, resultDto.checkoutStatus)
    }

    @Test
    fun `round trip toDto then toDomain should preserve data`() {
        // Given
        val originalDomain = ReceiptData(
            id = Uuid.random(),
            buyerName = "Test User",
            checkoutList = listOf(
                CheckoutItem(Uuid.random(), "Item", 1, mapOf("key" to "value"), 10.0)
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2025, 11, 17, 10, 30, 0)
        )

        // When
        val dto = ReceiptDataMapper.toDto(originalDomain)
        val resultDomain = ReceiptDataMapper.toDomain(dto)

        // Then
        assertEquals(originalDomain.id, resultDomain.id)
        assertEquals(originalDomain.buyerName, resultDomain.buyerName)
        assertEquals(originalDomain.checkoutList.size, resultDomain.checkoutList.size)
        assertEquals(originalDomain.paymentMethod, resultDomain.paymentMethod)
        assertEquals(originalDomain.checkoutStatus, resultDomain.checkoutStatus)
    }

    // ========== toDomainList Tests ==========

    @Test
    fun `toDomainList should map all items`() {
        // Given
        val dtoList = listOf(
            ReceiptDataDto(Uuid.random(), "Buyer 1", emptyList(), "CASH", "CHECKED_OUT", "2025-11-17T10:30:00"),
            ReceiptDataDto(Uuid.random(), "Buyer 2", emptyList(), "CASH", "PENDING", "2025-11-17T11:30:00"),
            ReceiptDataDto(Uuid.random(), "Buyer 3", emptyList(), "CASH", "SAVE_FOR_LATER", "2025-11-17T12:30:00")
        )

        // When
        val domainList = ReceiptDataMapper.toDomainList(dtoList)

        // Then
        assertEquals(3, domainList.size)
        assertEquals("Buyer 1", domainList[0].buyerName)
        assertEquals("Buyer 2", domainList[1].buyerName)
        assertEquals("Buyer 3", domainList[2].buyerName)
    }

    @Test
    fun `toDomainList with empty list should return empty list`() {
        // When
        val domainList = ReceiptDataMapper.toDomainList(emptyList())

        // Then
        assertEquals(0, domainList.size)
    }

    // ========== toDtoList Tests ==========

    @Test
    fun `toDtoList should map all items`() {
        // Given
        val domainList = listOf(
            ReceiptData(Uuid.random(), "Buyer 1", emptyList(), PaymentMethod.CASH, CheckoutStatus.CHECKED_OUT, LocalDateTime(2025, 11, 17, 10, 30, 0)),
            ReceiptData(Uuid.random(), "Buyer 2", emptyList(), PaymentMethod.CASH, CheckoutStatus.PENDING, LocalDateTime(2025, 11, 17, 11, 30, 0))
        )

        // When
        val dtoList = ReceiptDataMapper.toDtoList(domainList)

        // Then
        assertEquals(2, dtoList.size)
        assertEquals("Buyer 1", dtoList[0].buyerName)
        assertEquals("Buyer 2", dtoList[1].buyerName)
    }

    @Test
    fun `toDtoList with empty list should return empty list`() {
        // When
        val dtoList = ReceiptDataMapper.toDtoList(emptyList())

        // Then
        assertEquals(0, dtoList.size)
    }

    // ========== Edge Cases ==========

    @Test
    fun `toDomain with very long buyer name should work`() {
        // Given
        val longName = "a".repeat(1000)
        val dto = ReceiptDataDto(Uuid.random(), longName, emptyList(), "CASH", "CHECKED_OUT", "2025-11-17T10:30:00")

        // When
        val domain = ReceiptDataMapper.toDomain(dto)

        // Then
        assertEquals(longName, domain.buyerName)
    }

    @Test
    fun `toDomain with datetime with seconds and milliseconds should parse correctly`() {
        // Given
        val dto = ReceiptDataDto(Uuid.random(), "Test", emptyList(), "CASH", "CHECKED_OUT", "2025-11-17T10:30:45.123")

        // When
        val domain = ReceiptDataMapper.toDomain(dto)

        // Then
        assertNotNull(domain.dateTime)
        assertEquals(2025, domain.dateTime.year)
        assertEquals(11, domain.dateTime.month.number)
        assertEquals(17, domain.dateTime.day)
        assertEquals(10, domain.dateTime.hour)
        assertEquals(30, domain.dateTime.minute)
    }
}
