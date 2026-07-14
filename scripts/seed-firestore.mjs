/**
 * Seed Firestore dengan data gunung & jalur Indonesia.
 *
 * Cara pakai:
 *   1. Dapatkan service account key dari Firebase Console
 *      Project Settings > Service Accounts > Generate New Private Key
 *   2. Simpan sebagai bakudapa-adventure-firebase-adminsdk.json di project root
 *   3. Jalankan:
 *
 *      npm install firebase-admin
 *      node scripts/seed-firestore.mjs
 */

import { initializeApp, cert } from "firebase-admin/app"
import { getFirestore } from "firebase-admin/firestore"
import { readFileSync } from "fs"
import { createRequire } from "module"

const require = createRequire(import.meta.url)
const mountains = require("../seed_mountains.json")
const trails = require("../seed_trails.json")

// Coba load service account dari beberapa lokasi
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

async function seed() {
  console.log("🌱 Seeding mountains...")
  const results = []
  for (const m of mountains) {
    const id = `mount_${m.name.toLowerCase().replace(/\s+/g, "_")}`
    await db.collection("mountains").doc(id).set(m)
    results.push(id)
    console.log(`  ✅ ${m.name} (${id})`)
  }

  // Simpan ID mapping supaya trail bisa refer
  for (const [idx, m] of mountains.entries()) {
    const id = results[idx]
    await db.collection("mountains").doc(id).update({
      createdAt: m.createdAt ?? Date.now(),
      updatedAt: Date.now(),
    })
  }

  console.log("\n🌱 Seeding trails...")
  for (const t of trails) {
    await db.collection("trails").add(t)
    console.log(`  ✅ ${t.name}`)
  }

  console.log("\n🎉 Done! %d mountains, %d trails seeded.", mountains.length, trails.length)
}

seed().catch(console.error)
