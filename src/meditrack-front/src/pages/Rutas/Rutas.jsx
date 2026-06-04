import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRutas, getUsuarios } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './Rutas.css';

const ESTADO_COLORS = {
  PENDIENTE: '#f59e0b',
  EN_CURSO: '#3b82f6',
  COMPLETADA: '#10b981',
};

const Skeleton = ({ width = '100%', height = '20px', borderRadius = '4px', style = {} }) => (
  <div style={{ width, height, borderRadius, backgroundColor: '#E5E7EB', animation: 'pulse 1.5s infinite', ...style }} />
);

function Rutas() {
  const [rutas, setRutas] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    Promise.all([getRutas(), getUsuarios()])
      .then(([rutasData, usuariosData]) => {
        setRutas(rutasData);
        setUsuarios(usuariosData);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const getNombreRepartidor = (id) => {
    const u = usuarios.find(u => u.id === id);
    return u ? u.nombre : id;
  };

  const rutasFiltradas = rutas.filter(r => {
    const term = busqueda.toLowerCase();
    return (
      r.id.toLowerCase().includes(term) ||
      getNombreRepartidor(r.repartidorId).toLowerCase().includes(term) ||
      r.fecha?.toLowerCase().includes(term)
    );
  });

  return (
    <div className="container rutas-container">
      <div className="page-header-row" style={{ display: 'flex', alignItems: 'center', gap: '20px', marginBottom: '24px' }}>
        <button className="btn btn-secondary" onClick={() => navigate('/menu')}>VOLVER</button>
        <h1 style={{ fontSize: '24px', fontWeight: '800', color: '#111827' }}>Gestión de rutas</h1>
      </div>

      <div className="card">
        <div className="table-header-actions">
          <input
            className="search-input"
            style={{ margin: 0, flexGrow: 1 }}
            placeholder="Buscar por ID, repartidor o fecha..."
            value={busqueda}
            onChange={e => setBusqueda(e.target.value)}
          />
          {(user?.role === 'SUPERVISOR' || user?.role === 'ADMINISTRADOR') && (
            <button className="btn-new-shipment" onClick={() => navigate('/rutas/nueva')}>
              NUEVA RUTA
            </button>
          )}
        </div>

        {loading ? (
          <>
            <div className="rutas-table-container">
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr>
                    <th>ID Ruta</th>
                    <th>Fecha</th>
                    <th>Repartidor</th>
                    <th>Estado</th>
                    <th style={{ textAlign: 'center' }}>Envíos</th>
                    <th style={{ textAlign: 'center' }}>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {[...Array(5)].map((_, i) => (
                    <tr key={i}>
                      <td><Skeleton width="100px" height="18px" /></td>
                      <td><Skeleton width="120px" height="18px" /></td>
                      <td><Skeleton width="150px" height="18px" /></td>
                      <td><Skeleton width="80px" height="24px" borderRadius="12px" /></td>
                      <td><Skeleton width="40px" height="18px" style={{ margin: '0 auto' }} /></td>
                      <td><Skeleton width="30px" height="30px" borderRadius="50%" style={{ margin: '0 auto' }} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="rutas-mobile-grid">
              {[...Array(3)].map((_, i) => (
                <div key={i} className="ruta-card" style={{ gap: '15px' }}>
                  <div className="ruta-card-header">
                    <Skeleton width="120px" height="20px" />
                    <Skeleton width="80px" height="24px" borderRadius="12px" />
                  </div>
                  <div className="ruta-card-details">
                    <Skeleton width="160px" height="16px" style={{ marginBottom: '8px' }} />
                    <Skeleton width="190px" height="16px" style={{ marginBottom: '8px' }} />
                    <Skeleton width="100px" height="16px" />
                  </div>
                  <div className="ruta-card-footer">
                    <Skeleton width="100%" height="40px" borderRadius="8px" />
                  </div>
                </div>
              ))}
            </div>
          </>
        ) : (
          <>
            {/* Desktop Table View */}
            <div className="rutas-table-container">
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr>
                    <th>ID Ruta</th>
                    <th>Fecha</th>
                    <th>Repartidor</th>
                    <th>Estado</th>
                    <th style={{ textAlign: 'center' }}>Envíos</th>
                    <th style={{ textAlign: 'center' }}>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {rutasFiltradas.length === 0 ? (
                    <tr>
                      <td colSpan={6} style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>
                        No hay rutas registradas
                      </td>
                    </tr>
                  ) : (
                    rutasFiltradas.map(r => (
                      <tr key={r.id}>
                        <td style={{ fontWeight: 'bold', color: '#2563EB' }}>{r.id}</td>
                        <td>{r.fecha}</td>
                        <td>{getNombreRepartidor(r.repartidorId)}</td>
                        <td>
                          <span
                            className="status-tag"
                            style={{
                              backgroundColor: `${ESTADO_COLORS[r.estado]}20`,
                              color: ESTADO_COLORS[r.estado],
                            }}
                          >
                            {r.estado?.replace(/_/g, ' ')}
                          </span>
                        </td>
                        <td style={{ textAlign: 'center' }}>{r.envios?.length ?? 0}</td>
                        <td style={{ textAlign: 'center' }}>
                          <button
                            className="action-icon-btn"
                            title="Ver detalle"
                            onClick={() => navigate(`/rutas/${r.id}`)}
                          >
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563EB" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                              <circle cx="12" cy="12" r="3" />
                            </svg>
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Mobile Cards Grid View */}
            <div className="rutas-mobile-grid">
              {rutasFiltradas.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>
                  No hay rutas registradas
                </div>
              ) : (
                rutasFiltradas.map(r => (
                  <div key={r.id} className="ruta-card">
                    <div className="ruta-card-header">
                      <span className="ruta-card-id">{r.id}</span>
                      <span
                        className="status-tag"
                        style={{
                          backgroundColor: `${ESTADO_COLORS[r.estado]}20`,
                          color: ESTADO_COLORS[r.estado],
                          margin: 0
                        }}
                      >
                        {r.estado?.replace(/_/g, ' ')}
                      </span>
                    </div>
                    
                    <div className="ruta-card-details">
                      <div className="ruta-card-detail-item">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                          <line x1="16" y1="2" x2="16" y2="6"></line>
                          <line x1="8" y1="2" x2="8" y2="6"></line>
                          <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                        <span><strong>Fecha:</strong> {r.fecha}</span>
                      </div>
                      
                      <div className="ruta-card-detail-item">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                          <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                        <span><strong>Repartidor:</strong> {getNombreRepartidor(r.repartidorId)}</span>
                      </div>
                      
                      <div className="ruta-card-detail-item">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                          <rect x="1" y="3" width="15" height="13"></rect>
                          <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                          <circle cx="5.5" cy="18.5" r="2.5"></circle>
                          <circle cx="18.5" cy="18.5" r="2.5"></circle>
                        </svg>
                        <span><strong>Envíos:</strong> {r.envios?.length ?? 0}</span>
                      </div>
                    </div>
                    
                    <div className="ruta-card-footer">
                      <button className="ruta-card-btn" onClick={() => navigate(`/rutas/${r.id}`)}>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                          <circle cx="12" cy="12" r="3" />
                        </svg>
                        Ver Detalle de Ruta
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default Rutas;
