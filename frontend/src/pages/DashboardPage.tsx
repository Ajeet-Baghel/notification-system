import { useAuth } from '../auth/AuthContext'
import { useNavigate } from 'react-router-dom'
import { BellRing, LogOut, MailCheck, MessageSquareText } from 'lucide-react'

export default function DashboardPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const onLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-slate-950">
      <header className="border-b border-slate-800">
        <div className="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <BellRing className="h-6 w-6 text-indigo-400" />
            <span className="font-semibold text-white">Notification System</span>
          </div>
          <button
            onClick={onLogout}
            className="flex items-center gap-2 text-sm text-slate-300 hover:text-white transition-colors"
          >
            <LogOut className="h-4 w-4" />
            Sign out
          </button>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 py-12">
        <h1 className="text-3xl font-bold text-white">Hi, {user?.name}</h1>
        <p className="text-slate-400 mt-2">You're signed in as {user?.email}</p>

        <div className="grid sm:grid-cols-2 gap-4 mt-10">
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6">
            <MailCheck className="h-8 w-8 text-indigo-400 mb-3" />
            <h2 className="text-white font-semibold">Email notifications</h2>
            <p className="text-slate-400 text-sm mt-1">
              A welcome email was sent when you registered, and a security alert goes out on every login.
            </p>
          </div>
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6">
            <MessageSquareText className="h-8 w-8 text-indigo-400 mb-3" />
            <h2 className="text-white font-semibold">WhatsApp notifications</h2>
            <p className="text-slate-400 text-sm mt-1">
              The same events are delivered to your WhatsApp number via Twilio.
            </p>
          </div>
        </div>
      </main>
    </div>
  )
}
