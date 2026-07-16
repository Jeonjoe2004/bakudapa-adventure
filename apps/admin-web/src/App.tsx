import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './lib/useAuth'
import ProtectedRoute from './components/ProtectedRoute'
import Sidebar from './components/Sidebar'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import MountainsPage from './pages/MountainsPage'
import TrailsPage from './pages/TrailsPage'
import UsersPage from './pages/UsersPage'
import PostsPage from './pages/PostsPage'
import PoiPage from './pages/PoiPage'
import PendingTrailsPage from './pages/PendingTrailsPage'
import ReviewsPage from './pages/ReviewsPage'
import ArticlesPage from './pages/ArticlesPage'

function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 p-6 lg:ml-0 overflow-auto">
        {children}
      </main>
    </div>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<ProtectedRoute><AdminLayout><DashboardPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/mountains" element={<ProtectedRoute><AdminLayout><MountainsPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/trails" element={<ProtectedRoute><AdminLayout><TrailsPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/users" element={<ProtectedRoute><AdminLayout><UsersPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/posts" element={<ProtectedRoute><AdminLayout><PostsPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/poi" element={<ProtectedRoute><AdminLayout><PoiPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/pending-trails" element={<ProtectedRoute><AdminLayout><PendingTrailsPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/reviews" element={<ProtectedRoute><AdminLayout><ReviewsPage /></AdminLayout></ProtectedRoute>} />
          <Route path="/articles" element={<ProtectedRoute><AdminLayout><ArticlesPage /></AdminLayout></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
