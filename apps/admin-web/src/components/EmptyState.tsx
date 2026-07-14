import { Inbox } from 'lucide-react'

export default function EmptyState({ message = 'No data yet' }: { message?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-3 text-gray-400">
      <Inbox size={48} />
      <p className="text-sm">{message}</p>
    </div>
  )
}
