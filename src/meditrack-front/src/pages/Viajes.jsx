import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getRutas } from '../services/api';
import './Viajes.css';

function Viajes() {
    const { user } = useAuth();
    const navigate = useNavigate();
    
    // Estados de datos
    const [rutas, setRutas] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchRutas = async (silent = false) => {
        if (!silent) setLoading(true);
        try {
            const todasLasRutas = await getRutas();
            const misRutas = todasLasRutas.filter(r => r.repartidorId === user.id);
            setRutas(misRutas);
        } catch (error) {
            console.error("Error al cargar rutas", error);
        } finally {
            if (!silent) setLoading(false);
        }
    };

    useEffect(() => {
        if (user) fetchRutas();
    }, [user]);

    // Helpers para obtener el estado actual
    const rutaActiva = rutas.find(r => r.estado !== 'COMPLETADA');
    const enviosOrdenados = useMemo(() => {
        return rutaActiva?.envios ? [...rutaActiva.envios].sort((a, b) => a.orden - b.orden) : [];
    }, [rutaActiva]);
    
    // Busca el primer envío que NO esté terminado
    const paradaActual = useMemo(() => {
        return enviosOrdenados.find(item => 
            ['PENDIENTE', 'ASIGNADO', 'EN_PREPARACION', 'EN_TRANSITO', 'EN_PUNTO_DE_ENTREGA'].includes(item.envio.estado)
        );
    }, [enviosOrdenados]);

    if (loading) {
        return (
            <div className="viajes-loading-screen">
                <div className="viajes-loading-spinner" />
                <p className="viajes-loading-text">Cargando ruta activa...</p>
            </div>
        );
    }

    if (!rutaActiva) {
        return (
            <div className="viajes-empty-screen">
                <div className="viajes-empty-card animate-slide-up">
                    <div className="viajes-empty-icon">📦</div>
                    <h2 className="viajes-empty-title">No tienes viajes activos</h2>
                    <p className="viajes-empty-desc">En este momento no tienes una hoja de ruta asignada para realizar envíos.</p>
                    <button 
                        onClick={() => navigate('/inicio-repartidor')} 
                        className="viajes-empty-btn btn-action-hover"
                    >
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
                        Volver al Inicio
                    </button>
                </div>
            </div>
        );
    }

    // Estadísticas de ruta para la barra de progreso
    const totalEnvios = enviosOrdenados.length;
    const enviosCompletados = enviosOrdenados.filter(item => 
        ['ENTREGADO', 'INCIDENTE_REPORTADO', 'CANCELADO'].includes(item.envio.estado)
    ).length;
    const porcentajeCompletado = totalEnvios > 0 ? Math.round((enviosCompletados / totalEnvios) * 100) : 0;
    
    // Determina si la ruta ya tiene progreso
    const rutaIniciada = enviosCompletados > 0 || (paradaActual && !['PENDIENTE', 'ASIGNADO'].includes(paradaActual.envio.estado));

    return (
        <div className="viajes-page">
            
            {/* Contenedor principal limitado a 600px */}
            <div className="viajes-content-container">
            
                {/* Cabecera Premium */}
                <div className="viajes-header animate-fade-in">
                    <button 
                        onClick={() => navigate('/inicio-repartidor')} 
                        className="viajes-header-back-btn btn-action-hover"
                    >
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
                    </button>
                    <div>
                        <h1 className="viajes-header-title">Hoja de Ruta</h1>
                        <span className="viajes-header-subtitle">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
                            Asignada: {rutaActiva.fecha}
                        </span>
                    </div>
                </div>

                {/* Dashboard del Progreso */}
                <div className="viajes-progress-card animate-slide-up">
                    <div className="viajes-progress-header">
                        <div>
                            <span className="viajes-progress-code-label">CÓDIGO DE RUTA</span>
                            <p className="viajes-progress-code-value">{rutaActiva.id}</p>
                        </div>
                        <div className="viajes-progress-right">
                            <span className="viajes-progress-percentage-num">{porcentajeCompletado}%</span>
                            <p className="viajes-progress-percentage-label">Completado</p>
                        </div>
                    </div>

                    <div className="viajes-progress-bar-track">
                        <div 
                            className="viajes-progress-bar-fill" 
                            style={{ width: `${porcentajeCompletado}%` }} 
                        />
                    </div>
                    <span className="viajes-progress-summary">
                        {enviosCompletados} de {totalEnvios} paradas completadas
                    </span>
                </div>

                {/* Lista de Paradas con Conexión Visual (Timeline vertical) */}
                <div className="viajes-itinerary-card animate-slide-up">
                    <h3 className="viajes-itinerary-title">ITINERARIO DE PARADAS</h3>
                    
                    {/* Línea vertical de la ruta */}
                    {totalEnvios > 1 && (
                        <div className="viajes-itinerary-vertical-line" />
                    )}

                    <div className="viajes-itinerary-list">
                        {enviosOrdenados.map((item) => {
                            const esCompletado = ['ENTREGADO', 'INCIDENTE_REPORTADO', 'CANCELADO'].includes(item.envio.estado);
                            const esSiguiente = paradaActual && paradaActual.id === item.id;
                            
                            let badgeClass = 'badge-default';
                            let badgeText = item.envio.estado.replace(/_/g, ' ');
                            
                            if (item.envio.estado === 'ENTREGADO') {
                                badgeClass = 'badge-entregado';
                                badgeText = 'Entregado';
                            } else if (item.envio.estado === 'EN_PUNTO_DE_ENTREGA') {
                                badgeClass = 'badge-en-destino';
                                badgeText = 'En Destino';
                            } else if (item.envio.estado === 'EN_TRANSITO') {
                                badgeClass = 'badge-en-ruta';
                                badgeText = 'En Ruta';
                            } else if (item.envio.estado === 'EN_PREPARACION') {
                                badgeClass = 'badge-en-preparacion';
                                badgeText = 'En Preparación';
                            } else if (['PENDIENTE', 'ASIGNADO'].includes(item.envio.estado)) {
                                badgeClass = 'badge-asignado';
                                badgeText = 'Asignado';
                            } else if (item.envio.estado === 'INCIDENTE_REPORTADO') {
                                badgeClass = 'badge-incidente';
                                badgeText = 'Incidente';
                            }

                            return (
                                <div key={item.id} className="viajes-parada-row">
                                    {/* Indicador de número / estado */}
                                    <div className="viajes-parada-indicator">
                                        {esCompletado ? (
                                            <div className="viajes-node-completed">✓</div>
                                        ) : esSiguiente ? (
                                            <div className="viajes-node-active timeline-node-active-blue">
                                                {item.orden}
                                            </div>
                                        ) : (
                                            <div className="viajes-node-pending">
                                                {item.orden}
                                            </div>
                                        )}
                                    </div>

                                    {/* Información de la parada */}
                                    <div 
                                        onClick={() => esSiguiente && navigate('/viajes/detalle')}
                                        className={`viajes-parada-content ${esSiguiente ? 'is-next card-hover' : ''}`}
                                    >
                                        <div className="viajes-parada-header">
                                            <h4 className={`viajes-parada-destinatario ${esCompletado ? 'is-completed' : ''}`}>
                                                {item.envio.destinatario}
                                            </h4>
                                            {esSiguiente && (
                                                <span className="viajes-parada-next-badge">
                                                    SIGUIENTE
                                                </span>
                                            )}
                                        </div>
                                        
                                        <p className="viajes-parada-address">
                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                                            {item.envio.direccionEntrega}
                                        </p>

                                        <span className={`viajes-parada-badge ${badgeClass}`}>
                                            {badgeText}
                                        </span>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            
            </div>

            {/* Botón de acción fijo en la parte inferior */}
            <div className="viajes-footer">
                <div className="viajes-footer-container">
                    <button 
                        onClick={() => navigate('/viajes/detalle')}
                        className={`viajes-footer-btn btn-action-hover ${paradaActual ? 'is-active' : 'is-inactive'}`}
                    >
                        {paradaActual ? (
                            <>
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><polygon points="5 3 19 12 5 21 5 3"></polygon></svg>
                                {rutaIniciada ? 'CONTINUAR RUTA' : 'INICIAR RUTA'} (PARADA {paradaActual.orden})
                            </>
                        ) : (
                            <>
                                🏁 VER RESULTADOS FINALES
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Viajes;