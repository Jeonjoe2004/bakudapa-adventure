/**
 * Seed script untuk Firestore dengan data gunung & jalur Indonesia.
 *
 * Cara pakai:
 *   1. npm install -g tsx
 *   2. cd scripts
 *   3. tsx seed-firestore.ts
 *
 * Atau pake Node biasa:
 *   1. npm install firebase
 *   2. node seed-firestore.mjs
 */

import { initializeApp, cert } from "firebase-admin/app"
import { getFirestore } from "firebase-admin/firestore"
import mountains from "../seed_mountains.json"
import trails from "../seed_trails.json"
import { readFileSync } from "fs"

// Ganti dengan path service-account-mu
const serviceAccount = JSON.parse(
  readFileSync("../bakudapa-adventure-firebase-adminsdk.json", "utf-8")
)

const app = initializeApp({ credential: cert(serviceAccount) })
const db = getFirestore(app)

async function seed() {
  console.log("🌱 Seeding mountains...")
  for (const m of mountains) {
    const id = `mount_${m.name.toLowerCase().replace(/\s+/g, "_")}`
    await db.collection("mountains").doc(id).set(m)
    console.log(`  ✅ ${m.name} (${id})`)
  }

  console.log("\n🌱 Seeding trails...")
  for (const t of trails) {
    await db.collection("trails").add(t)
    console.log(`  ✅ ${t.name}`)
  }

  console.log("\n🎉 Done! Data siap digunakan.")
}

seed().catch(console.error)
