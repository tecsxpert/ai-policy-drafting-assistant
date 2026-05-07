import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { createPolicy, updatePolicy, getPolicyById } from '../services/policyService'
import { Navbar } from '../components/index'

const CATEGORIES = ['Cybersecurity', 'Data Governance', 'HR', 'Compliance', 'Risk Management', 'Technology', 'Finance', 'Operations', 'Legal']
const STATUSES   = ['DRAFT', 'ACTIVE', 'UNDER_REVIEW', 'ARCHIVED']

export default function FormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = !!id

  const [form, setForm]   = useState({ title: '', description: '', status: 'DRAFT', category: '' })
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)
  const [loading, setLoading] = useState(isEdit)

  useEffect(() => {
    if (isEdit) {
      getPolicyById(id).then(r => { setForm(r.data); setLoading(false) })
    }
  }, [id])

  const set = (k, v) => { setForm(f => ({ ...f, [k]: v })); setErrors(e => ({ ...e, [k]: '' })) }

  const validate = () => {
    const e = {}
    if (!form.title.trim()) e.title = 'Title is required'
    if (form.title.trim().length > 120) e.title = 'Title must be under 120 characters'
    if (!form.category) e.category = 'Category is required'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const handleSubmit = async () => {
    if (!validate()) return
    setSaving(true)
    try {
      isEdit ? await updatePolicy(id, form) : await createPolicy(form)
      navigate('/')
    } catch (err) {
      alert('Save failed: ' + (err.response?.data?.message ?? 'Unknown error'))
    } finally { setSaving(false) }
  }

  if (loading) return (
    <div className="min-h-screen bg-slate-950">
      <Navbar />
      <div className="flex justify-center pt-20">
        <div className="w-8 h-8 border-[3px] border-indigo-500 border-t-transparent rounded-full animate-spin" />
      </div>
    </div>
  )

  return (
    <div className="min-h-screen bg-slate-950">
      <Navbar />
      <div className="max-w-2xl mx-auto px-6 py-8">
        {/* Back */}
        <button onClick={() => navigate('/')} className="flex items-center gap-1.5 text-slate-400 hover:text-white text-sm mb-6 transition-colors">
          <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
          Back to list
        </button>

        <div className="bg-slate-900 border border-slate-700 rounded-2xl p-6">
          <h1 className="text-xl font-bold text-white mb-6">
            {isEdit ? 'Edit Policy' : 'Create New Policy'}
          </h1>

          {/* Title */}
          <div className="mb-5">
            <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1.5">
              Title <span className="text-red-400">*</span>
            </label>
            <input
              className={`w-full bg-slate-800 border text-white rounded-xl px-4 py-2.5 text-sm outline-none focus:ring-1 transition-colors placeholder:text-slate-500 ${errors.title ? 'border-red-500 focus:border-red-500 focus:ring-red-500' : 'border-slate-600 focus:border-indigo-500 focus:ring-indigo-500'}`}
              placeholder="e.g. Data Privacy & Protection Policy"
              value={form.title}
              onChange={e => set('title', e.target.value)}
            />
            {errors.title && <p className="text-red-400 text-xs mt-1.5">{errors.title}</p>}
          </div>

          {/* Description */}
          <div className="mb-5">
            <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1.5">Description</label>
            <textarea
              className="w-full bg-slate-800 border border-slate-600 text-white rounded-xl px-4 py-2.5 text-sm outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-colors placeholder:text-slate-500 resize-none"
              rows={5}
              placeholder="Describe the purpose, scope, and key requirements of this policy..."
              value={form.description}
              onChange={e => set('description', e.target.value)}
            />
          </div>

          {/* Category + Status row */}
          <div className="grid grid-cols-2 gap-4 mb-6">
            <div>
              <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1.5">
                Category <span className="text-red-400">*</span>
              </label>
              <select
                className={`w-full bg-slate-800 border text-white rounded-xl px-4 py-2.5 text-sm outline-none focus:ring-1 transition-colors ${errors.category ? 'border-red-500 focus:ring-red-500' : 'border-slate-600 focus:border-indigo-500 focus:ring-indigo-500'}`}
                value={form.category}
                onChange={e => set('category', e.target.value)}
              >
                <option value="">Select category</option>
                {CATEGORIES.map(c => <option key={c}>{c}</option>)}
              </select>
              {errors.category && <p className="text-red-400 text-xs mt-1.5">{errors.category}</p>}
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1.5">Status</label>
              <select
                className="w-full bg-slate-800 border border-slate-600 text-white rounded-xl px-4 py-2.5 text-sm outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-colors"
                value={form.status}
                onChange={e => set('status', e.target.value)}
              >
                {STATUSES.map(s => <option key={s}>{s}</option>)}
              </select>
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-3 pt-2 border-t border-slate-700">
            <button onClick={handleSubmit} disabled={saving}
              className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-semibold px-6 py-2.5 rounded-xl text-sm transition-colors min-h-[44px]">
              {saving ? (
                <><span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"/>{isEdit ? 'Saving...' : 'Creating...'}</>
              ) : (isEdit ? 'Save changes' : 'Create policy')}
            </button>
            <button onClick={() => navigate('/')}
              className="px-6 py-2.5 bg-slate-800 hover:bg-slate-700 border border-slate-600 text-slate-300 rounded-xl text-sm font-medium transition-colors min-h-[44px]">
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
