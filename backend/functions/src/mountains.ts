import { https } from 'firebase-functions/v2'
import { getFirestore } from 'firebase-admin/firestore'
import type { Mountain } from './types'

const db = getFirestore()

function isAdmin(req: https.CallableRequest): boolean {
  return req.auth?.token?.admin === true
}

/** Validate required fields for a Mountain */
function validateMountain(data: Record<string, unknown>): string | null {
  if (!data.name || typeof data.name !== 'string' || !data.name.trim())
    return 'name is required'
  if (!data.location || typeof data.location !== 'string' || !data.location.trim())
    return 'location is required'
  if (typeof data.elevation !== 'number' || data.elevation < 0 || !Number.isFinite(data.elevation))
    return 'elevation must be a positive number'
  if (data.imageUrl && typeof data.imageUrl !== 'string')
    return 'imageUrl must be a string'
  return null
}

export const listMountains = https.onCall(async (req) => {
  if (!req.auth) throw new https.HttpsError('unauthenticated', 'Login required')
  const snap = await db.collection('mountains').orderBy('name').get()
  return snap.docs.map(d => ({ id: d.id, ...d.data() }))
})

export const getMountain = https.onCall(async (req) => {
  if (!req.auth) throw new https.HttpsError('unauthenticated', 'Login required')
  const id = req.data?.id as string
  if (!id) throw new https.HttpsError('invalid-argument', 'id required')
  const doc = await db.collection('mountains').doc(id).get()
  if (!doc.exists) throw new https.HttpsError('not-found', 'Mountain not found')
  return { id: doc.id, ...doc.data() }
})

export const createMountain = https.onCall(async (req) => {
  if (!isAdmin(req)) throw new https.HttpsError('permission-denied', 'Admin only')
  const err = validateMountain(req.data)
  if (err) throw new https.HttpsError('invalid-argument', err)
  const data: Mountain = {
    name: req.data.name.trim(),
    location: req.data.location.trim(),
    elevation: req.data.elevation,
    imageUrl: req.data.imageUrl || '',
    rating: typeof req.data.rating === 'number' ? req.data.rating : 0,
    latitude: typeof req.data.latitude === 'number' ? req.data.latitude : undefined,
    longitude: typeof req.data.longitude === 'number' ? req.data.longitude : undefined,
    description: req.data.description ? String(req.data.description) : undefined,
    difficulty: req.data.difficulty ? String(req.data.difficulty) : undefined,
    bestSeason: req.data.bestSeason ? String(req.data.bestSeason) : undefined,
    distance: typeof req.data.distance === 'number' ? req.data.distance : undefined,
    createdAt: Date.now(),
    updatedAt: Date.now(),
  }
  const ref = await db.collection('mountains').add(data)
  return { id: ref.id, ...data }
})

export const updateMountain = https.onCall(async (req) => {
  if (!isAdmin(req)) throw new https.HttpsError('permission-denied', 'Admin only')
  const id = req.data?.id as string
  if (!id) throw new https.HttpsError('invalid-argument', 'id required')
  const err = validateMountain({ ...req.data })
  if (err) throw new https.HttpsError('invalid-argument', err)
  const { id: _id, ...rest } = req.data
  const updates = { ...rest, updatedAt: Date.now() }
  await db.collection('mountains').doc(id).update(updates)
  return { id, ...updates }
})

export const deleteMountain = https.onCall(async (req) => {
  if (!isAdmin(req)) throw new https.HttpsError('permission-denied', 'Admin only')
  const id = req.data?.id as string
  if (!id) throw new https.HttpsError('invalid-argument', 'id required')
  await db.collection('mountains').doc(id).delete()
  return { deleted: id }
})
