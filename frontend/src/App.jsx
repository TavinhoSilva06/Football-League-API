import { Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from './components/Layout'
import { CampeonatosPage } from './pages/CampeonatosPage'
import { CampeonatoDetalhe } from './pages/CampeonatoDetalhe'
import { TemporadasListPage } from './pages/TemporadasListPage'
import { TemporadaHub } from './pages/TemporadaHub'
import { TimesPage } from './pages/TimesPage'
import { TimeDetalhe } from './pages/TimeDetalhe'

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/campeonatos" />} />
        <Route path="/campeonatos" element={<CampeonatosPage />} />
        <Route path="/campeonatos/:campeonatoId/temporadas" element={<TemporadasListPage />} />
        <Route path="/campeonatos/:id" element={<CampeonatoDetalhe />} />
        <Route path="/temporadas/:id/*" element={<TemporadaHub />} />
        <Route path="/times" element={<TimesPage />} />
        <Route path="/times/:id" element={<TimeDetalhe />} />
        <Route path="*" element={<div className="text-center py-10"><h1 className="text-2xl font-bold">Página não encontrada</h1></div>} />
      </Routes>
    </Layout>
  )
}
