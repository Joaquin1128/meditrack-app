import { useState } from "react";
import { getTrackingPublico } from "../services/api";
import Navbar from '../components/Navbar';
import fondoCamion from '../assets/meditrack-bg.png';

const STEP_DETAILS = {
  PENDIENTE: 'Recibimos tu solicitud de envío. Estamos procesando los detalles.',
  ASIGNADO: 'Un chofer y vehículo han sido asignados para recolectar tu paquete.',
  EN_PREPARACION: 'Tu paquete está siendo embalado y verificado en nuestro centro de distribución.',
  EN_TRANSITO: '¡Tu envío está en camino! Salió de nuestro centro y se dirige a destino.',
  INCIDENTE_REPORTADO: 'Se ha registrado un inconveniente con tu envío. Nuestro equipo de soporte está trabajando para solucionarlo a la brevedad.',
  EN_PUNTO_DE_ENTREGA: 'Tu paquete ha llegado al centro de distribución final, listo para la entrega.',
  ENTREGADO: 'El envío ha sido entregado exitosamente. ¡Gracias por elegir MediTrack!',
  CANCELADO: 'Este envío ha sido cancelado. Por favor contáctanos para más información.'
};

export default function TrackingPublico() {
  const [trackingId, setTrackingId] = useState('');
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  async function consultar(e) {
    e.preventDefault();
    setError('');
    setResultado(null);

    const id = trackingId.trim();
    if (!id) {
      setError('Ingrese un Tracking ID');
      return;
    }

    setCargando(true);
    try {
      const data = await getTrackingPublico(id);
      setResultado(data);
    } catch (err) {
      setError(err?.message || 'Error al consultar tracking');
    } finally {
      setCargando(false);
    }
  }

  const obtenerPasos = () => {
    if (!resultado || !resultado.estado) return [];

    const estadoActual = resultado.estado;
    const historial = resultado.historial || [];
    const caminoBase = ['PENDIENTE', 'ASIGNADO', 'EN_PREPARACION', 'EN_TRANSITO'];

    if (estadoActual === 'CANCELADO') {
      const saltoDirecto = !historial.some(h => {
        if (h.tipo !== 'CAMBIO_ESTADO' || !h.detalle) return false;
        const detalleNormalizado = h.detalle
          .toUpperCase()
          .normalize("NFD")
          .replace(/[\u0300-\u036f]/g, "");
        
        return (
          detalleNormalizado.includes('ASIGNADO') || 
          detalleNormalizado.includes('PREPARACION') || 
          detalleNormalizado.includes('TRANSITO')
        );
      });
      
      if (saltoDirecto) return ['PENDIENTE', 'CANCELADO'];
    }

    const registroIncidente = historial.find(h => {
      if (h.tipo !== 'CAMBIO_ESTADO' || !h.detalle) return false;
      const detalleNormalizado = h.detalle
        .toUpperCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");
      return detalleNormalizado.includes('INCIDENTE_REPORTADO');
    });

    if (registroIncidente || estadoActual === 'INCIDENTE_REPORTADO' || estadoActual === 'CANCELADO') {
      const detalle = registroIncidente?.detalle || "";
      const pasoPrevio = detalle.split(' → ')[0]?.trim().toUpperCase();
      if (pasoPrevio === 'EN_PUNTO_DE_ENTREGA') {
        return [...caminoBase, 'EN_PUNTO_DE_ENTREGA', 'INCIDENTE_REPORTADO', 'CANCELADO'];
      } else {
        return [...caminoBase, 'INCIDENTE_REPORTADO', 'CANCELADO'];
      }
    }
    return [...caminoBase, 'EN_PUNTO_DE_ENTREGA', 'ENTREGADO'];
  };

  const pasos = obtenerPasos();
  const indiceActual = pasos.indexOf(resultado?.estado);

  const getStepConfig = (step, index) => {
    const isPassed = index <= indiceActual && indiceActual !== -1;
    if (!isPassed) return { color: '#E5E7EB', icon: '○', textColor: '#9CA3AF' };
    if (step === 'INCIDENTE_REPORTADO') return { color: '#F59E0B', icon: '⚠️', textColor: '#F59E0B' };
    if (step === 'CANCELADO') return { color: '#EF4444', icon: '❌', textColor: '#EF4444' };
    return { color: '#10B981', icon: '✅', textColor: '#10B981' };
  };

  const getLineColor = (index) => {
    if (indiceActual === -1 || index >= indiceActual) return '#E5E7EB';
    const nextStep = pasos[index + 1];
    if (nextStep === 'INCIDENTE_REPORTADO') return '#F59E0B';
    if (nextStep === 'CANCELADO') return '#EF4444';
    return '#10B981';
  };

  const encontrarFechaHora = (step, index) => {
    if (!resultado) return '';
    
    if (step === resultado.estado) {
      const f = resultado.fechaUltimoEstado || '';
      const h = resultado.horaUltimoEstado || '';
      if (!f) return '';
      const [y, m, d] = f.split('-');
      return y && m && d ? `${d}/${m}/${y} ${h}`.trim() : `${f} ${h}`.trim();
    }

    if (index > indiceActual) return '';

    const historial = resultado.historial || [];
    const registro = historial.find(h => {
      if (h.tipo !== 'CAMBIO_ESTADO' || !h.detalle) return false;
      const detalleNormalizado = h.detalle
        .toUpperCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");
      return detalleNormalizado.includes(`→ ${step.replace(/_/g, ' ')}`) || detalleNormalizado.includes(`→ ${step}`);
    });

    if (registro && registro.fecha) {
      const [y, m, d] = registro.fecha.split('-');
      const fFormateada = y && m && d ? `${d}/${m}/${y}` : registro.fecha;
      return `${fFormateada} ${registro.hora || ''}`.trim();
    }

    if (step === 'PENDIENTE') {
      const registroCreacion = historial.find(h => h.tipo === 'CREACION');
      if (registroCreacion && registroCreacion.fecha) {
        const [y, m, d] = registroCreacion.fecha.split('-');
        const fFormateada = y && m && d ? `${d}/${m}/${y}` : registroCreacion.fecha;
        return `${fFormateada} ${registroCreacion.hora || ''}`.trim();
      }
    }

    return '';
  };

  return (
    <div style={{ backgroundImage: `url(${fondoCamion})`, backgroundSize: 'cover', backgroundPosition: 'center', backgroundRepeat: 'no-repeat', minHeight: '100vh', width: '100%', display: 'flex', flexDirection: 'column' }}>
      <Navbar customLoginIcon={true} />

      <div style={{ flex: 1, display: 'flex', alignItems: 'flex-start', justifyContent: 'center', padding: 16, paddingTop: '8vh' }}>
        <div style={{ maxWidth: 820, width: '100%', padding: 16, color: '#111827', background: 'rgba(255, 255, 255, 0.85)', borderRadius: 16, backdropFilter: 'blur(4px)', boxShadow: '0 4px 30px rgba(0, 0, 0, 0.1)' }}>
          <h1>Seguimiento público</h1>
          <p>Ingresá tu Tracking ID para consultar el estado de tu envío.</p>

          <form onSubmit={consultar} style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 12, border: '1px solid #ddd', borderRadius: 8, padding: '4px 12px', background: '#fff' }}>
            <input
              value={trackingId}
              onChange={(e) => setTrackingId(e.target.value)}
              placeholder="Ej: A1B2C3D4"
              style={{ flex: 1, padding: '10px 0', border: 'none', outline: 'none', background: 'transparent' }}
            />
            <button type="submit"
              disabled={cargando}
              style={{
                padding: '8px',
                background: 'none',
                color: cargando ? '#6B7280' : '#00A86B',
                border: 'none',
                cursor: cargando ? 'not-allowed' : 'pointer',
                transition: "transform 0.05s ease, filter 0.15s ease",
                filter: cargando ? 'brightness(0.95)' : 'none',
                fontWeight: 'bold',
                fontSize: 20
              }}
              onMouseDown={(e) => {
                if (!cargando) e.currentTarget.style.transform = 'scale(0.95)';
              }}
              onMouseUp={(e) => {
                e.currentTarget.style.transform = 'scale(1)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'scale(1)';
              }}
              onMouseEnter={(e) => {
                if (!cargando) e.currentTarget.style.filter = 'brightness(0.85)';
              }}
              onMouseOut={(e) => {
                e.currentTarget.style.filter = cargando ? 'brightness(0.95)' : 'none';
              }}
            >
              {cargando ? '...' : '🔍'}
            </button>
          </form>
          {error && (
            <div style={{ marginTop: 16, color: '#DC2626' }}>
              {error}
            </div>
          )}

          {resultado && (
            <div style={{ marginTop: 24, padding: 16, borderTop: '1px solid #ddd' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20, color: "#111827", gap: 10, flexWrap: 'wrap' }}>
                <h2>Envío #{trackingId}</h2>
              </div>

              {pasos.length > 0 ? (
                <div style={{ position: 'relative', display: 'flex', flexDirection: 'column' }}>
                  {pasos.map((step, index) => {
                    const config = getStepConfig(step, index);
                    const isPassed = index <= indiceActual && indiceActual !== -1;
                    const fechaHoraText = encontrarFechaHora(step, index);
                    const detalleTexto = index === indiceActual ? STEP_DETAILS[step] : `El envío completó la etapa de ${step.toLowerCase().replace(/_/g, ' ')}.`;

                    return (
                      <div key={`${step}-${index}`} style={{ display: 'flex', gap: 16, marginBottom: index === pasos.length - 1 ? 0 : 28, position: 'relative', zIndex: 1, alignItems: 'flex-start' }}>
                        
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative', flexShrink: 0 }}>
                          <div style={{
                            width: 36,
                            height: 36,
                            borderRadius: '50%',
                            backgroundColor: isPassed ? config.color : 'white',
                            border: `2px solid ${isPassed ? config.color : '#D1D5DB'}`,
                            color: isPassed ? 'white' : '#9CA3AF',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: 18,
                            fontWeight: 700,
                            boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
                            zIndex: 2
                          }}>
                            {config.icon}
                          </div>
                          
                          {index < pasos.length - 1 && (
                            <div style={{
                              position: 'absolute',
                              top: '36px',
                              left: '50%',
                              transform: 'translateX(-50%)',
                              width: '3px',
                              height: '28px',
                              backgroundColor: getLineColor(index),
                              zIndex: 1
                            }} />
                          )}
                        </div>

                        <div style={{ flex: 1 }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4, gap: 10, flexWrap: 'wrap' }}>
                            <div style={{ fontSize: 16, fontWeight: 700, color: isPassed ? config.textColor : '#9CA3AF', textTransform: 'uppercase' }}>
                              {step.replace(/_/g, ' ')}
                            </div>
                            <div style={{ fontSize: 13, color: index === indiceActual ? config.textColor : '#9CA3AF', fontWeight: index === indiceActual ? '600' : '400' }}>
                              {fechaHoraText}
                            </div>
                          </div>
                          <p style={{ fontSize: 14, margin: 0, color: index === indiceActual ? '#111827' : '#9CA3AF' }}>
                            {isPassed ? detalleTexto : 'Etapa pendiente.'}
                          </p>
                        </div>

                      </div>
                    );
                  })}
                </div>
              ) : (
                <div style={{ textAlign: 'center', padding: '20px 0', color: '#6B7280' }}>
                  Ha ocurrido un error al procesar el historial del envío.
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}