import { Navigate } from 'react-router-dom'
import { useAuth } from '../lib/useAuth'
import LoadingState from './LoadingState'

export default function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth()
  if (loading) return <LoadingState text="Checking authentication..." />
  if (!user) return <Navigate to="/login" replace />
  return <>{children}</>
}
