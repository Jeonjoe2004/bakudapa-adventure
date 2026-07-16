import { identity, firestore } from 'firebase-functions/v2'
import { getFirestore } from 'firebase-admin/firestore'
import { getAuth } from 'firebase-admin/auth'
import { defineString } from 'firebase-functions/params'
import type { AppUser } from './types'
import type { BlockingFunction } from 'firebase-functions/v1'

const adminEmails = defineString('ADMIN_EMAILS', { default: 'admin@bakudapa.com' })
const db = getFirestore()

/** Create user doc + set admin claims when user registers */
export const onUserCreated: BlockingFunction = identity.beforeUserCreated(async (event) => {
  const user = event.data
  if (!user || !user.email) return

  const isAdmin = adminEmails.value().split(',').includes(user.email)
  const appUser: AppUser = {
    email: user.email,
    displayName: user.displayName || user.email.split('@')[0],
    photoUrl: user.photoURL || undefined,
    role: isAdmin ? 'admin' : 'user',
    createdAt: Date.now(),
  }

  await db.collection('users').doc(user.uid).set(appUser)

  if (isAdmin) {
    await getAuth().setCustomUserClaims(user.uid, { admin: true })
  }
})

/** Fallback: set admin claims when user doc is created (catches manual doc writes) */
export const setCustomClaims = firestore.onDocumentCreated('users/{userId}', async (event) => {
  const data = event.data?.data()
  if (!data?.email) return
  if (data.role === 'admin') {
    await getAuth().setCustomUserClaims(event.params.userId, { admin: true })
  }
})
