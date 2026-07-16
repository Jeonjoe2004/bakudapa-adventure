import { https, scheduler } from 'firebase-functions/v2'
import { getFirestore } from 'firebase-admin/firestore'
import type { DashboardStats } from './types'

const db = getFirestore()

export const getDashboardStats = https.onCall(async () => {
  const [mSnap, uSnap, tSnap, pSnap] = await Promise.all([
    db.collection('mountains').count().get(),
    db.collection('users').count().get(),
    db.collection('trails').count().get(),
    db.collection('posts').count().get(),
  ])

  return {
    totalMountains: mSnap.data().count,
    totalUsers: uSnap.data().count,
    totalTrails: tSnap.data().count,
    totalPosts: pSnap.data().count,
    activeToday: await estimateActiveToday(),
  } satisfies Omit<DashboardStats, 'updatedAt'>
})

/** Rough active-today estimate: users with activity in last 24h */
async function estimateActiveToday(): Promise<number> {
  try {
    const cutoff = Date.now() - 86_400_000
    const snap = await db.collection('users')
      .where('lastActiveAt', '>', cutoff)
      .count().get()
    return snap.data().count
  } catch {
    return 0 // field may not exist yet — non-fatal
  }
}

/** Refresh stats doc every 30 min */
export const scheduledStatsRefresh = scheduler.onSchedule('*/30 * * * *', async () => {
  const [mSnap, uSnap, tSnap, pSnap] = await Promise.all([
    db.collection('mountains').count().get(),
    db.collection('users').count().get(),
    db.collection('trails').count().get(),
    db.collection('posts').count().get(),
  ])

  const stats: DashboardStats = {
    totalMountains: mSnap.data().count,
    totalUsers: uSnap.data().count,
    totalTrails: tSnap.data().count,
    totalPosts: pSnap.data().count,
    activeToday: await estimateActiveToday(),
    updatedAt: Date.now(),
  }

  await db.collection('stats').doc('dashboard').set(stats)
})
