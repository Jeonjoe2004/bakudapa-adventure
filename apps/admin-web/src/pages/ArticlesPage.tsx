import { useEffect, useState } from 'react'
import { collection, getDocs, addDoc, updateDoc, deleteDoc, doc } from 'firebase/firestore'
import { db } from '../firebase'
import LoadingState from '../components/LoadingState'
import ErrorState from '../components/ErrorState'
import { Plus, Edit2, Trash2, Save, X } from 'lucide-react'

interface Article { id: string; title: string; content: string; author: string; createdAt: number; published: boolean }

export default function ArticlesPage() {
  const [articles, setArticles] = useState<Article[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [editing, setEditing] = useState<string | null>(null)
  const [form, setForm] = useState({ title: '', content: '', author: '' })

  useEffect(() => { fetchArticles() }, [])

  const fetchArticles = async () => {
    setLoading(true)
    try {
      const snap = await getDocs(collection(db, 'articles'))
      setArticles(snap.docs.map(d => ({ id: d.id, ...d.data() } as Article)))
    } catch (err: any) { setError(err.message) }
    finally { setLoading(false) }
  }

  const resetForm = () => { setForm({ title: '', content: '', author: '' }); setEditing(null) }

  const handleSave = async () => {
    if (!form.title.trim()) return
    try {
      if (editing) {
        await updateDoc(doc(db, 'articles', editing), { ...form, updatedAt: Date.now() })
      } else {
        await addDoc(collection(db, 'articles'), { ...form, createdAt: Date.now(), published: false })
      }
      resetForm(); await fetchArticles()
    } catch (err: any) { setError(err.message) }
  }

  const handleDelete = async (id: string) => {
    if (!confirm('Hapus artikel ini?')) return
    try { await deleteDoc(doc(db, 'articles', id)); await fetchArticles() }
    catch (err: any) { setError(err.message) }
  }

  if (loading) return <LoadingState />
  if (error) return <ErrorState message={error} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Articles</h1>
        {!editing && <button onClick={() => setEditing('new')} className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-800 text-white px-4 py-2 rounded-lg text-sm font-medium transition"><Plus size={18} /> New Article</button>}
      </div>

      {(editing === 'new' || (editing && articles.find(a => a.id === editing))) &&
        <div className="bg-white rounded-xl shadow-sm p-4 space-y-3">
          <input value={form.title} onChange={e => setForm({ ...form, title: e.target.value })} placeholder="Title" className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm" />
          <textarea value={form.content} onChange={e => setForm({ ...form, content: e.target.value })} rows={5} placeholder="Content (Markdown supported)" className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm" />
          <input value={form.author} onChange={e => setForm({ ...form, author: e.target.value })} placeholder="Author" className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm" />
          <div className="flex gap-2">
            <button onClick={handleSave} className="flex items-center gap-1 bg-emerald-700 hover:bg-emerald-800 text-white px-4 py-2 rounded-lg text-sm font-medium transition"><Save size={16} /> Save</button>
            <button onClick={resetForm} className="flex items-center gap-1 bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-lg text-sm font-medium transition"><X size={16} /> Cancel</button>
          </div>
        </div>}

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        {!articles.length ? <div className="p-6 text-center text-gray-400 text-sm">No articles yet.</div> :
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-600">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Title</th>
              <th className="text-left px-4 py-3 font-medium">Author</th>
              <th className="text-left px-4 py-3 font-medium">Status</th>
              <th className="text-right px-4 py-3 font-medium">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {articles.map(a => (
              <tr key={a.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium text-gray-800">{a.title}</td>
                <td className="px-4 py-3 text-gray-500">{a.author || '-'}</td>
                <td className="px-4 py-3">
                  <span className={`inline-flex px-2 py-0.5 rounded-full text-xs font-medium ${a.published ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'}`}>
                    {a.published ? 'Published' : 'Draft'}
                  </span>
                </td>
                <td className="px-4 py-3 text-right">
                  <button onClick={() => { const found = articles.find(x => x.id === a.id); if (found) { setForm({ title: found.title, content: found.content, author: found.author }); setEditing(a.id) } }} className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition"><Edit2 size={16} /></button>
                  <button onClick={() => handleDelete(a.id)} className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition"><Trash2 size={16} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>}
      </div>
    </div>
  )
}
