import { https } from 'firebase-functions/v2'
import { getFirestore } from 'firebase-admin/firestore'
import type { Trail } from './types'

const db = getFirestore()

function isAdmin(req: https.CallableRequest): boolean {
  return req.auth?.token?.admin === true
}

/** Validate required fields for a Trail */
function validateTrail(data: Record<string, unknown>): string | null {
  if (!data.name || typeof data.name !== 'string' || !data.name.trim())
    return 'name is required'
  if (!data.mountainId || typeof data.mountainId !== 'string')
    return 'mountainId is required'
  if (!data.mountainName || typeof data.mountainName !== 'string' || !data.mountainName.trim())
    return 'mountainName is required'
  const validDiffs = ['EASY', 'MODERATE', 'HARD', 'EXPERT']
  if (data.difficulty && !validDiffs.includes(String(data.difficulty)))
    return `difficulty must be one of: ${validDiffs.join(', ')}`
  if (data.distanceKm != null && (typeof data.distanceKm !== 'number' || data.distanceKm < 0))
    return 'distanceKm must be a positive number'
  if (data.durationMinutes != null && (typeof data.durationMinutes !== 'number' || data.durationMinutes < 0))
    return 'durationMinutes must be a positive number'
  return null
}

export const listTrails = https.onCall(async (req) => {
  if (!req.auth) throw new https.HttpsError('unauthenticated', 'Login required')
  let query: FirebaseFirestore.Query = db.collection('trails').orderBy('name')
  if (req.data?.mountainId) query = query.where('mountainId', '==', req.data.mountainId)
  const snap = await query.get()
  return snap.docs.map(d => ({ id: d.id, ...d.data() }))
})

export const getTrail = https.onCall(async (req) => {
  if (!req.auth) throw new https.HttpsError('unauthenticated', 'Login required')
  const id = req.data?.id as string
  if (!id) throw new https.HttpsError('invalid-argument', 'id required')
  const doc = await db.collection('trails').doc(id).get()
  if (!doc.exists) throw new https.HttpsError('not-found', 'Trail not found')
  return { id: doc.id, ...doc.data() }
})

export const createTrail = https.onCall(async (req) => {
  if (!isAdmin(req)) throw new https.HttpsError('permission-denied', 'Admin only')
  const err = validateTrail(req.data)
  if (err) throw new https.HttpsError('invalid-argument', err)
  const data: Trail = {
    name: req.data.name.trim(),
    mountainId: req.data.mountainId,
    mountainName: req.data.mountainName.trim(),
    difficulty: req.data.difficulty || 'MODERATE',
    distanceKm: typeof req.data.distanceKm === 'number' ? req.data.distanceKm : 0,
    durationMinutes: typeof req.data.durationMinutes === 'number' ? req.data.durationMinutes : 0,
    imageUrl: req.data.imageUrl || '',
    description: req.data.description ? String(req.data.description) : undefined,
    elevationGain: typeof req.data.elevationGain === 'number' ? req.data.elevationGain : undefined,
    maxElevation: typeof req.data.maxElevation === 'number' ? req.data.maxElevation : undefined,
    popularity: 0,
    createdAt: Date.now(),
  }
  const ref = await db.collection('trails').add(data)
  return { id: ref.id, ...data }
})

export const updateTrail = https.onCall(async (req) => {
  if (!isAdmin(req)) throw new https.HttpsError('permission-denied', 'Admin only')
  const id = req.data?.id as string
  if (!id) throw new https.HttpsError('invalid-argument', 'id required')
  const err = validateTrail({ ...req.data })
  if (err) throw new https.HttpsError('invalid-argument', err)
  const { id: _id, ...rest } = req.data
  await db.collection('trails').doc(id).update(rest)
  return { id, ...rest }
})

export const deleteTrail = https.onCall(async (req) => {
  if (!isAdmin(req)) throw new https.HttpsError('permission-denied', 'Admin only')
  const id = req.data?.id as string
  if (!id) throw new https.HttpsError('invalid-argument', 'id required')
  await db.collection('trails').doc(id).delete()
  return { deleted: id }
})
