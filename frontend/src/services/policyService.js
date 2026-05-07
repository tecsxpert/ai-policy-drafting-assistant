import axios from 'axios'

// ─── MOCK DATA ────────────────────────────────────────────────────────────────
// When backend is ready, set USE_MOCK = false
const USE_MOCK = true

let mockPolicies = [
  { id: 1, title: 'Data Privacy & Protection Policy', description: 'This policy governs how the organization collects, stores, processes, and shares personal data in compliance with GDPR and applicable data protection laws. All departments must adhere to strict data minimization principles.', status: 'ACTIVE', category: 'Data Governance', createdAt: '2026-01-15T09:00:00', updatedAt: '2026-03-10T14:30:00' },
  { id: 2, title: 'Remote Work Security Guidelines', description: 'Defines security requirements for employees working remotely, including VPN usage, device encryption, and secure communication protocols. Mandatory compliance for all remote staff.', status: 'ACTIVE', category: 'Cybersecurity', createdAt: '2026-02-01T10:00:00', updatedAt: '2026-02-20T11:00:00' },
  { id: 3, title: 'AI Usage & Ethics Framework', description: 'Establishes guidelines for responsible use of AI tools within the organization. Covers bias prevention, transparency requirements, human oversight, and prohibited use cases.', status: 'UNDER_REVIEW', category: 'Technology', createdAt: '2026-03-01T08:00:00', updatedAt: '2026-03-25T16:00:00' },
  { id: 4, title: 'Employee Code of Conduct', description: 'Outlines expected professional behavior, ethical standards, and disciplinary procedures. All employees must acknowledge and sign this policy annually.', status: 'ACTIVE', category: 'HR', createdAt: '2025-12-01T09:00:00', updatedAt: '2026-01-05T10:00:00' },
  { id: 5, title: 'Vendor Risk Assessment Policy', description: 'Framework for evaluating third-party vendors before onboarding. Includes financial stability checks, security audits, compliance verification, and ongoing monitoring requirements.', status: 'DRAFT', category: 'Risk Management', createdAt: '2026-04-01T09:00:00', updatedAt: '2026-04-01T09:00:00' },
  { id: 6, title: 'Incident Response Plan', description: 'Step-by-step procedures for identifying, containing, and recovering from security incidents. Defines roles, escalation paths, and communication templates.', status: 'ACTIVE', category: 'Cybersecurity', createdAt: '2025-11-15T09:00:00', updatedAt: '2026-02-10T14:00:00' },
  { id: 7, title: 'Business Continuity Policy', description: 'Ensures the organization can maintain essential functions during and after a disaster. Includes recovery time objectives, backup procedures, and crisis communication plans.', status: 'UNDER_REVIEW', category: 'Risk Management', createdAt: '2026-02-15T09:00:00', updatedAt: '2026-04-02T10:00:00' },
  { id: 8, title: 'Social Media Usage Policy', description: 'Governs employee use of social media platforms both professionally and personally when representing the organization. Includes content guidelines and confidentiality requirements.', status: 'ARCHIVED', category: 'HR', createdAt: '2024-06-01T09:00:00', updatedAt: '2025-09-01T09:00:00' },
  { id: 9, title: 'Cloud Infrastructure Governance', description: 'Standards for provisioning, managing, and decommissioning cloud resources across AWS, Azure, and GCP. Covers cost management, security baselines, and tagging requirements.', status: 'DRAFT', category: 'Technology', createdAt: '2026-04-10T09:00:00', updatedAt: '2026-04-10T09:00:00' },
  { id: 10, title: 'Anti-Bribery & Corruption Policy', description: 'Zero-tolerance policy on bribery and corruption. Defines prohibited conduct, gift and hospitality limits, reporting procedures, and consequences for violations.', status: 'ACTIVE', category: 'Compliance', createdAt: '2025-10-01T09:00:00', updatedAt: '2026-01-10T09:00:00' },
  { id: 11, title: 'Acceptable Use Policy', description: 'Defines permitted and prohibited uses of company IT resources including computers, networks, and software. Applies to all employees and contractors.', status: 'ACTIVE', category: 'Cybersecurity', createdAt: '2025-09-01T09:00:00', updatedAt: '2026-03-01T09:00:00' },
  { id: 12, title: 'Whistleblower Protection Policy', description: 'Protects employees who report unethical behavior, fraud, or policy violations. Outlines anonymous reporting channels and non-retaliation guarantees.', status: 'ACTIVE', category: 'Compliance', createdAt: '2025-08-01T09:00:00', updatedAt: '2026-02-01T09:00:00' },
]

let nextId = 13

const delay = (ms = 400) => new Promise(r => setTimeout(r, ms))

const mockStats = () => ({
  total: mockPolicies.length,
  active: mockPolicies.filter(p => p.status === 'ACTIVE').length,
  underReview: mockPolicies.filter(p => p.status === 'UNDER_REVIEW').length,
  draftedThisMonth: mockPolicies.filter(p => p.status === 'DRAFT').length,
  byCategory: Object.entries(
    mockPolicies.reduce((acc, p) => { acc[p.category] = (acc[p.category] || 0) + 1; return acc }, {})
  ).map(([category, count]) => ({ category, count })),
  byStatus: ['ACTIVE','DRAFT','UNDER_REVIEW','ARCHIVED'].map(s => ({
    status: s, count: mockPolicies.filter(p => p.status === s).length
  })),
  byMonth: [
    { month: 'Nov', count: 2 }, { month: 'Dec', count: 3 },
    { month: 'Jan', count: 4 }, { month: 'Feb', count: 5 },
    { month: 'Mar', count: 3 }, { month: 'Apr', count: mockPolicies.length - 17 < 0 ? 1 : mockPolicies.length - 17 },
  ],
})

// ─── REAL API SETUP ───────────────────────────────────────────────────────────
const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080' })
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ─── SERVICE FUNCTIONS ────────────────────────────────────────────────────────
export const loginUser = async (username, password) => {
  if (USE_MOCK) {
    await delay()
    if (username === 'admin' && password === 'admin123') {
      return { data: { token: 'mock-jwt-token-abc123' } }
    }
    throw { response: { data: { message: 'Invalid credentials' } } }
  }
  return axios.post(`${import.meta.env.VITE_API_URL}/auth/login`, { username, password })
}

export const getPolicies = async (page = 0, size = 8) => {
  if (USE_MOCK) {
    await delay()
    const start = page * size
    const content = mockPolicies.slice(start, start + size)
    return { data: { content, totalPages: Math.ceil(mockPolicies.length / size), totalElements: mockPolicies.length, number: page } }
  }
  return api.get(`/api/policies?page=${page}&size=${size}`)
}

export const getPolicyById = async (id) => {
  if (USE_MOCK) {
    await delay()
    const policy = mockPolicies.find(p => p.id === parseInt(id))
    if (!policy) throw { response: { status: 404 } }
    return { data: policy }
  }
  return api.get(`/api/policies/${id}`)
}

export const createPolicy = async (data) => {
  if (USE_MOCK) {
    await delay()
    const newPolicy = { ...data, id: nextId++, createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() }
    mockPolicies.unshift(newPolicy)
    return { data: newPolicy }
  }
  return api.post('/api/policies', data)
}

export const updatePolicy = async (id, data) => {
  if (USE_MOCK) {
    await delay()
    mockPolicies = mockPolicies.map(p => p.id === parseInt(id) ? { ...p, ...data, updatedAt: new Date().toISOString() } : p)
    return { data: mockPolicies.find(p => p.id === parseInt(id)) }
  }
  return api.put(`/api/policies/${id}`, data)
}

export const deletePolicy = async (id) => {
  if (USE_MOCK) {
    await delay()
    mockPolicies = mockPolicies.filter(p => p.id !== parseInt(id))
    return { data: {} }
  }
  return api.delete(`/api/policies/${id}`)
}

export const searchPolicies = async (q) => {
  if (USE_MOCK) {
    await delay(200)
    const lower = q.toLowerCase()
    const content = mockPolicies.filter(p =>
      p.title.toLowerCase().includes(lower) ||
      p.category.toLowerCase().includes(lower) ||
      p.description.toLowerCase().includes(lower)
    )
    return { data: { content } }
  }
  return api.get(`/api/policies/search?q=${q}`)
}

export const getStats = async () => {
  if (USE_MOCK) {
    await delay()
    return { data: mockStats() }
  }
  return api.get('/api/policies/stats')
}

export const exportCSV = async () => {
  if (USE_MOCK) {
    await delay()
    const headers = 'ID,Title,Status,Category,Created\n'
    const rows = mockPolicies.map(p =>
      `${p.id},"${p.title}",${p.status},${p.category},${p.createdAt}`
    ).join('\n')
    return { data: new Blob([headers + rows], { type: 'text/csv' }) }
  }
  return api.get('/api/policies/export', { responseType: 'blob' })
}

export const analysePolicy = async (id) => {
  if (USE_MOCK) {
    await delay(1800)
    const policy = mockPolicies.find(p => p.id === parseInt(id))
    return {
      data: {
        summary: `This ${policy?.category ?? 'policy'} policy is well-structured and covers the key requirements. It addresses compliance needs and provides clear guidance for employees.`,
        recommendations: [
          { description: 'Add a review schedule — recommend quarterly reviews to keep content current.' },
          { description: 'Include measurable KPIs to track policy compliance rates across departments.' },
          { description: 'Expand the scope to cover third-party contractors and vendors explicitly.' },
        ],
        category: policy?.category ?? 'General',
        confidence: 0.87,
      }
    }
  }
  const token = localStorage.getItem('token')
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/policies/${id}/ai-analyse`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
  })
  return { data: await res.json() }
}
