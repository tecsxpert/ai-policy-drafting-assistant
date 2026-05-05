import { useEffect, useState, useCallback } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getPolicies, searchPolicies, exportCSV } from '../services/policyService'
import { Navbar, StatusBadge, Spinner, EmptyState } from '../components/index'

function debounce(fn, ms) {
  let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn(...a), ms) }
}

export default function ListPage() {
  const [policies, setPolicies]     = useState([])
  const [loading, setLoading]       = useState(true)
  const [page, setPage]             = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotal]   = useState(0)
  const [search, setSearch]         = useState('')
  const [statusFilter, setStatus]   = useState('')
  const navigate = useNavigate()

  const load = useCallback((p = 0) => {
    setLoading(true)
    getPolicies(p).then(r => {
      setPolicies(r.data.content)
      setTotalPages(r.data.totalPages)
      setTotal(r.data.totalElements)
    }).finally(() => setLoading(false))
  }, [])

  useEffect(() => { load(page) }, [page])

  const handleSearch = useCallback(debounce((q) => {
    if (q) {
      setLoading(true)
      searchPolicies(q).then(r => {
        setPolicies(r.data.content ?? r.data)
        setTotalPages(1)
      }).finally(() => setLoading(false))
    } else { load(0) }
  }, 300), [])

  const handleExport = async () => {
    const res = await exportCSV()
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a')
    a.href = url; a.download = 'policies.csv'; a.click()
    window.URL.revokeObjectURL(url)
  }

  const filtered = statusFilter ? policies.filter(p => p.status === statusFilter) : policies

  return (
    <div className="min-h-screen bg-slate-950">
      <Navbar />
      <div className="max-w-7xl mx-auto px-6 py-8">

        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
          <div>
            <h1 className="text-2xl font-bold text-white">Policies</h1>
            <p className="text-slate-400 text-sm mt-0.5">{totalElements} total policies</p>
          </div>
          <div className="flex gap-2">
            <button onClick={handleExport}
              className="flex items-center gap-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 border border-slate-600 text-slate-200 rounded-xl text-sm font-medium transition-colors min-h-[44px]">
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              Export CSV
            </button>
            <Link to="/create"
              className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-sm font-semibold transition-colors min-h-[44px]">
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              New Policy
            </Link>
          </div>
        </div>

        {/* Search + filter bar */}
        <div className="flex flex-col sm:flex-row gap-3 mb-5">
          <div className="relative flex-1">
            <svg className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" width="15" height="15" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              className="w-full bg-slate-800 border border-slate-600 text-white rounded-xl pl-10 pr-4 py-2.5 text-sm outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-colors placeholder:text-slate-500"
              placeholder="Search by title, category, or description..."
              value={search}
              onChange={e => { setSearch(e.target.value); handleSearch(e.target.value) }}
            />
          </div>
          <select
            className="bg-slate-800 border border-slate-600 text-slate-200 rounded-xl px-4 py-2.5 text-sm outline-none focus:border-indigo-500 transition-colors"
            value={statusFilter}
            onChange={e => setStatus(e.target.value)}
          >
            <option value="">All statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="DRAFT">Draft</option>
            <option value="UNDER_REVIEW">Under Review</option>
            <option value="ARCHIVED">Archived</option>
          </select>
        </div>

        {/* Table */}
        {loading ? <Spinner /> : filtered.length === 0 ? (
          <EmptyState message="No policies found" sub="Try adjusting your search or filters" />
        ) : (
          <div className="bg-slate-900 border border-slate-700 rounded-2xl overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[640px]">
                <thead>
                  <tr className="border-b border-slate-700">
                    {['Title', 'Status', 'Category', 'Last updated', ''].map(h => (
                      <th key={h} className="text-left px-5 py-3.5 text-xs font-semibold text-slate-400 uppercase tracking-wider">
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {filtered.map(p => (
                    <tr key={p.id} className="hover:bg-slate-800/50 transition-colors group">
                      <td className="px-5 py-4">
                        <p className="font-medium text-white text-sm">{p.title}</p>
                        <p className="text-slate-500 text-xs mt-0.5 line-clamp-1">{p.description}</p>
                      </td>
                      <td className="px-5 py-4"><StatusBadge status={p.status} /></td>
                      <td className="px-5 py-4">
                        <span className="text-slate-300 text-sm bg-slate-800 px-2.5 py-1 rounded-lg">{p.category}</span>
                      </td>
                      <td className="px-5 py-4 text-slate-400 text-sm">
                        {new Date(p.updatedAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                      </td>
                      <td className="px-5 py-4">
                        <button
                          onClick={() => navigate(`/policy/${p.id}`)}
                          className="text-indigo-400 hover:text-indigo-300 text-sm font-medium opacity-0 group-hover:opacity-100 transition-opacity"
                        >
                          View →
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="flex items-center justify-between px-5 py-3.5 border-t border-slate-700">
              <span className="text-xs text-slate-500">
                Showing {Math.min(page * 8 + 1, totalElements)}–{Math.min((page + 1) * 8, totalElements)} of {totalElements}
              </span>
              <div className="flex gap-2">
                <button disabled={page === 0} onClick={() => setPage(p => p - 1)}
                  className="px-3 py-1.5 bg-slate-800 border border-slate-600 text-slate-300 rounded-lg text-xs disabled:opacity-30 hover:bg-slate-700 transition-colors">
                  ← Prev
                </button>
                <span className="px-3 py-1.5 text-xs text-slate-400">
                  {page + 1} / {totalPages || 1}
                </span>
                <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}
                  className="px-3 py-1.5 bg-slate-800 border border-slate-600 text-slate-300 rounded-lg text-xs disabled:opacity-30 hover:bg-slate-700 transition-colors">
                  Next →
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
