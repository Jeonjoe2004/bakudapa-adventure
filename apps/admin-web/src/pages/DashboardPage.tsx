import { useEffect, useState } from 'react'
import { collection, getDocs } from 'firebase/firestore'
import { db } from '../firebase'
import { Mountain, Users, Map, TrendingUp } from 'lucide-react'

interface Stats { totalMountains: number; totalUsers: number; totalTrails: number; activeToday: number }

export default function DashboardPage() {
  const [stats, setStats] = useState<Stats | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    ;(async () => {
      try {
        const [mCount, uCount, tCount] = await Promise.all([
          getDocs(collection(db, 'mountains')).then(s => s.size),
          getDocs(collection(db, 'users')).then(s => s.size),
          getDocs(collection(db, 'trails')).then(s => s.size),
        ])
        setStats({ totalMountains: mCount, totalUsers: uCount, totalTrails: tCount, activeToday: 0 })
      } catch (err: any) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    })()
  }, [])

  const cards = stats ? [
    { label: 'Mountains', value: stats.totalMountains, icon: Mountain, color: 'bg-emerald-500' },
    { label: 'Users', value: stats.totalUsers, icon: Users, color: 'bg-blue-500' },
    { label: 'Trails', value: stats.totalTrails, icon: Map, color: 'bg-amber-500' },
    { label: 'Active Today', value: stats.activeToday, icon: TrendingUp, color: 'bg-violet-500' },
  ] : []

  if (loading) return <div className="flex items-center justify-center py-20"><div className="size-8 border-4 border-emerald-600 border-t-transparent rounded-full animate-spin" /></div>

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>
      {error && <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-2">{error}</div>}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {cards.map(c => (
          <div key={c.label} className="bg-white rounded-xl shadow-sm p-5 flex items-center gap-4">
            <div className={`${c.color} p-3 rounded-lg text-white`}><c.icon size={24} /></div>
            <div>
              <p className="text-2xl font-bold text-gray-800">{c.value}</p>
              <p className="text-sm text-gray-500">{c.label}</p>
            </div>
          </div>
        ))}
      </div>
      <div className="bg-white rounded-xl shadow-sm p-6">
        <h2 className="font-semibold text-gray-700 mb-2">Recent Activity</h2>
        <p className="text-gray-400 text-sm">Coming soon — real-time activity feed.</p>
      </div>
    </div>
  )
}
