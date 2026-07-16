import { useEffect, useState } from 'react'
import { collection, getDocs, deleteDoc, doc, query, orderBy } from 'firebase/firestore'
import { db } from '../firebase'
import { Trash2, Search } from 'lucide-react'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import ErrorState from '../components/ErrorState'

interface Review { id: string; trailId: string; authorName: string; rating: number; comment: string; timestamp: number }

export default function ReviewsPage() {
  const [reviews, setReviews] = useState<Review[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')

  const fetchReviews = async () => {
    try {
      const snap = await getDocs(query(collection(db, 'trail_reviews'), orderBy('timestamp', 'desc')))
      setReviews(snap.docs.map(d => ({ id: d.id, ...d.data() } as Review)))
    } catch (err: any) { setError(err.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { fetchReviews() }, [])

  const handleDelete = async (id: string) => {
    if (!confirm('Delete this review?')) return
    try { await deleteDoc(doc(db, 'trail_reviews', id)); fetchReviews() }
    catch (err: any) { setError(err.message) }
  }

  const filtered = reviews.filter(r =>
    r.authorName.toLowerCase().includes(search.toLowerCase()) ||
    r.comment.toLowerCase().includes(search.toLowerCase())
  )

  if (loading) return <LoadingState />
  if (error) return <ErrorState message={error} />

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-gray-800">Reviews</h1>

      <div className="relative">
        <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search reviews..."
          className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
      </div>

      {!filtered.length && <EmptyState message={search ? 'No reviews match your search' : 'No reviews yet'} />}

      <div className="space-y-3">
        {filtered.map(r => (
          <div key={r.id} className="bg-white rounded-xl shadow-sm p-4 flex items-start justify-between">
            <div>
              <div className="flex items-center gap-2">
                <p className="font-semibold text-gray-800">{r.authorName}</p>
                <span className="text-yellow-500 text-sm">{'★'.repeat(Math.round(r.rating))}{'☆'.repeat(5 - Math.round(r.rating))}</span>
              </div>
              <p className="text-sm text-gray-600 mt-1">{r.comment}</p>
              <p className="text-xs text-gray-400 mt-1">{new Date(r.timestamp).toLocaleDateString()}</p>
            </div>
            <button onClick={() => handleDelete(r.id)} className="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition">
              <Trash2 size={18} />
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
