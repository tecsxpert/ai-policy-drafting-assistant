import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getPolicyById, deletePolicy, analysePolicy } from '../services/policyService'
import { Navbar, StatusBadge, Spinner } from '../components/index'

export default function DetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [policy, setPolicy]     = useState(null)
  const [aiResult, setAiResult] = useState(null)
  const [aiLoading, setAiLoading] = useState(false)
  const [aiError, setAiError]   = useState(null)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => { getPolicyById(id).then(r => setPolicy(r.data)) }, [id])

  const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this policy? This cannot be undone.')) return
    setDeleting(true)
    await deletePolicy(id)
    navigate('/')
  }

  const handleAI = async () => {
    setAiLoading(true); setAiError(null)
    try {
      const res = await analysePolicy(id)
      setAiResult(res.data)
    } catch { setAiError('Analysis failed. Please try again.') }
    finally { setAiLoading(false) }
  }

  if (!policy) return <div className="min-h-screen bg-slate-950"><Navbar /><Spinner /></div>

  return (
    <div className="min-h-screen bg-slate-950">
      <Navbar />
      <div className="max-w-4xl mx-auto px-6 py-8">

        {/* Back */}
        <button onClick={() => navigate('/')} className="flex items-center gap-1.5 text-slate-400 hover:text-white text-sm mb-6 transition-colors">
          <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
          Back to list
        </button>

        {/* Title row */}
        <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4 mb-6">
          <div>
            <h1 className="text-2xl font-bold text-white leading-tight">{policy.title}</h1>
            <div className="flex flex-wrap items-center gap-2 mt-3">
              <StatusBadge status={policy.status} />
              <span className="text-slate-400 text-sm bg-slate-800 px-3 py-1 rounded-lg">{policy.category}</span>
            </div>
          </div>
          <div className="flex gap-2 shrink-0">
            <button onClick={() => navigate(`/edit/${id}`)}
              className="flex items-center gap-1.5 px-4 py-2 bg-slate-800 hover:bg-slate-700 border border-slate-600 text-slate-200 rounded-xl text-sm font-medium transition-colors min-h-[44px]">
              <svg width="13" height="13" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
              Edit
            </button>
            <button onClick={handleDelete} disabled={deleting}
              className="flex items-center gap-1.5 px-4 py-2 bg-red-500/10 hover:bg-red-500/20 border border-red-500/30 text-red-400 rounded-xl text-sm font-medium transition-colors min-h-[44px] disabled:opacity-50">
              <svg width="13" height="13" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              {deleting ? 'Deleting...' : 'Delete'}
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          {/* Left: main content */}
          <div className="lg:col-span-2 space-y-4">
            {/* Description */}
            <div className="bg-slate-900 border border-slate-700 rounded-2xl p-5">
              <h2 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">Description</h2>
              <p className="text-slate-300 leading-relaxed text-sm">
                {policy.description || <span className="text-slate-500 italic">No description provided.</span>}
              </p>
            </div>

            {/* Metadata */}
            <div className="bg-slate-900 border border-slate-700 rounded-2xl p-5">
              <h2 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">Details</h2>
              <div className="grid grid-cols-2 gap-4 text-sm">
                {[
                  { label: 'Policy ID', value: `#${policy.id}` },
                  { label: 'Category', value: policy.category },
                  { label: 'Created', value: new Date(policy.createdAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' }) },
                  { label: 'Last Updated', value: new Date(policy.updatedAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' }) },
                ].map(item => (
                  <div key={item.label}>
                    <p className="text-slate-500 text-xs mb-0.5">{item.label}</p>
                    <p className="text-slate-200 font-medium">{item.value}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Right: AI panel */}
          <div className="lg:col-span-1">
            <div className="bg-gradient-to-b from-indigo-950/80 to-slate-900 border border-indigo-500/30 rounded-2xl p-5 h-full">
              <div className="flex items-center gap-2 mb-4">
                <div className="w-7 h-7 bg-indigo-600 rounded-lg flex items-center justify-center">
                  <svg width="13" height="13" fill="none" stroke="white" strokeWidth="2" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                  </svg>
                </div>
                <h2 className="font-semibold text-white text-sm">AI Analysis</h2>
              </div>

              {!aiResult && !aiLoading && !aiError && (
                <div>
                  <p className="text-slate-400 text-xs leading-relaxed mb-4">
                    Get AI-powered insights, risk assessment, and recommendations for this policy.
                  </p>
                  <button onClick={handleAI}
                    className="w-full bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold py-2.5 rounded-xl transition-colors min-h-[44px]">
                    Analyse with AI
                  </button>
                </div>
              )}

              {aiLoading && (
                <div className="space-y-3">
                  <p className="text-indigo-300 text-xs animate-pulse">Analysing policy...</p>
                  {[70, 50, 60].map((w, i) => (
                    <div key={i} className={`h-2.5 bg-indigo-900/60 rounded animate-pulse`} style={{ width: `${w}%` }} />
                  ))}
                </div>
              )}

              {aiError && (
                <div>
                  <p className="text-red-400 text-xs mb-3">{aiError}</p>
                  <button onClick={handleAI} className="text-indigo-400 text-xs underline">Try again</button>
                </div>
              )}

              {aiResult && (
                <div className="space-y-4">
                  <div>
                    <p className="text-xs font-semibold text-indigo-300 uppercase tracking-wider mb-1.5">Summary</p>
                    <p className="text-slate-300 text-xs leading-relaxed">{aiResult.summary}</p>
                  </div>
                  {aiResult.recommendations?.length > 0 && (
                    <div>
                      <p className="text-xs font-semibold text-indigo-300 uppercase tracking-wider mb-2">Recommendations</p>
                      <div className="space-y-2">
                        {aiResult.recommendations.map((r, i) => (
                          <div key={i} className="flex gap-2">
                            <span className="text-indigo-400 mt-0.5 shrink-0">•</span>
                            <p className="text-slate-300 text-xs leading-relaxed">{r.description ?? r}</p>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                  {aiResult.confidence && (
                    <div className="pt-2 border-t border-indigo-900/50">
                      <p className="text-xs text-slate-500">Confidence: <span className="text-indigo-300">{Math.round(aiResult.confidence * 100)}%</span></p>
                    </div>
                  )}
                  <button onClick={handleAI}
                    className="w-full bg-slate-800 hover:bg-slate-700 border border-slate-600 text-slate-300 text-xs py-2 rounded-xl transition-colors">
                    Re-analyse
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
