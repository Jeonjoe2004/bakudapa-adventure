package com.bakudapa.adventure.feature.premium.domain.model

data class PremiumPlan(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val monthlyPrice: Double = 0.0,
    val yearlyPrice: Double = 0.0,
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false,
)

data class PremiumSubscription(
    val userId: String = "",
    val planId: String = "",
    val isActive: Boolean = false,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val autoRenew: Boolean = true,
    val platform: SubscriptionPlatform = SubscriptionPlatform.UNKNOWN,
)

enum class SubscriptionPlatform {
    GOOGLE_PLAY, APP_STORE, UNKNOWN
}

data class PremiumFeatures(
    val hasOfflinePremiumMaps: Boolean = false,
    val hasCloudBackup: Boolean = false,
    val hasMultiDeviceSync: Boolean = false,
    val hasPrioritySupport: Boolean = false,
    val hasDetailedStats: Boolean = false,
    val hasAdFree: Boolean = false,
    val downloadLimit: Int = 0,
    val storageLimitMb: Int = 0,
)

val FREE_PLAN = PremiumPlan(
    id = "free",
    name = "Gratis",
    description = "Basic hiking features untuk semua pendaki",
    features = listOf(
        "Peta dasar & GPS tracking",
        "Daftar & detail gunung",
        "Riwayat pendakian",
        "Fitur darurat (SOS)",
        "Feed komunitas",
    ),
    monthlyPrice = 0.0,
    yearlyPrice = 0.0,
    isPopular = false,
)

val PREMIUM_MONTHLY = PremiumPlan(
    id = "premium_monthly",
    name = "Premium Bulanan",
    description = "Fitur lengkap untuk petualang serius",
    features = listOf(
        "Semua fitur gratis",
        "Peta offline premium",
        "Backup cloud otomatis",
        "Sinkronisasi multi-perangkat",
        "Statistik lengkap & analitik",
        "Prioritas support",
        "Bebas iklan",
        "Download hingga 10 region",
    ),
    monthlyPrice = 29900.0,
    yearlyPrice = 0.0,
    isPopular = false,
)

val PREMIUM_YEARLY = PremiumPlan(
    id = "premium_yearly",
    name = "Premium Tahunan",
    description = "Hemat 2 bulan dengan langganan tahunan",
    features = listOf(
        "Semua fitur Premium",
        "Download region tak terbatas",
        "Penyimpanan cloud 10GB",
        "Early access fitur baru",
        "Badge eksklusif",
    ),
    monthlyPrice = 0.0,
    yearlyPrice = 299000.0,
    isPopular = true,
)
