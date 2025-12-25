package com.example.jbchretreatstore.bookstore.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ReceiptDataDtoTest {

    // ============= DEFAULT CONSTRUCTOR TESTS =============

    @Test
    fun `default constructor creates dto with empty values`() {
        val dto = ReceiptDataDto()

        assertEquals("", dto.buyerName)
        assertTrue(dto.checkoutList.isEmpty())
        assertEquals("CASH", dto.paymentMethod)
        assertEquals("PENDING", dto.checkoutStatus)
    }

    @Test
    fun `each dto has unique id by default`() {
        val dto1 = ReceiptDataDto()
        val dto2 = ReceiptDataDto()

        assertNotEquals(dto1.id, dto2.id)
    }

    @Test
    fun `dto can have custom id`() {
        val customId = Uuid.random()
        val dto = ReceiptDataDto(id = customId)

        assertEquals(customId, dto.id)
    }

    // ============= PARAMETER CONSTRUCTOR TESTS =============

    @Test
    fun `constructor with parameters creates dto correctly`() {
        val checkoutList = listOf(
            CheckoutItemDto(itemName = "Book", quantity = 2, totalPrice = 40.0)
        )
        val dto = ReceiptDataDto(
            buyerName = "John Doe",
            checkoutList = checkoutList,
            paymentMethod = "CREDIT_CARD",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-12-25T10:30:00"
        )

        assertEquals("John Doe", dto.buyerName)
        assertEquals(1, dto.checkoutList.size)
        assertEquals("CREDIT_CARD", dto.paymentMethod)
        assertEquals("CHECKED_OUT", dto.checkoutStatus)
        assertEquals("2024-12-25T10:30:00", dto.dateTime)
    }

    // ============= PAYMENT METHOD STRING TESTS =============

    @Test
    fun `paymentMethod accepts CASH`() {
        val dto = ReceiptDataDto(paymentMethod = "CASH")
        assertEquals("CASH", dto.paymentMethod)
    }

    @Test
    fun `paymentMethod accepts CREDIT_CARD`() {
        val dto = ReceiptDataDto(paymentMethod = "CREDIT_CARD")
        assertEquals("CREDIT_CARD", dto.paymentMethod)
    }

    @Test
    fun `paymentMethod accepts ZELLE`() {
        val dto = ReceiptDataDto(paymentMethod = "ZELLE")
        assertEquals("ZELLE", dto.paymentMethod)
    }

    @Test
    fun `paymentMethod accepts VENMO`() {
        val dto = ReceiptDataDto(paymentMethod = "VENMO")
        assertEquals("VENMO", dto.paymentMethod)
    }

    @Test
    fun `paymentMethod accepts CHECK`() {
        val dto = ReceiptDataDto(paymentMethod = "CHECK")
        assertEquals("CHECK", dto.paymentMethod)
    }

    // ============= CHECKOUT STATUS STRING TESTS =============

    @Test
    fun `checkoutStatus accepts CHECKED_OUT`() {
        val dto = ReceiptDataDto(checkoutStatus = "CHECKED_OUT")
        assertEquals("CHECKED_OUT", dto.checkoutStatus)
    }

    @Test
    fun `checkoutStatus accepts SAVE_FOR_LATER`() {
        val dto = ReceiptDataDto(checkoutStatus = "SAVE_FOR_LATER")
        assertEquals("SAVE_FOR_LATER", dto.checkoutStatus)
    }

    @Test
    fun `checkoutStatus accepts PENDING`() {
        val dto = ReceiptDataDto(checkoutStatus = "PENDING")
        assertEquals("PENDING", dto.checkoutStatus)
    }

    // ============= DATETIME FORMAT TESTS =============

    @Test
    fun `dateTime accepts ISO format`() {
        val dto = ReceiptDataDto(dateTime = "2024-12-25T10:30:00")
        assertEquals("2024-12-25T10:30:00", dto.dateTime)
    }

    @Test
    fun `dateTime accepts format with nanoseconds`() {
        val dto = ReceiptDataDto(dateTime = "2024-12-25T10:30:00.123456789")
        assertEquals("2024-12-25T10:30:00.123456789", dto.dateTime)
    }

    @Test
    fun `dateTime accepts midnight`() {
        val dto = ReceiptDataDto(dateTime = "2024-01-01T00:00:00")
        assertEquals("2024-01-01T00:00:00", dto.dateTime)
    }

    @Test
    fun `dateTime accepts end of day`() {
        val dto = ReceiptDataDto(dateTime = "2024-12-31T23:59:59")
        assertEquals("2024-12-31T23:59:59", dto.dateTime)
    }

    // ============= COPY TESTS =============

    @Test
    fun `copy preserves id when not specified`() {
        val original = ReceiptDataDto(buyerName = "Original")
        val copied = original.copy(buyerName = "Copied")

        assertEquals(original.id, copied.id)
        assertEquals("Copied", copied.buyerName)
    }

    @Test
    fun `copy can change all fields`() {
        val original = ReceiptDataDto(
            buyerName = "Original",
            paymentMethod = "CASH",
            checkoutStatus = "PENDING"
        )
        val copied = original.copy(
            buyerName = "Copied",
            paymentMethod = "VENMO",
            checkoutStatus = "CHECKED_OUT"
        )

        assertEquals("Copied", copied.buyerName)
        assertEquals("VENMO", copied.paymentMethod)
        assertEquals("CHECKED_OUT", copied.checkoutStatus)
    }

    // ============= EDGE CASES =============

    @Test
    fun `dto with empty buyer name`() {
        val dto = ReceiptDataDto(buyerName = "")
        assertEquals("", dto.buyerName)
    }

    @Test
    fun `dto with whitespace buyer name`() {
        val dto = ReceiptDataDto(buyerName = "   ")
        assertEquals("   ", dto.buyerName)
    }

    @Test
    fun `dto with special characters in buyer name`() {
        val dto = ReceiptDataDto(buyerName = "José García-López O'Connor")
        assertEquals("José García-López O'Connor", dto.buyerName)
    }

    @Test
    fun `dto with unicode characters in buyer name`() {
        val dto = ReceiptDataDto(buyerName = "张三 李四")
        assertEquals("张三 李四", dto.buyerName)
    }

    @Test
    fun `dto with very long buyer name`() {
        val longName = "A".repeat(1000)
        val dto = ReceiptDataDto(buyerName = longName)
        assertEquals(1000, dto.buyerName.length)
    }

    @Test
    fun `dto with empty checkout list`() {
        val dto = ReceiptDataDto(checkoutList = emptyList())
        assertTrue(dto.checkoutList.isEmpty())
    }

    @Test
    fun `dto with many checkout items`() {
        val checkoutList = (1..100).map { i ->
            CheckoutItemDto(itemName = "Item $i", quantity = i, totalPrice = i * 10.0)
        }
        val dto = ReceiptDataDto(checkoutList = checkoutList)
        assertEquals(100, dto.checkoutList.size)
    }

    @Test
    fun `dto checkout list preserves order`() {
        val checkoutList = listOf(
            CheckoutItemDto(itemName = "First", quantity = 1, totalPrice = 10.0),
            CheckoutItemDto(itemName = "Second", quantity = 2, totalPrice = 20.0),
            CheckoutItemDto(itemName = "Third", quantity = 3, totalPrice = 30.0)
        )
        val dto = ReceiptDataDto(checkoutList = checkoutList)

        assertEquals("First", dto.checkoutList[0].itemName)
        assertEquals("Second", dto.checkoutList[1].itemName)
        assertEquals("Third", dto.checkoutList[2].itemName)
    }

    // ============= INVALID STRING VALUES (EDGE CASES) =============

    @Test
    fun `paymentMethod can be arbitrary string at DTO level`() {
        // At DTO level, we accept any string - validation happens at domain level
        val dto = ReceiptDataDto(paymentMethod = "INVALID_METHOD")
        assertEquals("INVALID_METHOD", dto.paymentMethod)
    }

    @Test
    fun `checkoutStatus can be arbitrary string at DTO level`() {
        // At DTO level, we accept any string - validation happens at domain level
        val dto = ReceiptDataDto(checkoutStatus = "INVALID_STATUS")
        assertEquals("INVALID_STATUS", dto.checkoutStatus)
    }

    @Test
    fun `dateTime can be arbitrary string at DTO level`() {
        // At DTO level, we accept any string - parsing happens at mapper level
        val dto = ReceiptDataDto(dateTime = "not-a-date")
        assertEquals("not-a-date", dto.dateTime)
    }

    // ============= EQUALITY TESTS =============

    @Test
    fun `dtos with same data are equal`() {
        val id = Uuid.random()
        val checkoutList = listOf(
            CheckoutItemDto(itemName = "Item", quantity = 1, totalPrice = 10.0)
        )

        val dto1 = ReceiptDataDto(
            id = id,
            buyerName = "Test",
            checkoutList = checkoutList,
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-01-01T00:00:00"
        )
        val dto2 = ReceiptDataDto(
            id = id,
            buyerName = "Test",
            checkoutList = checkoutList,
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-01-01T00:00:00"
        )

        assertEquals(dto1, dto2)
    }

    @Test
    fun `dtos with different ids are not equal`() {
        val dto1 = ReceiptDataDto(buyerName = "Test")
        val dto2 = ReceiptDataDto(buyerName = "Test")

        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `dtos with different buyer names are not equal`() {
        val id = Uuid.random()
        val dto1 = ReceiptDataDto(id = id, buyerName = "Name1")
        val dto2 = ReceiptDataDto(id = id, buyerName = "Name2")

        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `dtos with different payment methods are not equal`() {
        val id = Uuid.random()
        val dto1 = ReceiptDataDto(id = id, paymentMethod = "CASH")
        val dto2 = ReceiptDataDto(id = id, paymentMethod = "VENMO")

        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `dtos with different checkout statuses are not equal`() {
        val id = Uuid.random()
        val dto1 = ReceiptDataDto(id = id, checkoutStatus = "PENDING")
        val dto2 = ReceiptDataDto(id = id, checkoutStatus = "CHECKED_OUT")

        assertNotEquals(dto1, dto2)
    }

    // ============= HASH CODE TESTS =============

    @Test
    fun `equal dtos have same hash code`() {
        val id = Uuid.random()
        val dto1 = ReceiptDataDto(
            id = id,
            buyerName = "Test",
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-01-01T00:00:00"
        )
        val dto2 = ReceiptDataDto(
            id = id,
            buyerName = "Test",
            paymentMethod = "CASH",
            checkoutStatus = "PENDING",
            dateTime = "2024-01-01T00:00:00"
        )

        assertEquals(dto1.hashCode(), dto2.hashCode())
    }

    // ============= CHECKOUT ITEMS WITH VARIANTS =============

    @Test
    fun `dto with checkout items containing variants`() {
        val variants = listOf(
            CheckoutItemDto.VariantDto(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val checkoutList = listOf(
            CheckoutItemDto(
                itemName = "T-Shirt",
                quantity = 2,
                variants = variants,
                totalPrice = 50.0
            )
        )
        val dto = ReceiptDataDto(
            buyerName = "Test",
            checkoutList = checkoutList,
            paymentMethod = "CASH",
            checkoutStatus = "CHECKED_OUT",
            dateTime = "2024-01-01T00:00:00"
        )

        assertEquals(1, dto.checkoutList[0].variants.size)
        assertEquals("M", dto.checkoutList[0].variants[0].selectedValue)
    }
}

