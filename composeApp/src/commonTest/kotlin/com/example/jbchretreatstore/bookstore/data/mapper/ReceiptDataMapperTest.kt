package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.CheckoutItemDto
import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ReceiptDataMapperTest {

    // ============= TO DOMAIN TESTS =============

    @Test
    fun `toDomain maps dto correctly`() {
        val checkoutItems = listOf(
            CheckoutItemDto(itemName = "Item 1", quantity = 2, totalPrice = 40.0)
        )
        val dto = ReceiptDataDto(
            id = Uuid.random(),
            buyerName = "John Doe",
            checkoutList = checkoutItems,
            paymentMethod = "CREDIT_CARD",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-12-25T10:30:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals(dto.id, domain.id)
        assertEquals("John Doe", domain.buyerName)
        assertEquals(1, domain.checkoutList.size)
        assertEquals("Item 1", domain.checkoutList[0].itemName)
        assertEquals(PaymentMethod.CREDIT_CARD, domain.paymentMethod)
        assertEquals(CheckoutStatus.CHECKED_OUT, domain.checkoutStatus)
        assertEquals(2024, domain.dateTime.year)
        assertEquals(12, domain.dateTime.monthNumber)
        assertEquals(25, domain.dateTime.dayOfMonth)
        assertEquals(10, domain.dateTime.hour)
        assertEquals(30, domain.dateTime.minute)
    }

    @Test
    fun `toDomain handles all payment methods`() {
        PaymentMethod.entries.forEach { method ->
            val dto = ReceiptDataDto(
                paymentMethod = method.name,
                checkoutStatus = "PENDING",
                dateTime = "2024-01-01T00:00:00"
            )

            val domain = ReceiptDataMapper.toDomain(dto)

            assertEquals(method, domain.paymentMethod)
        }
    }

    @Test
    fun `toDomain handles all checkout statuses`() {
        CheckoutStatus.entries.forEach { status ->
            val dto = ReceiptDataDto(
                paymentMethod = "CASH",
                checkoutStatus = status.name,
                dateTime = "2024-01-01T00:00:00"
            )

            val domain = ReceiptDataMapper.toDomain(dto)

            assertEquals(status, domain.checkoutStatus)
        }
    }

    @Test
    fun `toDomain handles empty checkout list`() {
        val dto = ReceiptDataDto(
            buyerName = "Test",
            checkoutList = emptyList(),
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertTrue(domain.checkoutList.isEmpty())
    }

    @Test
    fun `toDomain parses datetime correctly`() {
        val dto = ReceiptDataDto(
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-06-15T14:30:45"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals(2024, domain.dateTime.year)
        assertEquals(6, domain.dateTime.monthNumber)
        assertEquals(15, domain.dateTime.dayOfMonth)
        assertEquals(14, domain.dateTime.hour)
        assertEquals(30, domain.dateTime.minute)
        assertEquals(45, domain.dateTime.second)
    }

    // ============= TO DTO TESTS =============

    @Test
    fun `toDto maps domain correctly`() {
        val dateTime = LocalDateTime(2024, 12, 25, 10, 30, 0, 0)
        val checkoutList = listOf(
            CheckoutItem(itemName = "Item 1", quantity = 2, totalPrice = 40.0)
        )
        val domain = ReceiptData(
            id = Uuid.random(),
            buyerName = "John Doe",
            checkoutList = checkoutList,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = dateTime
        )

        val dto = ReceiptDataMapper.toDto(domain)

        assertEquals(domain.id, dto.id)
        assertEquals("John Doe", dto.buyerName)
        assertEquals(1, dto.checkoutList.size)
        assertEquals("CREDIT_CARD", dto.paymentMethod)
        assertEquals("CHECKED_OUT", dto.checkoutStatus)
        assertTrue(dto.dateTime.contains("2024-12-25"))
    }

    @Test
    fun `toDto converts all payment methods correctly`() {
        PaymentMethod.entries.forEach { method ->
            val domain = ReceiptData(
                paymentMethod = method,
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )

            val dto = ReceiptDataMapper.toDto(domain)

            assertEquals(method.name, dto.paymentMethod)
        }
    }

    @Test
    fun `toDto converts all checkout statuses correctly`() {
        CheckoutStatus.entries.forEach { status ->
            val domain = ReceiptData(
                checkoutStatus = status,
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )

            val dto = ReceiptDataMapper.toDto(domain)

            assertEquals(status.name, dto.checkoutStatus)
        }
    }

    @Test
    fun `toDto handles empty checkout list`() {
        val domain = ReceiptData(
            checkoutList = emptyList(),
            dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
        )

        val dto = ReceiptDataMapper.toDto(domain)

        assertTrue(dto.checkoutList.isEmpty())
    }

    // ============= LIST MAPPING TESTS =============

    @Test
    fun `toDomainList maps list correctly`() {
        val dtoList = listOf(
            ReceiptDataDto(
                buyerName = "Buyer 1",
                paymentMethod = "CASH",
                checkoutStatus = "CHECKED_OUT",
                dateTime = "2024-01-01T00:00:00"
            ),
            ReceiptDataDto(
                buyerName = "Buyer 2",
                paymentMethod = "CASH",
                checkoutStatus = "PENDING",
                dateTime = "2024-01-02T00:00:00"
            )
        )

        val domainList = ReceiptDataMapper.toDomainList(dtoList)

        assertEquals(2, domainList.size)
        assertEquals("Buyer 1", domainList[0].buyerName)
        assertEquals("Buyer 2", domainList[1].buyerName)
    }

    @Test
    fun `toDomainList handles empty list`() {
        val domainList = ReceiptDataMapper.toDomainList(emptyList())

        assertTrue(domainList.isEmpty())
    }

    @Test
    fun `toDtoList maps list correctly`() {
        val domainList = listOf(
            ReceiptData(buyerName = "Buyer 1", dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)),
            ReceiptData(buyerName = "Buyer 2", dateTime = LocalDateTime(2024, 1, 2, 0, 0, 0, 0))
        )

        val dtoList = ReceiptDataMapper.toDtoList(domainList)

        assertEquals(2, dtoList.size)
        assertEquals("Buyer 1", dtoList[0].buyerName)
        assertEquals("Buyer 2", dtoList[1].buyerName)
    }

    @Test
    fun `toDtoList handles empty list`() {
        val dtoList = ReceiptDataMapper.toDtoList(emptyList())

        assertTrue(dtoList.isEmpty())
    }

    // ============= ROUND TRIP TESTS =============

    @Test
    fun `round trip preserves data`() {
        val original = ReceiptData(
            buyerName = "Round Trip Test",
            checkoutList = listOf(
                CheckoutItem(itemName = "Item 1", quantity = 2, totalPrice = 40.0),
                CheckoutItem(itemName = "Item 2", quantity = 1, totalPrice = 20.0)
            ),
            paymentMethod = PaymentMethod.VENMO,
            checkoutStatus = CheckoutStatus.SAVE_FOR_LATER,
            dateTime = LocalDateTime(2024, 6, 15, 14, 30, 0, 0)
        )

        val dto = ReceiptDataMapper.toDto(original)
        val restored = ReceiptDataMapper.toDomain(dto)

        assertEquals(original.buyerName, restored.buyerName)
        assertEquals(original.checkoutList.size, restored.checkoutList.size)
        assertEquals(original.checkoutList[0].itemName, restored.checkoutList[0].itemName)
        assertEquals(original.paymentMethod, restored.paymentMethod)
        assertEquals(original.checkoutStatus, restored.checkoutStatus)
        assertEquals(original.dateTime.year, restored.dateTime.year)
        assertEquals(original.dateTime.monthNumber, restored.dateTime.monthNumber)
        assertEquals(original.dateTime.dayOfMonth, restored.dateTime.dayOfMonth)
    }

    @Test
    fun `round trip preserves id`() {
        val original = ReceiptData(
            id = Uuid.random(),
            buyerName = "Test",
            dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
        )

        val dto = ReceiptDataMapper.toDto(original)
        val restored = ReceiptDataMapper.toDomain(dto)

        assertEquals(original.id, restored.id)
    }

    // ============= EDGE CASES =============

    @Test
    fun `mapping handles special characters in buyer name`() {
        val dto = ReceiptDataDto(
            buyerName = "José García-López O'Connor",
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals("José García-López O'Connor", domain.buyerName)
    }

    @Test
    fun `mapping handles unicode characters in buyer name`() {
        val dto = ReceiptDataDto(
            buyerName = "张三 李四",
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals("张三 李四", domain.buyerName)
    }

    @Test
    fun `mapping handles many checkout items`() {
        val items = (1..100).map { i ->
            CheckoutItemDto(itemName = "Item $i", quantity = i, totalPrice = i * 10.0)
        }
        val dto = ReceiptDataDto(
            buyerName = "Bulk Buyer",
            checkoutList = items,
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals(100, domain.checkoutList.size)
    }

    @Test
    fun `mapping handles very long buyer name`() {
        val longName = "A".repeat(1000)
        val dto = ReceiptDataDto(
            buyerName = longName,
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals(1000, domain.buyerName.length)
    }

    @Test
    fun `mapping handles empty buyer name`() {
        val dto = ReceiptDataDto(
            buyerName = "",
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals("", domain.buyerName)
    }

    @Test
    fun `mapping handles datetime with nanoseconds`() {
        val dto = ReceiptDataDto(
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-06-15T14:30:45.123456789"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals(2024, domain.dateTime.year)
        assertEquals(6, domain.dateTime.monthNumber)
        assertEquals(15, domain.dateTime.dayOfMonth)
        assertEquals(14, domain.dateTime.hour)
        assertEquals(30, domain.dateTime.minute)
        assertEquals(45, domain.dateTime.second)
    }

    @Test
    fun `mapping handles checkout items with variants`() {
        val variants = listOf(
            CheckoutItemDto.VariantDto(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val items = listOf(
            CheckoutItemDto(
                itemName = "T-Shirt",
                quantity = 2,
                variants = variants,
                totalPrice = 40.0
            )
        )
        val dto = ReceiptDataDto(
            buyerName = "Test",
            checkoutList = items,
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals(1, domain.checkoutList[0].variants.size)
        assertEquals("Size", domain.checkoutList[0].variants[0].key)
    }

    @Test
    fun `mapping preserves checkout list order`() {
        val items = listOf(
            CheckoutItemDto(itemName = "First", quantity = 1, totalPrice = 10.0),
            CheckoutItemDto(itemName = "Second", quantity = 2, totalPrice = 20.0),
            CheckoutItemDto(itemName = "Third", quantity = 3, totalPrice = 30.0)
        )
        val dto = ReceiptDataDto(
            buyerName = "Test",
            checkoutList = items,
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-01-01T00:00:00"
        )

        val domain = ReceiptDataMapper.toDomain(dto)

        assertEquals("First", domain.checkoutList[0].itemName)
        assertEquals("Second", domain.checkoutList[1].itemName)
        assertEquals("Third", domain.checkoutList[2].itemName)
    }
}

