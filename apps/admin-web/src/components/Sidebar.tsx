import { NavLink } from 'react-router-dom'
import { LayoutDashboard, Mountain, Map, Users, LogOut, Menu, X } from 'lucide-react'
import { useState } from 'react'
import { useAuth } from '../lib/useAuth'

const links = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/mountains', label: 'Mountains', icon: Mountain },
  { to: '/trails', label: 'Trails', icon: Map },
  { to: '/users', label: 'Users', icon: Users },
]

export default function Sidebar() {
  const { logout } = useAuth()
  const [open, setOpen] = useState(false)

  return (
    <>
      {/* Mobile toggle */}
      <button onClick={() => setOpen(!open)} className="lg:hidden fixed top-4 left-4 z-50 p-2 bg-white rounded-lg shadow-md">
        {open ? <X size={20} /> : <Menu size={20} />}
      </button>

      <aside className={`fixed inset-y-0 left-0 z-40 w-64 bg-white border-r border-gray-200 transform transition-transform lg:translate-x-0 lg:static flex flex-col ${open ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="p-5 border-b border-gray-100">
          <h1 className="text-lg font-bold text-emerald-800">Bakudapa Admin</h1>
          <p className="text-xs text-gray-400">Adventure Dashboard</p>
        </div>

        <nav className="flex-1 p-3 space-y-1">
          {links.map(l => (
            <NavLink key={l.to} to={l.to} end={l.to === '/'} onClick={() => setOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${isActive ? 'bg-emerald-50 text-emerald-700' : 'text-gray-600 hover:bg-gray-50'}`
              }>
              <l.icon size={20} /> {l.label}
            </NavLink>
          ))}
        </nav>

        <div className="p-3 border-t border-gray-100">
          <button onClick={logout} className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-600 hover:bg-red-50 hover:text-red-600 transition w-full">
            <LogOut size={20} /> Logout
          </button>
        </div>
      </aside>

      {open && <div className="fixed inset-0 bg-black/30 z-30 lg:hidden" onClick={() => setOpen(false)} />}
    </>
  )
}
