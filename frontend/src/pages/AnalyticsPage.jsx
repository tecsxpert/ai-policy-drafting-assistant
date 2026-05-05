import { useEffect, useState } from 'react'
import { getStats } from '../services/policyService'
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, Tooltip, ResponsiveContainer, Legend
} from 'recharts'
import { Navbar, Spinner } from '../components/index'

const PIE_COLORS = ['#6366f1', '#10b981', '#f59e0b', '#f43f5e']

const ChartTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null
  return (
    <div className="bg-slate-800 border border-slate-600 rounded-xl px-3 py-2 text-xs shadow-lg">
      {label && <p className="text-slate-300 font-medium mb-1">{label}</p>}
      {payload.map((p, i) => (
        <p key={i} style={{ color: p.color ?? '#818cf8' }}>{p.name ?? 'Count'}: {p.value}</p>
      ))}
    </div>
  )
}

export default function AnalyticsPage() {
  const [stats, setStats] = useState(null)

  useEffect(() => { getStats().then(r => setStats(r.data)) }, [])

  if (!stats) return <div className="min-h-screen bg-slate-950"><Navbar /><Spinner /></div>

  return (
    <div className="min-h-screen bg-slate-950">
      <Navbar />
      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-white">Analytics</h1>
          <p className="text-slate-400 text-sm mt-0.5">Trends and distribution of your policy portfolio</p>
        </div>

        {/* Line chart full width */}
        <div className="bg-slate-900 border border-slate-700 rounded-2xl p-6 mb-4">
          <h2 className="text-sm font-semibold text-white mb-1">Policies created over time</h2>
          <p className="text-slate-500 text-xs mb-5">Last 6 months</p>
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={stats.byMonth}>
              <XAxis dataKey="month" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip content={<ChartTooltip />} />
              <Line type="monotone" dataKey="count" stroke="#6366f1" strokeWidth={2.5}
                dot={{ fill: '#6366f1', strokeWidth: 0, r: 4 }}
                activeDot={{ r: 6, fill: '#818cf8' }} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Two charts side by side */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          {/* Bar chart by category */}
          <div className="bg-slate-900 border border-slate-700 rounded-2xl p-6">
            <h2 className="text-sm font-semibold text-white mb-1">Policies by category</h2>
            <p className="text-slate-500 text-xs mb-5">Distribution across all categories</p>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={stats.byCategory} barSize={28} layout="vertical">
                <XAxis type="number" tick={{ fill: '#64748b', fontSize: 10 }} axisLine={false} tickLine={false} />
                <YAxis dataKey="category" type="category" tick={{ fill: '#94a3b8', fontSize: 10 }} axisLine={false} tickLine={false} width={90} />
                <Tooltip content={<ChartTooltip />} cursor={{ fill: 'rgba(99,102,241,0.08)' }} />
                <Bar dataKey="count" radius={[0, 6, 6, 0]} fill="#6366f1" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Pie chart by status */}
          <div className="bg-slate-900 border border-slate-700 rounded-2xl p-6">
            <h2 className="text-sm font-semibold text-white mb-1">Policies by status</h2>
            <p className="text-slate-500 text-xs mb-5">Current lifecycle distribution</p>
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie
                  data={stats.byStatus}
                  dataKey="count"
                  nameKey="status"
                  cx="50%" cy="50%"
                  innerRadius={55}
                  outerRadius={80}
                  paddingAngle={3}
                >
                  {stats.byStatus.map((_, i) => (
                    <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip content={<ChartTooltip />} />
                <Legend
                  formatter={(value) => <span style={{ color: '#94a3b8', fontSize: 11 }}>{value?.replace('_', ' ')}</span>}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Summary stats row */}
        <div className="grid grid-cols-3 gap-4 mt-4">
          {[
            { label: 'Most common category', value: stats.byCategory.sort((a,b) => b.count - a.count)[0]?.category ?? '-' },
            { label: 'Active rate', value: `${stats.total > 0 ? Math.round((stats.active / stats.total) * 100) : 0}%` },
            { label: 'Pending review', value: stats.underReview },
          ].map(s => (
            <div key={s.label} className="bg-slate-900 border border-slate-700 rounded-2xl p-4 text-center">
              <p className="text-xl font-bold text-indigo-400">{s.value}</p>
              <p className="text-slate-500 text-xs mt-1">{s.label}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
