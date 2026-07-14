import { useEffect, useState } from 'react'
import { collection, getDocs, addDoc, deleteDoc, doc, updateDoc, query, orderBy } from 'firebase/firestore'
import { db } from '../firebase'
import { Plus, Pencil, Trash2, Search } from 'lucide-react'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import ErrorState from '../components/ErrorState'

interface Trail { id: string; name: string; mountainName: string; difficulty: string; distanceKm: number; durationMinutes: number; imageUrl: string }

const DIFFICULTIES = ['EASY', 'MODERATE', 'HARD', 'EXPERT']

export default function TrailsPage() {
  const [trails, setTrails] = useState<Trail[]>([])
  const [mountains, setMountains] = useState<{id:string;name:string}[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')
  const [editing, setEditing] = useState<Trail | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ name: '', mountainId: '', mountainName: '', difficulty: 'MODERATE', distanceKm: 0, durationMinutes: 0, imageUrl: '', description: '', elevationGain: 0, maxElevation: 0 })

  const fetchTrails = async () => {
    try {
      const snap = await getDocs(query(collection(db, 'trails'), orderBy('name')))
      setTrails(snap.docs.map(d => ({ id: d.id, ...d.data() } as Trail)))
      const mSnap = await getDocs(collection(db, 'mountains'))
      setMountains(mSnap.docs.map(d => ({ id: d.id, name: d.data().name })))
    } catch (err: any) { setError(err.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { fetchTrails() }, [])

  const filtered = trails.filter(t =>
    t.name.toLowerCase().includes(search.toLowerCase()) ||
    t.mountainName.toLowerCase().includes(search.toLowerCase())
  )

  const openCreate = () => {
    setEditing(null)
    setForm({ name: '', mountainId: '', mountainName: '', difficulty: 'MODERATE', distanceKm: 0, durationMinutes: 0, imageUrl: '', description: '', elevationGain: 0, maxElevation: 0 })
    setShowForm(true)
  }

  const openEdit = (t: Trail) => {
    setEditing(t)
    setForm({ name: t.name, mountainId: '', mountainName: t.mountainName, difficulty: t.difficulty, distanceKm: t.distanceKm, durationMinutes: t.durationMinutes, imageUrl: t.imageUrl, description: '', elevationGain: 0, maxElevation: 0 })
    setShowForm(true)
  }

  const selectMountain = (id: string) => {
    const m = mountains.find(x => x.id === id)
    setForm({ ...form, mountainId: id, mountainName: m?.name || '' })
  }

  const handleSave = async () => {
    try {
      const data = { ...form }
      if (editing) {
        await updateDoc(doc(db, 'trails', editing.id), data)
      } else {
        await addDoc(collection(db, 'trails'), { ...data, popularity: 0, createdAt: Date.now() })
      }
      setShowForm(false)
      fetchTrails()
    } catch (err: any) { setError(err.message) }
  }

  const handleDelete = async (id: string) => {
    if (!confirm('Delete this trail?')) return
    try { await deleteDoc(doc(db, 'trails', id)); fetchTrails() }
    catch (err: any) { setError(err.message) }
  }

  if (loading) return <LoadingState />
  if (error) return <ErrorState message={error} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Trails</h1>
        <button onClick={openCreate} className="bg-emerald-700 hover:bg-emerald-800 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center gap-2 transition">
          <Plus size={18} /> Add Trail
        </button>
      </div>

      <div className="relative">
        <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search trails..."
          className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
      </div>

      {!filtered.length && <EmptyState message={search ? 'No trails match your search' : 'No trails yet'} />}

      {showForm && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50" onClick={() => setShowForm(false)}>
          <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-lg space-y-4 max-h-[90vh] overflow-y-auto" onClick={e => e.stopPropagation()}>
            <h2 className="text-lg font-bold">{editing ? 'Edit Trail' : 'Add Trail'}</h2>
            <div>
              <label className="text-sm font-medium text-gray-700">Name</label>
              <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Mountain</label>
              <select value={form.mountainId} onChange={e => selectMountain(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1">
                <option value="">Select mountain...</option>
                {mountains.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
              </select>
            </div>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="text-sm font-medium text-gray-700">Difficulty</label>
                <select value={form.difficulty} onChange={e => setForm({ ...form, difficulty: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1">
                  {DIFFICULTIES.map(d => <option key={d} value={d}>{d}</option>)}
                </select>
              </div>
              <div className="flex-1">
                <label className="text-sm font-medium text-gray-700">Distance (km)</label>
                <input type="number" step="0.1" value={form.distanceKm} onChange={e => setForm({ ...form, distanceKm: +e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
              </div>
            </div>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="text-sm font-medium text-gray-700">Duration (min)</label>
                <input type="number" value={form.durationMinutes} onChange={e => setForm({ ...form, durationMinutes: +e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
              </div>
              <div className="flex-1">
                <label className="text-sm font-medium text-gray-700">Elevation Gain (m)</label>
                <input type="number" value={form.elevationGain} onChange={e => setForm({ ...form, elevationGain: +e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
              </div>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Image URL</label>
              <input value={form.imageUrl} onChange={e => setForm({ ...form, imageUrl: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Description</label>
              <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <button onClick={() => setShowForm(false)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg transition">Cancel</button>
              <button onClick={handleSave} className="px-4 py-2 text-sm bg-emerald-700 text-white rounded-lg hover:bg-emerald-800 transition">{editing ? 'Update' : 'Create'}</button>
            </div>
          </div>
        </div>
      )}

      <div className="grid gap-3">
        {filtered.map(t => (
          <div key={t.id} className="bg-white rounded-xl shadow-sm p-4 flex items-center gap-4">
            <div className="size-14 rounded-lg bg-gray-100 overflow-hidden shrink-0">
              {t.imageUrl ? <img src={t.imageUrl} alt="" className="size-full object-cover" /> : <div className="size-full flex items-center justify-center text-gray-400 text-xs">No img</div>}
            </div>
            <div className="flex-1 min-w-0">
              <p className="font-semibold text-gray-800 truncate">{t.name}</p>
              <p className="text-sm text-gray-500">{t.mountainName} • {t.distanceKm}km • {t.durationMinutes}min</p>
              <span className={`inline-block text-xs font-bold px-2 py-0.5 rounded mt-1 ${
                t.difficulty === 'EASY' ? 'bg-green-100 text-green-700' :
                t.difficulty === 'MODERATE' ? 'bg-amber-100 text-amber-700' :
                t.difficulty === 'HARD' ? 'bg-red-100 text-red-700' :
                'bg-purple-100 text-purple-700'
              }`}>{t.difficulty}</span>
            </div>
            <div className="flex gap-1">
              <button onClick={() => openEdit(t)} className="p-2 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition"><Pencil size={18} /></button>
              <button onClick={() => handleDelete(t.id)} className="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition"><Trash2 size={18} /></button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
