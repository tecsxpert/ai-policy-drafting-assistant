import { useEffect, useState } from 'react'
import { getStats } from '../services/policyService'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts'
import { Navbar, Spinner } from '../components/index'
import { Link } from 'react-router-dom'

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload?.length) return (
    <div className="bg-slate-800 border border-slate-600 rounded-xl px-3 py-2 text-xs">
      <p className="text-slate-300 font-medium">{label}</p>
      <p className="text-indigo-400">{payload[0].value} policies</p>
    </div>
  )
  return null
}

export default function Dashboard() {
  const [stats, setStats] = useState(null)

  useEffect(() => { getStats().then(r => setStats(r.data)) }, [])

  if (!stats) return <div className="min-h-screen bg-slate-950"><Navbar /><Spinner /></div>

  const kpis = [
    { label: 'Total Policies', value: stats.total, color: 'text-white', icon: (
      <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>
    )},
    { label: 'Active', value: stats.active, color: 'text-emerald-400', icon: (
      <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
    )},
    { label: 'Under Review', value: stats.underReview, color: 'text-amber-400', icon: (
      <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
    )},
    { label: 'Drafts', value: stats.draftedThisMonth, color: 'text-slate-400', icon: (
      <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
    )},
  ]

  return (
    <div className="min-h-screen bg-slate-950">
      <Navbar />
      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-white">Dashboard</h1>
            <p className="text-slate-400 text-sm mt-0.5">Overview of your policy portfolio</p>
          </div>
          <Link to="/create"
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-sm font-semibold transition-colors">
            + New Policy
          </Link>
        </div>

        {/* KPI Cards */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          {kpis.map(k => (
            <div key={k.label} className="bg-slate-900 border border-slate-700 rounded-2xl p-5">
              <div className={`${k.color} mb-3`}>{k.icon}</div>
              <p className={`text-3xl font-bold ${k.color}`}>{k.value}</p>
              <p className="text-slate-400 text-sm mt-1">{k.label}</p>
            </div>
          ))}
        </div>

        {/* Charts row */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          {/* Bar chart */}
          <div className="lg:col-span-2 bg-slate-900 border border-slate-700 rounded-2xl p-5">
            <h2 className="text-sm font-semibold text-white mb-5">Policies by category</h2>
            <ResponsiveContainer width="100%" height={230}>
              <BarChart data={stats.byCategory} barSize={32}>
                <XAxis dataKey="category" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
                <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(99,102,241,0.08)' }} />
                <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                  {stats.byCategory.map((_, i) => (
                    <Cell key={i} fill={i % 2 === 0 ? '#6366f1' : '#818cf8'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Status breakdown */}
          <div className="bg-slate-900 border border-slate-700 rounded-2xl p-5">
            <h2 className="text-sm font-semibold text-white mb-5">Status breakdown</h2>
            <div className="space-y-3">
              {stats.byStatus.map(s => {
                const pct = stats.total > 0 ? Math.round((s.count / stats.total) * 100) : 0
                const colors = { ACTIVE: 'bg-emerald-500', DRAFT: 'bg-slate-500', UNDER_REVIEW: 'bg-amber-500', ARCHIVED: 'bg-rose-500' }
                return (
                  <div key={s.status}>
                    <div className="flex justify-between text-xs mb-1.5">
                      <span className="text-slate-400">{s.status.replace('_', ' ')}</span>
                      <span className="text-slate-300 font-medium">{s.count}</span>
                    </div>
                    <div className="h-1.5 bg-slate-800 rounded-full overflow-hidden">
                      <div className={`h-full ${colors[s.status] ?? 'bg-indigo-500'} rounded-full transition-all`} style={{ width: `${pct}%` }} />
                    </div>
                  </div>
                )
              })}
            </div>
            <div className="mt-6 pt-4 border-t border-slate-700">
              <p className="text-xs text-slate-500">Compliance rate</p>
              <p className="text-2xl font-bold text-emerald-400 mt-1">
                {stats.total > 0 ? Math.round((stats.active / stats.total) * 100) : 0}%
              </p>
              <p className="text-xs text-slate-500 mt-0.5">policies are active</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
