package lat.pam.kopidannyemil

import java.util.Locale

data class User(val fullName: String, val username: String, val password: String)

enum class MenuCategory(val displayName: String) {
    COFFEE("Kopi"),
    TEA("Teh"),
    BAKERY("Roti & Pastry"),
    DESSERT("Dessert")
}

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val emoji: String,
    val category: MenuCategory = MenuCategory.COFFEE
)

val dummyMenu = listOf(
    MenuItem(1, "Kopi Susu Gula Aren", "Espresso, susu, gula aren", 22_000, "☕", MenuCategory.COFFEE),
    MenuItem(2, "Donat Cokelat", "Donat empuk, glaze cokelat", 12_000, "🍩", MenuCategory.BAKERY),
    MenuItem(3, "Sourdough Loaf", "Roti fermentasi alami", 35_000, "🍞", MenuCategory.BAKERY),
    MenuItem(4, "Cappuccino", "Espresso, foam susu lembut", 25_000, "🥛", MenuCategory.COFFEE),
    MenuItem(5, "Matcha Latte", "Matcha Jepang, susu segar", 27_000, "🍵", MenuCategory.TEA),
    MenuItem(6, "Croissant Butter", "Renyah, butter Prancis", 20_000, "🥐", MenuCategory.BAKERY),
    MenuItem(7, "NYC Cookie", "Tebal, padat, lumer", 17_000, "🍪", MenuCategory.DESSERT),
    MenuItem(8, "Jasmine Tea", "Diramu dengan bunga melati", 8_000, "🫖", MenuCategory.TEA),
    MenuItem(9, "Gelato", "Susu, krim, gula", 15_000, "🍨", MenuCategory.DESSERT),
    MenuItem(10, "Dubai Chewy Cookie", "Marshmallow, bubuk kakao, pistachio", 30_000, "🍪", MenuCategory.DESSERT),
)

fun rupiah(value: Int): String =
    "Rp " + String.format(Locale.forLanguageTag("id-ID"), "%,d", value)
