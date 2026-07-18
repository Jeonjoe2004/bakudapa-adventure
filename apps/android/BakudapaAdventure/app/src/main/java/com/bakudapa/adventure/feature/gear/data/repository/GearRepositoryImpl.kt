package com.bakudapa.adventure.feature.gear.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.gear.domain.model.GearCategory
import com.bakudapa.adventure.feature.gear.domain.model.GearItem
import com.bakudapa.adventure.feature.gear.domain.model.GearPreset
import com.bakudapa.adventure.feature.gear.domain.repository.GearRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GearRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) : GearRepository {

    // Presets bawaan berdasarkan tingkat kesulitan
    private val allPresets = listOf(
        GearPreset("easy", "Pendakian Ringan (1 hari)", listOf(
            GearItem("e1", "Ransel 20-30L", "🎒", GearCategory.PACK),
            GearItem("e2", "Sepatu hiking", "🥾", GearCategory.FOOTWEAR),
            GearItem("e3", "Jas hujan / windbreaker", "🧥", GearCategory.CLOTHING),
            GearItem("e4", "Celana panjang", "👖", GearCategory.CLOTHING),
            GearItem("e5", "Baju ganti", "👕", GearCategory.CLOTHING),
            GearItem("e6", "Topi", "🧢", GearCategory.CLOTHING),
            GearItem("e7", "Air minum 1.5L", "💧", GearCategory.FOOD),
            GearItem("e8", "Makanan ringan", "🍫", GearCategory.FOOD),
            GearItem("e9", "Power bank", "🔋", GearCategory.SAFETY),
            GearItem("e10", "Senter / headlamp", "🔦", GearCategory.SAFETY),
            GearItem("e11", "P3K mini", "🩹", GearCategory.SAFETY),
            GearItem("e12", "Kantong plastik", "🛍️", GearCategory.OTHER),
        )),
        GearPreset("medium", "Pendakian 2-3 Hari", listOf(
            GearItem("m1", "Ransel 40-60L", "🎒", GearCategory.PACK),
            GearItem("m2", "Sepatu hiking", "🥾", GearCategory.FOOTWEAR),
            GearItem("m3", "Jaket gunung", "🧥", GearCategory.CLOTHING),
            GearItem("m4", "Celana gunung 2", "👖", GearCategory.CLOTHING),
            GearItem("m5", "Baju ganti 2", "👕", GearCategory.CLOTHING),
            GearItem("m6", "Sleeping bag", "🛌", GearCategory.CAMPING),
            GearItem("m7", "Matras", "🧘", GearCategory.CAMPING),
            GearItem("m8", "Tenda (bagi rombongan)", "⛺", GearCategory.CAMPING),
            GearItem("m9", "Kompor portable + gas", "🔥", GearCategory.FOOD),
            GearItem("m10", "Makanan 3 hari", "🍜", GearCategory.FOOD),
            GearItem("m11", "Air minum 2-3L + botol", "💧", GearCategory.FOOD),
            GearItem("m12", "Headlamp + baterai cadangan", "🔦", GearCategory.SAFETY),
            GearItem("m13", "Power bank 20000mAh", "🔋", GearCategory.PACK),
            GearItem("m14", "P3K lengkap", "🩹", GearCategory.SAFETY),
            GearItem("m15", "Tongkat hiking", "🏏", GearCategory.SAFETY),
            GearItem("m16", "Kantong sampah", "🗑️", GearCategory.OTHER),
            GearItem("m17", "Dokumen (KTP/identitas)", "🪪", GearCategory.OTHER),
        )),
        GearPreset("hard", "Ekspedisi (3+ Hari)", listOf(
            GearItem("h1", "Ransel 60-80L", "🎒", GearCategory.PACK),
            GearItem("h2", "Sepatu hiking high-cut", "🥾", GearCategory.FOOTWEAR),
            GearItem("h3", "Jaket down/thermal", "🧥", GearCategory.CLOTHING),
            GearItem("h4", "Base layer (2)", "👕", GearCategory.CLOTHING),
            GearItem("h5", "Celana gunung 2", "👖", GearCategory.CLOTHING),
            GearItem("h6", "Sarung tangan", "🧤", GearCategory.CLOTHING),
            GearItem("h7", "Kupluk / buff", "🧣", GearCategory.CLOTHING),
            GearItem("h8", "Tenda + flysheet", "⛺", GearCategory.CAMPING),
            GearItem("h9", "Sleeping bag (comfort 0°C)", "🛌", GearCategory.CAMPING),
            GearItem("h10", "Matras isolasi", "🧘", GearCategory.CAMPING),
            GearItem("h11", "Kompor + gas cadangan", "🔥", GearCategory.FOOD),
            GearItem("h12", "Makanan 5+ hari", "🍜", GearCategory.FOOD),
            GearItem("h13", "Water filter / tablet", "💧", GearCategory.SAFETY),
            GearItem("h14", "Headlamp + baterai", "🔦", GearCategory.SAFETY),
            GearItem("h15", "P3K lengkap + obat pribadi", "🩹", GearCategory.SAFETY),
            GearItem("h16", "GPS / kompas", "🧭", GearCategory.SAFETY),
            GearItem("h17", "Peta + guidebook", "🗺️", GearCategory.SAFETY),
            GearItem("h18", "Tongkat hiking (2)", "🏏", GearCategory.SAFETY),
            GearItem("h19", "Kantong sampah besar", "🗑️", GearCategory.OTHER),
            GearItem("h20", "Sunscreen + lip balm", "🧴", GearCategory.SAFETY),
        )),
    )

    override fun getGearPresets(): List<GearPreset> = allPresets

    override fun getSavedChecklist(mountainId: String): Flow<DataResult<List<GearItem>>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val listener = firestoreManager.getCollection("users").document(uid)
            .collection("gearChecklists").document(mountainId)
            .addSnapshotListener { snap, error ->
                if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
                val items = if (snap?.exists() == true) {
                    val saved = snap.get("items") as? List<Map<String, Any>> ?: emptyList()
                    saved.map { item ->
                        GearItem(
                            id = item["id"] as? String ?: "",
                            name = item["name"] as? String ?: "",
                            icon = item["icon"] as? String ?: "🎒",
                            category = try { GearCategory.valueOf(item["category"] as? String ?: "OTHER") } catch (_: Exception) { GearCategory.OTHER },
                            isChecked = item["isChecked"] as? Boolean ?: false
                        )
                    }
                } else emptyList()
                trySend(DataResult.Success(items))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveChecklist(mountainId: String, items: List<GearItem>): DataResult<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            val data = items.map { mapOf(
                "id" to it.id,
                "name" to it.name,
                "icon" to it.icon,
                "category" to it.category.name,
                "isChecked" to it.isChecked
            )}
            firestoreManager.getCollection("users").document(uid)
                .collection("gearChecklists").document(mountainId)
                .set(mapOf("items" to data)).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun toggleItem(mountainId: String, itemId: String): DataResult<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            val doc = firestoreManager.getCollection("users").document(uid)
                .collection("gearChecklists").document(mountainId).get().await()
            val items = (doc.get("items") as? List<Map<String, Any>> ?: emptyList()).map { item ->
                if (item["id"] == itemId) item.toMutableMap().apply { this["isChecked"] = !(this["isChecked"] as? Boolean ?: false) }
                else item
            }
            firestoreManager.getCollection("users").document(uid)
                .collection("gearChecklists").document(mountainId)
                .update("items", items).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
