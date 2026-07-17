import { useEffect, useState } from 'react'
import {
  collection, getDocs,
  query, orderBy, Timestamp
} from 'firebase/firestore'
import { httpsCallable } from 'firebase/functions'
import { db, functions } from '../firebase'
import { Trash2, Eye, Search, MessageSquare } from 'lucide-react'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import ErrorState from '../components/ErrorState'

interface Post {
  id: string
  authorId: string
  authorName: string
  authorPhotoUrl?: string
  content: string
  mediaUrl?: string
  likesCount: number
  commentsCount: number
  timestamp: Timestamp
  hashtags?: string[]
}

export default function PostsPage() {
  const [posts, setPosts] = useState<Post[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [search, setSearch] = useState('')
  const [showDelete, setShowDelete] = useState<string | null>(null)

  const load = async () => {
    setLoading(true)
    setError(null)
    try {
      const q = query(collection(db, 'posts'), orderBy('timestamp', 'desc'))
      const snap = await getDocs(q)
      setPosts(snap.docs.map(d => ({ id: d.id, ...d.data() } as Post)))
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const doDelete = async (id: string) => {
    try {
      const fn = httpsCallable(functions, 'deletePost')
      await fn({ id })
      setPosts(p => p.filter(x => x.id !== id))
      setShowDelete(null)
    } catch (e: any) {
      alert('Gagal menghapus: ' + (e.message || 'Unknown error'))
    }
  }

  const filtered = posts.filter(p =>
    p.authorName?.toLowerCase().includes(search.toLowerCase()) ||
    p.content?.toLowerCase().includes(search.toLowerCase())
  )

  if (loading) return <LoadingState text="Memuat postingan..." />
  if (error) return <ErrorState message={error} />

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Posts</h1>
        <span className="text-sm text-gray-400">{posts.length} total</span>
      </div>

      {/* Search */}
      <div className="relative mb-4 max-w-sm">
        <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          type="text" placeholder="Cari postingan..."
          value={search} onChange={e => setSearch(e.target.value)}
          className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
        />
      </div>

      {filtered.length === 0 ? (
        <EmptyState message={search ? 'Postingan tidak ditemukan' : 'Belum ada postingan'} />
      ) : (
        <div className="space-y-3">
          {filtered.map(post => (
            <div key={post.id} className="bg-white rounded-lg border border-gray-200 p-4">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3 flex-1 min-w-0">
                  <img
                    src={post.authorPhotoUrl || 'https://ui-avatars.com/api/?name=' + post.authorName}
                    alt="" className="w-10 h-10 rounded-full bg-gray-100 object-cover flex-shrink-0"
                  />
                  <div className="min-w-0 flex-1">
                    <p className="font-semibold text-gray-800 text-sm">{post.authorName}</p>
                    <p className="text-xs text-gray-400">
                      {post.timestamp?.toDate?.()?.toLocaleDateString('id-ID', {
                        day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
                      })}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2 flex-shrink-0 ml-2">
                  <span className="flex items-center gap-1 text-xs text-gray-400">
                    <Eye size={14} /> {post.likesCount}
                  </span>
                  <span className="flex items-center gap-1 text-xs text-gray-400">
                    <MessageSquare size={14} /> {post.commentsCount}
                  </span>
                  <button
                    onClick={() => setShowDelete(post.id)}
                    className="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>

              {post.content && (
                <p className="mt-3 text-sm text-gray-600 line-clamp-3">{post.content}</p>
              )}

              {post.mediaUrl && (
                <img
                  src={post.mediaUrl} alt=""
                  className="mt-3 rounded-lg w-full max-h-48 object-cover"
                />
              )}

              {post.hashtags && post.hashtags.length > 0 && (
                <div className="mt-2 flex flex-wrap gap-1">
                  {post.hashtags.map(t => (
                    <span key={t} className="text-xs text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full">
                      #{t}
                    </span>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Delete confirmation modal */}
      {showDelete && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 max-w-sm w-full mx-4 shadow-xl">
            <h3 className="text-lg font-bold text-gray-800">Hapus postingan?</h3>
            <p className="text-sm text-gray-500 mt-2">Postingan akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.</p>
            <div className="flex gap-3 mt-6 justify-end">
              <button onClick={() => setShowDelete(null)} className="px-4 py-2 text-sm rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50">
                Batal
              </button>
              <button onClick={() => doDelete(showDelete)} className="px-4 py-2 text-sm rounded-lg bg-red-500 text-white hover:bg-red-600">
                Hapus
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
