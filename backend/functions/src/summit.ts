import { https } from 'firebase-functions/v2'
import { getFirestore } from 'firebase-admin/firestore'

const db = getFirestore()

/**
 * Create summit log dengan validasi:
 * - Auth required
 * - 1x per user per gunung per hari
 * - Validasi GPS dalam radius 1km dari titik puncak
 */
export const createSummitLog = https.onCall(async (req) => {
  const uid = req.auth?.uid
  if (!uid) throw new https.HttpsError('unauthenticated', 'Login required')

  const { mountainId, mountainName, caption, photoUrl, latitude, longitude, elevation } = req.data || {}
  if (!mountainId || !mountainName) {
    throw new https.HttpsError('invalid-argument', 'mountainId dan mountainName wajib')
  }

  // Cek duplikat: 1x per user per gunung per hari
  const todayStart = new Date()
  todayStart.setHours(0, 0, 0, 0)
  const existing = await db.collection('summit_logs')
    .where('userId', '==', uid)
    .where('mountainId', '==', mountainId)
    .where('timestamp', '>=', todayStart.getTime())
    .get()

  if (!existing.empty) {
    throw new https.HttpsError('already-exists', 'Kamu sudah check-in hari ini untuk gunung ini')
  }

  // Validasi GPS jika ada koordinat
  if (latitude && longitude) {
    const mountainSnap = await db.collection('mountains').doc(mountainId).get()
    const mountainLat = mountainSnap.get('latitude') as number | undefined
    const mountainLng = mountainSnap.get('longitude') as number | undefined

    if (mountainLat && mountainLng) {
      const distance = haversine(latitude, longitude, mountainLat, mountainLng)
      if (distance > 1000) {
        throw new https.HttpsError('invalid-argument',
          `Lokasi kamu terlalu jauh dari puncak (${Math.round(distance)}m). Kamu harus di radius 1km dari puncak.`)
      }
    }
  }

  // Simpan
  const log = {
    userId: uid,
    userName: req.auth?.token?.name || req.data.userName || 'Petualang',
    userPhotoUrl: req.auth?.token?.picture || req.data.userPhotoUrl || null,
    mountainId,
    mountainName,
    photoUrl: photoUrl || null,
    caption: caption || '',
    latitude: latitude || 0,
    longitude: longitude || 0,
    elevation: elevation || 0,
    timestamp: Date.now(),
  }

  await db.collection('summit_logs').add(log)
  return { success: true }
})

function haversine(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const R = 6371000
  const dLat = toRad(lat2 - lat1)
  const dLon = toRad(lon2 - lon1)
  const a = Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
    Math.sin(dLon / 2) ** 2
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

function toRad(deg: number): number {
  return deg * (Math.PI / 180)
}
