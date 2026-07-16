import { https } from 'firebase-functions/v2'
import { getFirestore } from 'firebase-admin/firestore'

const db = getFirestore()

export const deletePost = https.onCall(async (req) => {
  if (req.auth?.token?.admin !== true) {
    throw new https.HttpsError('permission-denied', 'Admin only')
  }
  const id = req.data?.id as string
  if (!id) throw new https.HttpsError('invalid-argument', 'id required')
  await db.collection('posts').doc(id).delete()
  return { deleted: id }
})
