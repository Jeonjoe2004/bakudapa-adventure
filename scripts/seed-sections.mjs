/**
 * Seed data untuk mountain sections (pos, basecamp, camping ground, water source).
 *
 * Jalankan setelah seed utama:
 *   node scripts/seed-sections.mjs
 *
 * Atau jalanin langsung setelah seed-firestore:
 *   node scripts/seed-firestore.mjs && node scripts/seed-sections.mjs
 */

import { initializeApp, cert } from "firebase-admin/app"
import { getFirestore } from "firebase-admin/firestore"
import { readFileSync } from "fs"

let serviceAccount
try {
  serviceAccount = JSON.parse(
    readFileSync("../bakudapa-adventure-firebase-adminsdk.json", "utf-8")
  )
} catch {
  serviceAccount = JSON.parse(
    readFileSync("bakudapa-adventure-firebase-adminsdk.json", "utf-8")
  )
}

const app = initializeApp({ credential: cert(serviceAccount) })
const db = getFirestore(app)

const sections = {
  mount_klabat: [
    { name: "Basecamp Airmadidi", elevation: 750, type: "POS", lat: 1.42, lng: 125.01, facilities: ["Parkir", "Toilet", "Pos pendaftaran"] },
    { name: "Pos 1 - Hutan Tropis", elevation: 950, type: "POS", lat: 1.43, lng: 125.02, facilities: ["Area istirahat"] },
    { name: "Pos 2 - Batu Perhentian", elevation: 1200, type: "POS", lat: 1.44, lng: 125.025, facilities: ["Sumber air (musim hujan)"] },
    { name: "Pos 3 - Camping Ground", elevation: 1500, type: "CAMPGROUND", lat: 1.445, lng: 125.03, facilities: ["Area tenda", "Sumber air"] },
    { name: "Puncak Klabat", elevation: 1995, type: "SUMMIT", lat: 1.45, lng: 125.03, facilities: ["View 360° Laut Sulawesi", "Kawah"] },
  ],
  mount_lokon: [
    { name: "Basecamp Kakaskasen", elevation: 1050, type: "POS", lat: 1.34, lng: 124.83, facilities: ["Parkir", "Toilet", "Pos pendaftaran"] },
    { name: "Puncak Lokon", elevation: 1580, type: "SUMMIT", lat: 1.35, lng: 124.83, facilities: ["Kawah aktif", "View Tomohon"] },
    { name: "Kawah Lokon", elevation: 1550, type: "DANGER_ZONE", lat: 1.355, lng: 124.825, facilities: ["Area berbahaya", "Bau belerang"] },
  ],
  mount_mahawu: [
    { name: "Basecamp Rurukan", elevation: 1000, type: "POS", lat: 1.34, lng: 124.87, facilities: ["Parkir", "Toilet"] },
    { name: "Bukit Doa", elevation: 1150, type: "CAMPGROUND", lat: 1.345, lng: 124.86, facilities: ["Area tenda", "View sunrise", "Toilet"] },
    { name: "Puncak Mahawu", elevation: 1311, type: "SUMMIT", lat: 1.358, lng: 124.855, facilities: ["Kawah", "View Tondano", "Sunrise spot"] },
  ],
  mount_soputan_mama: [
    { name: "Basecamp Tombatu", elevation: 600, type: "POS", lat: 0.87, lng: 124.71, facilities: ["Parkir", "Pos pendaftaran"] },
    { name: "Pos 2 - Hutan Lindung", elevation: 950, type: "POS", lat: 0.88, lng: 124.72, facilities: ["Area istirahat"] },
    { name: "Pos 3 - Camp Terakhir", elevation: 1300, type: "CAMPGROUND", lat: 0.885, lng: 124.725, facilities: ["Area tenda", "Sumber air"] },
    { name: "Puncak Soputan Mama", elevation: 1784, type: "SUMMIT", lat: 0.89, lng: 124.73, facilities: ["Kawah aktif", "View Minahasa"] },
  ],
  mount_soputan_anak: [
    { name: "Basecamp Ratahan", elevation: 600, type: "POS", lat: 0.86, lng: 124.75, facilities: ["Parkir"] },
    { name: "Pos 1 - Perkebunan", elevation: 900, type: "POS", lat: 0.87, lng: 124.74, facilities: ["Air bersih"] },
    { name: "Puncak Soputan Anak", elevation: 1500, type: "SUMMIT", lat: 0.88, lng: 124.74, facilities: ["View Soputan Mama", "Camping area"] },
  ],
  mount_ambang: [
    { name: "Basecamp Sinsingon", elevation: 900, type: "POS", lat: 0.73, lng: 124.41, facilities: ["Parkir", "Pos pendaftaran"] },
    { name: "Hutan Lindung Ambang", elevation: 1100, type: "WATER_SOURCE", lat: 0.74, lng: 124.415, facilities: ["Air bersih", "Flora langka"] },
    { name: "Puncak Ambang", elevation: 1795, type: "SUMMIT", lat: 0.75, lng: 124.42, facilities: ["Camping", "View Bolaang Mongondow"] },
  ],
  mount_tampusu: [
    { name: "Basecamp Remboken", elevation: 800, type: "POS", lat: 1.11, lng: 124.71, facilities: ["Parkir", "Toilet"] },
    { name: "Pos 1 - Air", elevation: 950, type: "WATER_SOURCE", lat: 1.115, lng: 124.715, facilities: ["Sumber air bersih"] },
    { name: "Puncak Tampusu", elevation: 1200, type: "SUMMIT", lat: 1.12, lng: 124.72, facilities: ["Padang rumput", "Camping", "View Danau Tondano"] },
  ],
  mount_empung: [
    { name: "Basecamp Kakaskasen", elevation: 1050, type: "POS", lat: 1.34, lng: 124.83, facilities: ["Parkir", "Pos pendaftaran"] },
    { name: "Kawah Empung", elevation: 1340, type: "SUMMIT", lat: 1.355, lng: 124.825, facilities: ["Danau kawah", "View Tomohon"] },
  ],
}

async function seed() {
  console.log("🗺️  Seeding mountain sections...")
  let total = 0

  for (const [mountainId, items] of Object.entries(sections)) {
    const batch = db.batch()
    for (const item of items) {
      const ref = db.collection("mountains").doc(mountainId).collection("sections").doc()
      batch.set(ref, item)
      total++
    }
    await batch.commit()
    console.log(`  ✅ ${mountainId}: ${items.length} sections`)
  }

  console.log(`\n🎉 ${total} sections seeded!`)
}

seed().catch(console.error)
