import { useEffect, useState } from 'react'
import { collection, getDocs, doc, updateDoc, deleteDoc, query, where } from 'firebase/firestore'
import { db } from '../firebase'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import ErrorState from '../components/ErrorState'

interface PendingTrail { id: string; name: string; mountainName: string; difficulty: string; authorName: string; status: string; description: string; createdAt: number }

export default function PendingTrailsPage() {
  const [trails, setTrails] = useState<PendingTrail[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchPending = async () => {
    setLoading(true)
    try {
      const snap = await getDocs(query(collection(db, 'trails'), where('status', '==', 'pending')))
      setTrails(snap.docs.map(d => ({ id: d.id, ...d.data() } as PendingTrail)))
    } catch (err: any) { setError(err.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { fetchPending() }, [])

  const handleApprove = async (id: string) => {
    try {
      await updateDoc(doc(db, 'trails', id), { status: 'approved' })
      setTrails(prev => prev.filter(t => t.id !== id))
    } catch (err: any) { setError(err.message) }
  }

  const handleReject = async (id: string) => {
    try {
      await deleteDoc(doc(db, 'trails', id))
      setTrails(prev => prev.filter(t => t.id !== id))
    } catch (err: any) { setError(err.message) }
  }

  if (loading) return <LoadingState />
  if (error) return <ErrorState message={error} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Pending Trails</h1>
        <button onClick={fetchPending} className="text-sm text-emerald-700 hover:underline">Refresh</button>
      </div>

      {!trails.length && <EmptyState message="No pending trails" />}

      <div className="space-y-3">
        {trails.map(t => (
          <div key={t.id} className="bg-white rounded-xl shadow-sm p-4">
            <div className="flex items-start justify-between">
              <div>
                <p className="font-semibold text-gray-800">{t.name}</p>
                <p className="text-sm text-gray-500">{t.mountainName} • {t.difficulty}</p>
                <p className="text-sm text-gray-400">by {t.authorName || 'Anonymous'}</p>
                {t.description && <p className="text-sm text-gray-600 mt-2">{t.description}</p>}
              </div>
              <div className="flex gap-2">
                <button onClick={() => handleApprove(t.id)}
                  className="bg-emerald-700 hover:bg-emerald-800 text-white px-4 py-2 rounded-lg text-sm font-medium transition">Approve</button>
                <button onClick={() => handleReject(t.id)}
                  className="bg-red-100 hover:bg-red-200 text-red-700 px-4 py-2 rounded-lg text-sm font-medium transition">Reject</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
