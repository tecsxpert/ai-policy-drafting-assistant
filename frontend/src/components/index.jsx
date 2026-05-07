import { Navigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'
import { Component } from 'react'

// ── ProtectedRoute ────────────────────────────────────────────────────────────
export function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? children : <Navigate to="/login" />
}

// ── ErrorBoundary ─────────────────────────────────────────────────────────────
export class ErrorBoundary extends Component {
  state = { hasError: false }
  static getDerivedStateFromError() { return { hasError: true } }
  render() {
    if (this.state.hasError)
      return (
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <p className="text-red-500 text-lg font-medium">Something went wrong.</p>
            <button onClick={() => window.location.reload()}
              className="mt-3 px-4 py-2 bg-slate-800 text-white rounded-lg text-sm">
              Refresh page
            </button>
          </div>
        </div>
      )
    return this.props.children
  }
}

// ── StatusBadge ───────────────────────────────────────────────────────────────
const badgeStyles = {
  ACTIVE:       'bg-emerald-100 text-emerald-800 border border-emerald-200 whitespace-nowrap',
  DRAFT:        'bg-slate-100 text-slate-600 border border-slate-200 whitespace-nowrap',
  UNDER_REVIEW: 'bg-amber-100 text-amber-800 border border-amber-200 whitespace-nowrap',
  ARCHIVED:     'bg-rose-100 text-rose-700 border border-rose-200 whitespace-nowrap',
}
export function StatusBadge({ status }) {
  return (
    <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold tracking-wide ${badgeStyles[status] ?? 'bg-slate-100 text-slate-600'}`}>
      {status?.replace('_', ' ')}
    </span>
  )
}

// ── Spinner ───────────────────────────────────────────────────────────────────
export function Spinner({ size = 'md' }) {
  const s = size === 'sm' ? 'w-4 h-4 border-2' : 'w-8 h-8 border-[3px]'
  return (
    <div className="flex justify-center items-center py-16">
      <div className={`${s} border-indigo-500 border-t-transparent rounded-full animate-spin`} />
    </div>
  )
}

// ── EmptyState ────────────────────────────────────────────────────────────────
export function EmptyState({ message = 'Nothing here yet', sub = '' }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-slate-400">
      <div className="w-16 h-16 rounded-2xl bg-slate-100 flex items-center justify-center mb-4">
        <svg width="28" height="28" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      </div>
      <p className="text-base font-medium text-slate-600">{message}</p>
      {sub && <p className="text-sm mt-1">{sub}</p>}
    </div>
  )
}

// ── Navbar ────────────────────────────────────────────────────────────────────
import { Link, useLocation, useNavigate } from 'react-router-dom'

export function Navbar() {
  const { logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const links = [
    { to: '/', label: 'Policies' },
    { to: '/dashboard', label: 'Dashboard' },
    { to: '/analytics', label: 'Analytics' },
  ]

  const handleLogout = () => { logout(); navigate('/login') }

  return (
    <nav className="bg-slate-900 text-white px-6 py-0 flex items-center justify-between sticky top-0 z-50 border-b border-slate-700">
      <div className="flex items-center gap-1">
        <div className="w-7 h-7 bg-indigo-500 rounded-lg flex items-center justify-center mr-3">
          <svg width="14" height="14" fill="none" stroke="white" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <span className="font-bold text-sm tracking-tight mr-6 text-white">PolicyAI</span>
        {links.map(l => (
          <Link key={l.to} to={l.to}
            className={`px-4 py-4 text-sm font-medium border-b-2 transition-colors ${
              location.pathname === l.to
                ? 'border-indigo-400 text-white'
                : 'border-transparent text-slate-400 hover:text-white hover:border-slate-500'
            }`}>
            {l.label}
          </Link>
        ))}
      </div>
      <button onClick={handleLogout}
        className="text-xs text-slate-400 hover:text-white transition-colors px-3 py-1.5 rounded-lg hover:bg-slate-700">
        Sign out
      </button>
    </nav>
  )
}
