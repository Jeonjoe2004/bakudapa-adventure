import { useEffect, useState } from 'react'
import { collection, getDocs, addDoc, deleteDoc, doc, updateDoc, query, orderBy } from 'firebase/firestore'
import { db } from '../firebase'
import { Plus, Pencil, Trash2, Search } from 'lucide-react'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import ErrorState from '../components/ErrorState'

interface Mountain { id: string; name: string; location: string; elevation: number; rating: number; imageUrl: string }

export default function MountainsPage() {
  const [mountains, setMountains] = useState<Mountain[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')
  const [editing, setEditing] = useState<Mountain | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ name: '', location: '', elevation: 0, rating: 0, imageUrl: '' })

  const fetchMountains = async () => {
    try {
      const snap = await getDocs(query(collection(db, 'mountains'), orderBy('name')))
      setMountains(snap.docs.map(d => ({ id: d.id, ...d.data() } as Mountain)))
    } catch (err: any) { setError(err.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { fetchMountains() }, [])

  const filtered = mountains.filter(m =>
    m.name.toLowerCase().includes(search.toLowerCase()) ||
    m.location.toLowerCase().includes(search.toLowerCase())
  )

  const openCreate = () => {
    setEditing(null)
    setForm({ name: '', location: '', elevation: 0, rating: 0, imageUrl: '' })
    setShowForm(true)
  }

  const openEdit = (m: Mountain) => {
    setEditing(m)
    setForm({ name: m.name, location: m.location, elevation: m.elevation, rating: m.rating, imageUrl: m.imageUrl })
    setShowForm(true)
  }

  const handleSave = async () => {
    try {
      if (editing) {
        await updateDoc(doc(db, 'mountains', editing.id), form)
      } else {
        await addDoc(collection(db, 'mountains'), form)
      }
      setShowForm(false)
      fetchMountains()
    } catch (err: any) { setError(err.message) }
  }

  const handleDelete = async (id: string) => {
    if (!confirm('Delete this mountain?')) return
    try {
      await deleteDoc(doc(db, 'mountains', id))
      fetchMountains()
    } catch (err: any) { setError(err.message) }
  }

  if (loading) return <LoadingState />
  if (error) return <ErrorState message={error} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Mountains</h1>
        <button onClick={openCreate} className="bg-emerald-700 hover:bg-emerald-800 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center gap-2 transition">
          <Plus size={18} /> Add Mountain
        </button>
      </div>

      <div className="relative">
        <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search mountains..."
          className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
      </div>

      {!filtered.length && <EmptyState message={search ? 'No mountains match your search' : 'No mountains yet'} />}

      {/* Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50" onClick={() => setShowForm(false)}>
          <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-md space-y-4" onClick={e => e.stopPropagation()}>
            <h2 className="text-lg font-bold">{editing ? 'Edit Mountain' : 'Add Mountain'}</h2>
            {['name', 'location', 'imageUrl'].map(f => (
              <div key={f}>
                <label className="text-sm font-medium text-gray-700 capitalize">{f}</label>
                <input value={(form as any)[f]} onChange={e => setForm({ ...form, [f]: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
              </div>
            ))}
            <div className="flex gap-4">
              <div>
                <label className="text-sm font-medium text-gray-700">Elevation (m)</label>
                <input type="number" value={form.elevation} onChange={e => setForm({ ...form, elevation: +e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
              </div>
              <div>
                <label className="text-sm font-medium text-gray-700">Rating</label>
                <input type="number" step="0.1" value={form.rating} onChange={e => setForm({ ...form, rating: +e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 mt-1" />
              </div>
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <button onClick={() => setShowForm(false)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg transition">Cancel</button>
              <button onClick={handleSave} className="px-4 py-2 text-sm bg-emerald-700 text-white rounded-lg hover:bg-emerald-800 transition">{editing ? 'Update' : 'Create'}</button>
            </div>
          </div>
        </div>
      )}

      <div className="grid gap-3">
        {filtered.map(m => (
          <div key={m.id} className="bg-white rounded-xl shadow-sm p-4 flex items-center gap-4">
            <div className="size-14 rounded-lg bg-gray-100 overflow-hidden shrink-0">
              {m.imageUrl ? <img src={m.imageUrl} alt="" className="size-full object-cover" /> : <div className="size-full flex items-center justify-center text-gray-400 text-xs">No img</div>}
            </div>
            <div className="flex-1 min-w-0">
              <p className="font-semibold text-gray-800 truncate">{m.name}</p>
              <p className="text-sm text-gray-500">{m.location} • {m.elevation}m • ⭐ {m.rating}</p>
            </div>
            <div className="flex gap-1">
              <button onClick={() => openEdit(m)} className="p-2 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition"><Pencil size={18} /></button>
              <button onClick={() => handleDelete(m.id)} className="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition"><Trash2 size={18} /></button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
