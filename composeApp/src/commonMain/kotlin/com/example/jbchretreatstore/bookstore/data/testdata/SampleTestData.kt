package com.example.jbchretreatstore.bookstore.data.testdata

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime
import kotlin.math.round

/**
 * Sample test data for development and testing purposes
 * Contains 100 display items and 500 receipts across multiple months/years
 */
object SampleTestData {

    // Item name templates for generating display items
    private val itemCategories = listOf(
        Triple("Holy Bible", listOf("NIV", "ESV", "KJV", "NASB", "NLT"), 35.99..65.99),
        Triple("Study Bible", listOf("ESV", "NIV", "NKJV"), 49.99..79.99),
        Triple("Devotional Journal", listOf("Daily", "Weekly", "Monthly"), 15.99..25.99),
        Triple("Retreat T-Shirt", listOf("2024", "2025", "Classic"), 20.00..30.00),
        Triple("Prayer Bracelet", listOf("Wood", "Stone", "Metal"), 10.00..18.00),
        Triple("Worship CD", listOf("Vol 1", "Vol 2", "Vol 3", "Live"), 12.99..18.99),
        Triple("Cross Necklace", listOf("Simple", "Ornate", "Minimalist"), 25.00..45.00),
        Triple("Christian Book", listOf("Bestseller", "Classic", "New Release"), 18.99..28.99),
        Triple("Retreat Hoodie", listOf("Pullover", "Zip-up"), 40.00..55.00),
        Triple("Scripture Art Print", listOf("Modern", "Classic", "Watercolor"), 22.00..35.00),
        Triple("Prayer Card Set", listOf("Standard", "Premium"), 8.99..15.99),
        Triple("Hymnal", listOf("Traditional", "Contemporary"), 25.00..40.00),
        Triple("Faith Mug", listOf("Ceramic", "Travel"), 12.00..20.00),
        Triple("Bookmark Set", listOf("Leather", "Laminated", "Fabric"), 5.99..12.99),
        Triple("Candle", listOf("Prayer", "Worship", "Meditation"), 15.00..28.00),
        Triple("Wall Cross", listOf("Wood", "Metal", "Ceramic"), 30.00..75.00),
        Triple("Bible Cover", listOf("Leather", "Canvas", "Nylon"), 20.00..45.00),
        Triple("Communion Set", listOf("Personal", "Family", "Church"), 35.00..120.00),
        Triple("Worship Flag", listOf("Silk", "Polyester"), 25.00..50.00),
        Triple("Faith Stickers", listOf("Pack of 10", "Pack of 25", "Pack of 50"), 4.99..12.99)
    )

    private val colors =
        listOf("White", "Navy", "Black", "Gray", "Burgundy", "Forest Green", "Royal Blue", "Cream")
    private val sizes = listOf("XS", "S", "M", "L", "XL", "2XL", "3XL")
    private val languages =
        listOf("English", "Spanish", "Chinese", "Korean", "Portuguese", "French")
    private val materials = listOf("Leather", "Cloth", "Paperback", "Hardcover", "Premium")
    private val chainLengths = listOf("14 inch", "16 inch", "18 inch", "20 inch", "22 inch")
    private val verses = listOf(
        "Jeremiah 29:11", "Psalm 23:1", "Philippians 4:13", "Proverbs 3:5-6",
        "Romans 8:28", "Isaiah 41:10", "John 3:16", "Matthew 11:28",
        "Psalm 46:10", "Joshua 1:9", "Ephesians 2:8-9", "2 Timothy 1:7"
    )

    private val firstNames = listOf(
        "John",
        "Mary",
        "Robert",
        "Sarah",
        "Michael",
        "Emily",
        "David",
        "Jennifer",
        "Chris",
        "Amanda",
        "Kevin",
        "Lisa",
        "Jason",
        "Michelle",
        "Daniel",
        "Rachel",
        "Steven",
        "Angela",
        "Brian",
        "Nicole",
        "James",
        "Jessica",
        "William",
        "Ashley",
        "Thomas",
        "Megan",
        "Andrew",
        "Lauren",
        "Joshua",
        "Samantha",
        "Matthew",
        "Hannah",
        "Anthony",
        "Elizabeth",
        "Mark",
        "Kayla",
        "Paul",
        "Brittany",
        "Timothy",
        "Victoria",
        "Ryan",
        "Stephanie",
        "Nathan",
        "Christina",
        "Eric",
        "Heather",
        "Adam",
        "Amber",
        "Benjamin",
        "Rebecca"
    )

    private val lastNames = listOf(
        "Smith",
        "Johnson",
        "Williams",
        "Brown",
        "Jones",
        "Garcia",
        "Miller",
        "Davis",
        "Rodriguez",
        "Martinez",
        "Hernandez",
        "Lopez",
        "Gonzalez",
        "Wilson",
        "Anderson",
        "Thomas",
        "Taylor",
        "Moore",
        "Jackson",
        "Martin",
        "Lee",
        "Perez",
        "Thompson",
        "White",
        "Harris",
        "Sanchez",
        "Clark",
        "Ramirez",
        "Lewis",
        "Robinson",
        "Walker",
        "Young",
        "Allen",
        "King",
        "Wright",
        "Scott",
        "Torres",
        "Nguyen",
        "Hill",
        "Flores",
        "Green",
        "Adams",
        "Nelson",
        "Baker",
        "Hall",
        "Rivera",
        "Campbell",
        "Mitchell",
        "Carter",
        "Roberts"
    )

    private val paymentMethods = PaymentMethod.entries.toTypedArray()

    val sampleDisplayItems: List<DisplayItem> = generateDisplayItems()

    val samplePurchaseHistory: List<ReceiptData> = generateReceipts()

    private fun generateDisplayItems(): List<DisplayItem> {
        val items = mutableListOf<DisplayItem>()
        var index = 0

        // Generate items from categories
        for ((baseName, variants, priceRange) in itemCategories) {
            for (variant in variants) {
                val itemName = "$baseName - $variant"
                val price =
                    (priceRange.start + (priceRange.endInclusive - priceRange.start) * (index % 10) / 10)
                        .let { round(it * 100) / 100 }

                val itemVariants = mutableListOf<DisplayItem.Variant>()

                // Add appropriate variants based on item type
                when {
                    baseName.contains("Bible") || baseName.contains("Book") || baseName.contains("Hymnal") -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Language",
                                valueList = languages.take(4)
                            )
                        )
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Cover",
                                valueList = materials.take(4)
                            )
                        )
                    }

                    baseName.contains("T-Shirt") || baseName.contains("Hoodie") -> {
                        itemVariants.add(DisplayItem.Variant(key = "Size", valueList = sizes))
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Color",
                                valueList = colors.take(5)
                            )
                        )
                    }

                    baseName.contains("Necklace") || baseName.contains("Bracelet") -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Material",
                                valueList = listOf("Silver", "Gold", "Rose Gold", "Bronze")
                            )
                        )
                        if (baseName.contains("Necklace")) {
                            itemVariants.add(
                                DisplayItem.Variant(
                                    key = "Chain Length",
                                    valueList = chainLengths.take(4)
                                )
                            )
                        } else {
                            itemVariants.add(
                                DisplayItem.Variant(
                                    key = "Size",
                                    valueList = listOf("Small", "Medium", "Large")
                                )
                            )
                        }
                    }

                    baseName.contains("Art Print") -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Verse",
                                valueList = verses.take(6)
                            )
                        )
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Size",
                                valueList = listOf("8x10", "11x14", "16x20", "24x36")
                            )
                        )
                    }

                    baseName.contains("Journal") -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Color",
                                valueList = colors.take(6)
                            )
                        )
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Pages",
                                valueList = listOf("100", "200", "365")
                            )
                        )
                    }

                    baseName.contains("CD") -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Artist",
                                valueList = listOf(
                                    "Hillsong",
                                    "Bethel",
                                    "Elevation",
                                    "Maverick City",
                                    "Chris Tomlin"
                                )
                            )
                        )
                    }

                    baseName.contains("Mug") -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Color",
                                valueList = colors.take(4)
                            )
                        )
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Size",
                                valueList = listOf("12oz", "16oz", "20oz")
                            )
                        )
                    }

                    baseName.contains("Candle") -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Scent",
                                valueList = listOf(
                                    "Frankincense",
                                    "Lavender",
                                    "Vanilla",
                                    "Cedar",
                                    "Rose"
                                )
                            )
                        )
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Size",
                                valueList = listOf("Small", "Medium", "Large")
                            )
                        )
                    }

                    else -> {
                        itemVariants.add(
                            DisplayItem.Variant(
                                key = "Style",
                                valueList = listOf("Classic", "Modern", "Contemporary")
                            )
                        )
                    }
                }

                items.add(DisplayItem(name = itemName, price = price, variants = itemVariants))
                index++

                if (items.size >= 100) break
            }
            if (items.size >= 100) break
        }

        return items
    }

    private fun generateReceipts(): List<ReceiptData> {
        val receipts = mutableListOf<ReceiptData>()

        // Generate receipts across different years and months
        // 2023: Jan-Dec (some months)
        // 2024: Jan-Dec (all months)
        // 2025: Jan-Dec (current year)

        val dateRanges = listOf(
            // 2023 - scattered months (50 receipts)
            Triple(2023, 3, 8),   // March 2023 - 8 receipts
            Triple(2023, 6, 10),  // June 2023 - 10 receipts
            Triple(2023, 9, 12),  // September 2023 - 12 receipts
            Triple(2023, 11, 10), // November 2023 - 10 receipts
            Triple(2023, 12, 10), // December 2023 - 10 receipts

            // 2024 - all months (250 receipts)
            Triple(2024, 1, 15),
            Triple(2024, 2, 18),
            Triple(2024, 3, 20),
            Triple(2024, 4, 22),
            Triple(2024, 5, 18),
            Triple(2024, 6, 25),
            Triple(2024, 7, 20),
            Triple(2024, 8, 22),
            Triple(2024, 9, 25),
            Triple(2024, 10, 20),
            Triple(2024, 11, 25),
            Triple(2024, 12, 20),

            // 2025 - current year (200 receipts)
            Triple(2025, 1, 25),
            Triple(2025, 2, 20),
            Triple(2025, 3, 25),
            Triple(2025, 4, 22),
            Triple(2025, 5, 20),
            Triple(2025, 6, 25),
            Triple(2025, 7, 20),
            Triple(2025, 8, 18),
            Triple(2025, 9, 15),
            Triple(2025, 10, 5),
            Triple(2025, 11, 3),
            Triple(2025, 12, 2)
        )

        var receiptIndex = 0

        for ((year, month, count) in dateRanges) {
            val daysInMonth = when (month) {
                2 -> if (year % 4 == 0) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }

            for (i in 0 until count) {
                val day = (i % daysInMonth) + 1
                val hour = 9 + (receiptIndex % 10)
                val minute = (receiptIndex * 7) % 60

                val buyerName =
                    "${firstNames[receiptIndex % firstNames.size]} ${lastNames[(receiptIndex * 3) % lastNames.size]}"
                val paymentMethod = paymentMethods[receiptIndex % paymentMethods.size]

                // Generate 1-3 checkout items per receipt
                val numItems = 1 + (receiptIndex % 3)
                val checkoutItems = mutableListOf<CheckoutItem>()

                for (j in 0 until numItems) {
                    val item = sampleDisplayItems[(receiptIndex + j) % sampleDisplayItems.size]
                    val quantity = 1 + ((receiptIndex + j) % 4)

                    val checkoutVariants = item.variants.map { variant ->
                        val selectedValue =
                            variant.valueList[(receiptIndex + j) % variant.valueList.size]
                        CheckoutItem.Variant(
                            key = variant.key,
                            valueList = listOf(selectedValue),
                            selectedValue = selectedValue
                        )
                    }

                    checkoutItems.add(
                        CheckoutItem(
                            itemName = item.name,
                            quantity = quantity,
                            variants = checkoutVariants,
                            totalPrice = item.price * quantity
                        )
                    )
                }

                val status = when (receiptIndex % 20) {
                    0 -> CheckoutStatus.PENDING
                    1 -> CheckoutStatus.SAVE_FOR_LATER
                    else -> CheckoutStatus.CHECKED_OUT
                }

                receipts.add(
                    ReceiptData(
                        buyerName = buyerName,
                        checkoutList = checkoutItems,
                        paymentMethod = paymentMethod,
                        checkoutStatus = status,
                        dateTime = LocalDateTime(year, month, day, hour, minute, 0)
                    )
                )

                receiptIndex++
            }
        }

        return receipts
    }
}
