package com.example.jbchretreatstore.bookstore.data.testdata

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime

/**
 * Sample test data for development and testing purposes
 */
object SampleTestData {

    val sampleDisplayItems: List<DisplayItem> = listOf(
        DisplayItem(
            name = "Holy Bible - NIV",
            price = 45.99,
            variants = listOf(
                DisplayItem.Variant(
                    key = "Language",
                    valueList = listOf("English", "Spanish", "French", "Chinese")
                ),
                DisplayItem.Variant(
                    key = "Cover",
                    valueList = listOf("Hardcover", "Leather", "Paperback")
                )
            )
        ),
        DisplayItem(
            name = "Study Bible - ESV",
            price = 59.99,
            variants = listOf(
                DisplayItem.Variant(key = "Language", valueList = listOf("English", "Spanish")),
                DisplayItem.Variant(key = "Size", valueList = listOf("Regular", "Large Print"))
            )
        ),
        DisplayItem(
            name = "Devotional Journal",
            price = 18.50,
            variants = listOf(
                DisplayItem.Variant(
                    key = "Color",
                    valueList = listOf("Blue", "Brown", "Pink", "Green")
                ),
                DisplayItem.Variant(key = "Pages", valueList = listOf("200", "365"))
            )
        ),
        DisplayItem(
            name = "Retreat T-Shirt",
            price = 25.00,
            variants = listOf(
                DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L", "XL", "2XL")),
                DisplayItem.Variant(
                    key = "Color",
                    valueList = listOf("White", "Navy", "Gray", "Black")
                )
            )
        ),
        DisplayItem(
            name = "Prayer Bracelet",
            price = 12.00,
            variants = listOf(
                DisplayItem.Variant(key = "Material", valueList = listOf("Wood", "Stone", "Metal")),
                DisplayItem.Variant(key = "Size", valueList = listOf("Small", "Medium", "Large"))
            )
        ),
        DisplayItem(
            name = "Worship CD Album",
            price = 15.00,
            variants = listOf(
                DisplayItem.Variant(
                    key = "Artist",
                    valueList = listOf("Hillsong", "Bethel", "Elevation", "Maverick City")
                )
            )
        ),
        DisplayItem(
            name = "Cross Necklace",
            price = 35.00,
            variants = listOf(
                DisplayItem.Variant(
                    key = "Material",
                    valueList = listOf("Silver", "Gold", "Rose Gold")
                ),
                DisplayItem.Variant(
                    key = "Chain Length",
                    valueList = listOf("16 inch", "18 inch", "20 inch")
                )
            )
        ),
        DisplayItem(
            name = "Christian Book - Purpose Driven Life",
            price = 22.00,
            variants = listOf(
                DisplayItem.Variant(
                    key = "Format",
                    valueList = listOf("Hardcover", "Paperback", "Large Print")
                )
            )
        ),
        DisplayItem(
            name = "Retreat Hoodie",
            price = 45.00,
            variants = listOf(
                DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L", "XL", "2XL")),
                DisplayItem.Variant(
                    key = "Color",
                    valueList = listOf("Black", "Heather Gray", "Navy")
                )
            )
        ),
        DisplayItem(
            name = "Scripture Art Print",
            price = 28.00,
            variants = listOf(
                DisplayItem.Variant(
                    key = "Verse",
                    valueList = listOf(
                        "Jeremiah 29:11",
                        "Psalm 23",
                        "Philippians 4:13",
                        "Proverbs 3:5-6"
                    )
                ),
                DisplayItem.Variant(key = "Size", valueList = listOf("8x10", "11x14", "16x20"))
            )
        )
    )

    val samplePurchaseHistory: List<ReceiptData> = listOf(
        ReceiptData(
            buyerName = "John Smith",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Holy Bible - NIV",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Language",
                            valueList = listOf("English"),
                            selectedValue = "English"
                        ),
                        CheckoutItem.Variant(
                            key = "Cover",
                            valueList = listOf("Hardcover"),
                            selectedValue = "Hardcover"
                        )
                    ),
                    totalPrice = 91.98
                )
            ),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 10, 14, 30, 0)
        ),
        ReceiptData(
            buyerName = "Mary Johnson",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Retreat T-Shirt",
                    quantity = 3,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("M"),
                            selectedValue = "M"
                        ),
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Navy"),
                            selectedValue = "Navy"
                        )
                    ),
                    totalPrice = 75.00
                ),
                CheckoutItem(
                    itemName = "Prayer Bracelet",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Material",
                            valueList = listOf("Wood"),
                            selectedValue = "Wood"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("Medium"),
                            selectedValue = "Medium"
                        )
                    ),
                    totalPrice = 12.00
                )
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 10, 15, 45, 0)
        ),
        ReceiptData(
            buyerName = "Robert Williams",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Study Bible - ESV",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Language",
                            valueList = listOf("English"),
                            selectedValue = "English"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("Large Print"),
                            selectedValue = "Large Print"
                        )
                    ),
                    totalPrice = 59.99
                )
            ),
            paymentMethod = PaymentMethod.ZELLE,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 9, 10, 0, 0)
        ),
        ReceiptData(
            buyerName = "Sarah Davis",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Devotional Journal",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Pink"),
                            selectedValue = "Pink"
                        ),
                        CheckoutItem.Variant(
                            key = "Pages",
                            valueList = listOf("365"),
                            selectedValue = "365"
                        )
                    ),
                    totalPrice = 37.00
                ),
                CheckoutItem(
                    itemName = "Cross Necklace",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Material",
                            valueList = listOf("Silver"),
                            selectedValue = "Silver"
                        ),
                        CheckoutItem.Variant(
                            key = "Chain Length",
                            valueList = listOf("18 inch"),
                            selectedValue = "18 inch"
                        )
                    ),
                    totalPrice = 35.00
                )
            ),
            paymentMethod = PaymentMethod.DEBIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 9, 11, 30, 0)
        ),
        ReceiptData(
            buyerName = "Michael Brown",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Worship CD Album",
                    quantity = 4,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Artist",
                            valueList = listOf("Hillsong"),
                            selectedValue = "Hillsong"
                        )
                    ),
                    totalPrice = 60.00
                )
            ),
            paymentMethod = PaymentMethod.E_WALLET,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 8, 16, 20, 0)
        ),
        ReceiptData(
            buyerName = "Emily Chen",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Retreat Hoodie",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("L"),
                            selectedValue = "L"
                        ),
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Heather Gray"),
                            selectedValue = "Heather Gray"
                        )
                    ),
                    totalPrice = 90.00
                )
            ),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 8, 9, 15, 0)
        ),
        ReceiptData(
            buyerName = "David Lee",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Scripture Art Print",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Verse",
                            valueList = listOf("Jeremiah 29:11"),
                            selectedValue = "Jeremiah 29:11"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("11x14"),
                            selectedValue = "11x14"
                        )
                    ),
                    totalPrice = 28.00
                ),
                CheckoutItem(
                    itemName = "Christian Book - Purpose Driven Life",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Format",
                            valueList = listOf("Paperback"),
                            selectedValue = "Paperback"
                        )
                    ),
                    totalPrice = 22.00
                )
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 7, 14, 0, 0)
        ),
        ReceiptData(
            buyerName = "Jennifer Martinez",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Holy Bible - NIV",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Language",
                            valueList = listOf("Spanish"),
                            selectedValue = "Spanish"
                        ),
                        CheckoutItem.Variant(
                            key = "Cover",
                            valueList = listOf("Leather"),
                            selectedValue = "Leather"
                        )
                    ),
                    totalPrice = 45.99
                )
            ),
            paymentMethod = PaymentMethod.ZELLE,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 7, 12, 45, 0)
        ),
        ReceiptData(
            buyerName = "Chris Wilson",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Retreat T-Shirt",
                    quantity = 5,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("XL"),
                            selectedValue = "XL"
                        ),
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("White"),
                            selectedValue = "White"
                        )
                    ),
                    totalPrice = 125.00
                )
            ),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 6, 17, 30, 0)
        ),
        ReceiptData(
            buyerName = "Amanda Taylor",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Prayer Bracelet",
                    quantity = 3,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Material",
                            valueList = listOf("Stone"),
                            selectedValue = "Stone"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("Small"),
                            selectedValue = "Small"
                        )
                    ),
                    totalPrice = 36.00
                )
            ),
            paymentMethod = PaymentMethod.DEBIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 6, 11, 0, 0)
        ),
        ReceiptData(
            buyerName = "Kevin Anderson",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Study Bible - ESV",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Language",
                            valueList = listOf("English"),
                            selectedValue = "English"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("Regular"),
                            selectedValue = "Regular"
                        )
                    ),
                    totalPrice = 119.98
                ),
                CheckoutItem(
                    itemName = "Devotional Journal",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Brown"),
                            selectedValue = "Brown"
                        ),
                        CheckoutItem.Variant(
                            key = "Pages",
                            valueList = listOf("200"),
                            selectedValue = "200"
                        )
                    ),
                    totalPrice = 18.50
                )
            ),
            paymentMethod = PaymentMethod.E_WALLET,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 5, 15, 15, 0)
        ),
        ReceiptData(
            buyerName = "Lisa Garcia",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Cross Necklace",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Material",
                            valueList = listOf("Gold"),
                            selectedValue = "Gold"
                        ),
                        CheckoutItem.Variant(
                            key = "Chain Length",
                            valueList = listOf("16 inch"),
                            selectedValue = "16 inch"
                        )
                    ),
                    totalPrice = 70.00
                )
            ),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 5, 10, 30, 0)
        ),
        ReceiptData(
            buyerName = "Jason Thompson",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Worship CD Album",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Artist",
                            valueList = listOf("Maverick City"),
                            selectedValue = "Maverick City"
                        )
                    ),
                    totalPrice = 30.00
                ),
                CheckoutItem(
                    itemName = "Retreat Hoodie",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("M"),
                            selectedValue = "M"
                        ),
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Black"),
                            selectedValue = "Black"
                        )
                    ),
                    totalPrice = 45.00
                )
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 4, 16, 45, 0)
        ),
        ReceiptData(
            buyerName = "Michelle White",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Scripture Art Print",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Verse",
                            valueList = listOf("Psalm 23"),
                            selectedValue = "Psalm 23"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("16x20"),
                            selectedValue = "16x20"
                        )
                    ),
                    totalPrice = 56.00
                )
            ),
            paymentMethod = PaymentMethod.ZELLE,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 4, 13, 0, 0)
        ),
        ReceiptData(
            buyerName = "Daniel Harris",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Christian Book - Purpose Driven Life",
                    quantity = 3,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Format",
                            valueList = listOf("Hardcover"),
                            selectedValue = "Hardcover"
                        )
                    ),
                    totalPrice = 66.00
                )
            ),
            paymentMethod = PaymentMethod.DEBIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 3, 9, 30, 0)
        ),
        ReceiptData(
            buyerName = "Rachel Kim",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Holy Bible - NIV",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Language",
                            valueList = listOf("Chinese"),
                            selectedValue = "Chinese"
                        ),
                        CheckoutItem.Variant(
                            key = "Cover",
                            valueList = listOf("Paperback"),
                            selectedValue = "Paperback"
                        )
                    ),
                    totalPrice = 45.99
                ),
                CheckoutItem(
                    itemName = "Prayer Bracelet",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Material",
                            valueList = listOf("Metal"),
                            selectedValue = "Metal"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("Large"),
                            selectedValue = "Large"
                        )
                    ),
                    totalPrice = 24.00
                )
            ),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 3, 14, 20, 0)
        ),
        ReceiptData(
            buyerName = "Steven Moore",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Retreat T-Shirt",
                    quantity = 2,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("2XL"),
                            selectedValue = "2XL"
                        ),
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Gray"),
                            selectedValue = "Gray"
                        )
                    ),
                    totalPrice = 50.00
                ),
                CheckoutItem(
                    itemName = "Retreat Hoodie",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("2XL"),
                            selectedValue = "2XL"
                        ),
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Navy"),
                            selectedValue = "Navy"
                        )
                    ),
                    totalPrice = 45.00
                )
            ),
            paymentMethod = PaymentMethod.E_WALLET,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 2, 11, 45, 0)
        ),
        ReceiptData(
            buyerName = "Angela Clark",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Devotional Journal",
                    quantity = 4,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Color",
                            valueList = listOf("Green"),
                            selectedValue = "Green"
                        ),
                        CheckoutItem.Variant(
                            key = "Pages",
                            valueList = listOf("365"),
                            selectedValue = "365"
                        )
                    ),
                    totalPrice = 74.00
                )
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.SAVE_FOR_LATER,
            dateTime = LocalDateTime(2024, 12, 2, 16, 0, 0)
        ),
        ReceiptData(
            buyerName = "Brian Rodriguez",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Cross Necklace",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Material",
                            valueList = listOf("Rose Gold"),
                            selectedValue = "Rose Gold"
                        ),
                        CheckoutItem.Variant(
                            key = "Chain Length",
                            valueList = listOf("20 inch"),
                            selectedValue = "20 inch"
                        )
                    ),
                    totalPrice = 35.00
                ),
                CheckoutItem(
                    itemName = "Worship CD Album",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Artist",
                            valueList = listOf("Bethel"),
                            selectedValue = "Bethel"
                        )
                    ),
                    totalPrice = 15.00
                )
            ),
            paymentMethod = PaymentMethod.ZELLE,
            checkoutStatus = CheckoutStatus.PENDING,
            dateTime = LocalDateTime(2024, 12, 1, 10, 15, 0)
        ),
        ReceiptData(
            buyerName = "Nicole Lewis",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Study Bible - ESV",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Language",
                            valueList = listOf("Spanish"),
                            selectedValue = "Spanish"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("Regular"),
                            selectedValue = "Regular"
                        )
                    ),
                    totalPrice = 59.99
                ),
                CheckoutItem(
                    itemName = "Scripture Art Print",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Verse",
                            valueList = listOf("Philippians 4:13"),
                            selectedValue = "Philippians 4:13"
                        ),
                        CheckoutItem.Variant(
                            key = "Size",
                            valueList = listOf("8x10"),
                            selectedValue = "8x10"
                        )
                    ),
                    totalPrice = 28.00
                ),
                CheckoutItem(
                    itemName = "Christian Book - Purpose Driven Life",
                    quantity = 1,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Format",
                            valueList = listOf("Large Print"),
                            selectedValue = "Large Print"
                        )
                    ),
                    totalPrice = 22.00
                )
            ),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2024, 12, 1, 15, 30, 0)
        )
    )
}

