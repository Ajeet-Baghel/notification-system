import type { ReactNode } from 'react'
import { BellRing } from 'lucide-react'

export default function AuthLayout({ title, subtitle, children }: {
  title: string
  subtitle: string
  children: ReactNode
}) {
  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="flex items-center justify-center gap-2 mb-8">
          <BellRing className="h-8 w-8 text-indigo-400" />
          <span className="text-xl font-semibold text-white">Notification System</span>
        </div>
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-8 shadow-xl">
          <h1 className="text-2xl font-bold text-white">{title}</h1>
          <p className="text-slate-400 text-sm mt-1 mb-6">{subtitle}</p>
          {children}
        </div>
      </div>
    </div>
  )
}
