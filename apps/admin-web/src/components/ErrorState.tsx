import { AlertTriangle } from 'lucide-react'

export default function ErrorState({ message = 'Something went wrong' }: { message?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-3 text-red-500">
      <AlertTriangle size={48} />
      <p className="text-sm font-medium">{message}</p>
    </div>
  )
}
