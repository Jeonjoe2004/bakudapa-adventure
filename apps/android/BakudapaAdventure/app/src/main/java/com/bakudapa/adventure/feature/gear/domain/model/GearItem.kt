package com.bakudapa.adventure.feature.gear.domain.model

data class GearItem(
    val id: String,
    val name: String,
    val icon: String = "🎒",
    val category: GearCategory = GearCategory.OTHER,
    val isChecked: Boolean = false
)

enum class GearCategory(val label: String) {
    CLOTHING("Pakaian"),
    FOOTWEAR("Alas Kaki"),
    PACK("Tas & Perlengkapan"),
    SAFETY("Keselamatan"),
    CAMPING("Camping"),
    FOOD("Makanan & Minuman"),
    OTHER("Lainnya")
}

data class GearPreset(
    val id: String,
    val name: String,
    val items: List<GearItem>
)
