import { useEffect, useState } from 'react'
import { collection, getDocs, doc, query, orderBy, updateDoc } from 'firebase/firestore'
import { db } from '../firebase'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import ErrorState from '../components/ErrorState'

interface PoiEntry { name: string; type: string; latitude: number; longitude: number; elevation: number; description: string }

const POI_TYPES = ['TRAIL_HEAD', 'CAMPING_GROUND', 'WATER_SOURCE', 'SHELTER', 'SUMMIT', 'DANGER_ZONE']

export default function PoiPage() {
  const [trails, setTrails] = useState<{id:string;name:string;pointsOfInterest?:PoiEntry[]}[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedTrail, setSelectedTrail] = useState('')
  const [pois, setPois] = useState<PoiEntry[]>([])
  const [form, setForm] = useState<PoiEntry>({ name: '', type: 'TRAIL_HEAD', latitude: 0, longitude: 0, elevation: 0, description: '' })

  const fetchTrails = async () => {
    try {
      const snap = await getDocs(query(collection(db, 'trails'), orderBy('name')))
      const list = snap.docs.map(d => ({ id: d.id, ...d.data() }))
      setTrails(list as any)
    } catch (err: any) { setError(err.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { fetchTrails() }, [])

  const selectTrail = (id: string) => {
    setSelectedTrail(id)
    const t = trails.find(x => x.id === id)
    setPois(t?.pointsOfInterest || [])
  }

  const handleAdd = async () => {
    if (!form.name || !selectedTrail) return
    const updated = [...pois, form]
    try {
      await updateDoc(doc(db, 'trails', selectedTrail), { pointsOfInterest: updated })
      setPois(updated)
      setForm({ name: '', type: 'TRAIL_HEAD', latitude: 0, longitude: 0, elevation: 0, description: '' })
    } catch (err: any) { setError(err.message) }
  }

  const handleDelete = async (i: number) => {
    const updated = pois.filter((_, idx) => idx !== i)
    try {
      await updateDoc(doc(db, 'trails', selectedTrail), { pointsOfInterest: updated })
      setPois(updated)
    } catch (err: any) { setError(err.message) }
  }

  if (loading) return <LoadingState />
  if (error) return <ErrorState message={error} />

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-gray-800">Points of Interest</h1>

      <div>
        <label className="text-sm font-medium text-gray-700">Trail</label>
        <select value={selectedTrail} onChange={e => selectTrail(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1">
          <option value="">Select trail...</option>
          {trails.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
        </select>
      </div>

      {selectedTrail && (
        <>
          <div className="bg-white rounded-xl shadow-sm p-4 space-y-3">
            <h2 className="font-semibold text-gray-700">Add POI</h2>
            <div className="grid grid-cols-2 gap-3">
              <div className="col-span-2">
                <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Name"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <select value={form.type} onChange={e => setForm({ ...form, type: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500">
                  {POI_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
              <div className="flex gap-2">
                <input type="number" step="any" value={form.latitude} onChange={e => setForm({ ...form, latitude: +e.target.value })} placeholder="Lat"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
                <input type="number" step="any" value={form.longitude} onChange={e => setForm({ ...form, longitude: +e.target.value })} placeholder="Lng"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <input type="number" value={form.elevation} onChange={e => setForm({ ...form, elevation: +e.target.value })} placeholder="Elevation (m)"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <input value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="Description"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
            </div>
            <button onClick={handleAdd} disabled={!form.name}
              className="bg-emerald-700 hover:bg-emerald-800 text-white px-4 py-2 rounded-lg text-sm font-medium transition disabled:opacity-50">Add POI</button>
          </div>

          {!pois.length && <EmptyState message="No POI for this trail" />}

          <div className="space-y-2">
            {pois.map((poi, i) => (
              <div key={i} className="bg-white rounded-xl shadow-sm p-4 flex items-center justify-between">
                <div>
                  <p className="font-semibold text-gray-800">{poi.name}</p>
                  <p className="text-sm text-gray-500">{poi.type} • {poi.elevation}m • {poi.latitude.toFixed(4)}, {poi.longitude.toFixed(4)}</p>
                  {poi.description && <p className="text-sm text-gray-400">{poi.description}</p>}
                </div>
                <button onClick={() => handleDelete(i)} className="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
                </button>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  )
}
