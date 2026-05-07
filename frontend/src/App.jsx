import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './AuthContext'
import { ProtectedRoute, ErrorBoundary } from './components/index'
import LoginPage from './pages/LoginPage'
import ListPage from './pages/ListPage'
import FormPage from './pages/FormPage'
import DetailPage from './pages/DetailPage'
import Dashboard from './pages/Dashboard'
import AnalyticsPage from './pages/AnalyticsPage'

function Protected({ children }) {
  return (
    <ProtectedRoute>
      <ErrorBoundary>{children}</ErrorBoundary>
    </ProtectedRoute>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<Protected><ListPage /></Protected>} />
          <Route path="/create" element={<Protected><FormPage /></Protected>} />
          <Route path="/edit/:id" element={<Protected><FormPage /></Protected>} />
          <Route path="/policy/:id" element={<Protected><DetailPage /></Protected>} />
          <Route path="/dashboard" element={<Protected><Dashboard /></Protected>} />
          <Route path="/analytics" element={<Protected><AnalyticsPage /></Protected>} />
          <Route path="*" element={<Protected><ListPage /></Protected>} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
