import { https } from 'firebase-functions/v2'
import { getFirestore } from 'firebase-admin/firestore'

const db = getFirestore()

function isAdmin(req: https.CallableRequest): boolean {
  return req.auth?.token?.admin === true
}

interface Article {
  title: string
  content: string
  author: string
  published: boolean
  createdAt: number
  updatedAt?: number
}

export const createArticle = https.onCall(async (req) => {
  if (!isAdmin(req)) throw new https.HttpsError('permission-denied', 'Admin only')
  const { title, content, author } = req.data || {}
  if (!title || typeof title !== 'string' || !title.trim())
    throw new https.HttpsError('invalid-argument', 'title is required')
  if (!content || typeof content !== 'string' || !content.trim())
    throw new https.HttpsError('invalid-argument', 'content is required')
  const article: Article = {
    title: title.trim(),
    content: content.trim(),
    author: typeof author === 'string' ? author.trim() : 'Admin',
    published: false,
    createdAt: Date.now(),
  }
  const ref = await db.collection('articles').add(article)
  return { id: ref.id, ...article }
})

export const listArticles = https.onCall(async (req) => {
  if (!req.auth) throw new https.HttpsError('unauthenticated', 'Login required')
  const snap = await db.collection('articles').orderBy('createdAt', 'desc').get()
  return snap.docs.map(d => ({ id: d.id, ...d.data() }))
})
