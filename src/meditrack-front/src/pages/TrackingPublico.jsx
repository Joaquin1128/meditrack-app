import { useState } from "react";
import { getTrackingPublico } from "../services/api";
import Navbar from '../components/Navbar';
import bgImg from '../assets/bg.png';
import { Package, Truck, MapPin, AlertTriangle, CheckCircle2, XCircle, Search, Loader2 } from "lucide-react";

const STEP_DETAILS = {
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
      
      if (['PENDIENTE', 'ASIGNADO', 'EN_PREPARACION'].includes(data.estado)) {
        data.estadoVisual = 'EN_PREPARACION';
      } else {
        data.estadoVisual = data.estado;
      }
      
      setResultado(data);
    } catch (err) {
      setError(err?.message || 'Error al consultar tracking');
    } finally {
      setCargando(false);
    }
  }

  const obtenerPasos = () => {
    if (!resultado) return [];
    
    const historial = resultado.historial || [];
    const estadosVistos = new Set();

    historial.forEach(h => {
      if (h.estado) {
        const est = h.estado.toUpperCase().trim();
        if (['PENDIENTE', 'ASIGNADO', 'EN_PREPARACION'].includes(est)) {
          estadosVistos.add('EN_PREPARACION');
        } else {
          estadosVistos.add(est);
        }
      }
    });

    if (resultado.estadoVisual) {
      estadosVistos.add(resultado.estadoVisual);
    }

    const ordenLogico = [
      'EN_PREPARACION',
      'EN_TRANSITO',
      'EN_PUNTO_DE_ENTREGA',
      'INCIDENTE_REPORTADO',
      'ENTREGADO',
      'CANCELADO'
    ];

    return ordenLogico.filter(estado => estadosVistos.has(estado));
  };

  const pasos = obtenerPasos();

  const getStepConfig = (step) => {
    switch (step) {
      case 'EN_PREPARACION':
        return { color: '#10B981', icon: Package, textColor: '#10B981' };
      case 'EN_TRANSITO':
        return { color: '#10B981', icon: Truck, textColor: '#10B981' };
      case 'EN_PUNTO_DE_ENTREGA':
        return { color: '#10B981', icon: MapPin, textColor: '#10B981' };
      case 'INCIDENTE_REPORTADO':
        return { color: '#F59E0B', icon: AlertTriangle, textColor: '#F59E0B' };
      case 'ENTREGADO':
        return { color: '#10B981', icon: CheckCircle2, textColor: '#10B981' };
      case 'CANCELADO':
        return { color: '#EF4444', icon: XCircle, textColor: '#EF4444' };
      default:
        return { color: '#10B981', icon: Package, textColor: '#10B981' };
    }
  };

  const getLineColor = (index) => {
    if (index >= pasos.length - 1) return '#E5E7EB';
    const nextStep = pasos[index + 1];
    if (nextStep === 'INCIDENTE_REPORTADO') return '#F59E0B';
    if (nextStep === 'CANCELADO') return '#EF4444';
    return '#10B981';
  };

  const encontrarFechaHora = (step) => {
    if (!resultado) return '';
    
    if (step === resultado.estadoVisual) {
      const f = resultado.fechaUltimoEstado || '';
      const h = resultado.horaUltimoEstado || '';
      if (!f) return '';
      const [y, m, d] = f.split('-');
      return y && m && d ? `${d}/${m}/${y} ${h}`.trim() : `${f} ${h}`.trim();
    }

    const historial = resultado.historial || [];
    
    const registro = [...historial].reverse().find(h => {
      if (!h.estado) return false;
      const est = h.estado.toUpperCase().trim();
      if (step === 'EN_PREPARACION') {
        return ['PENDIENTE', 'ASIGNADO', 'EN_PREPARACION'].includes(est);
      }
      return est === step;
    });

    if (registro && registro.fecha) {
      const [y, m, d] = registro.fecha.split('-');
      const fFormateada = y && m && d ? `${d}/${m}/${y}` : registro.fecha;
      return `${fFormateada} ${registro.hora || ''}`.trim();
    }

    return '';
  };

  return (
    <div style={{ backgroundImage: `url(${bgImg})`, backgroundSize: 'cover', backgroundPosition: 'center', backgroundRepeat: 'no-repeat', minHeight: '100vh', width: '100%', display: 'flex', flexDirection: 'column' }}>
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
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
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
              {cargando ? <Loader2 size={20} className="animate-spin" /> : <Search size={20} />}
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
                    const config = getStepConfig(step);
                    const IconComponent = config.icon;
                    const fechaHoraText = encontrarFechaHora(step);
                    const detalleTexto = index === pasos.length - 1 ? STEP_DETAILS[step] : `El envío completó la etapa de ${step.toLowerCase().replace(/_/g, ' ')}.`;

                    return (
                      <div key={`${step}-${index}`} style={{ display: 'flex', gap: 16, marginBottom: index === pasos.length - 1 ? 0 : 28, position: 'relative', zIndex: 1, alignItems: 'flex-start' }}>
                        
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative', flexShrink: 0 }}>
                          <div style={{
                            width: 36,
                            height: 36,
                            borderRadius: '50%',
                            backgroundColor: config.color,
                            border: `2px solid ${config.color}`,
                            color: 'white',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
                            zIndex: 2
                          }}>
                            <IconComponent size={18} strokeWidth={2.5} />
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
                            <div style={{ fontSize: 16, fontWeight: 700, color: config.textColor, textTransform: 'uppercase' }}>
                              {step.replace(/_/g, ' ')}
                            </div>
                            <div style={{ fontSize: 13, color: index === pasos.length - 1 ? config.textColor : '#9CA3AF', fontWeight: index === pasos.length - 1 ? '600' : '400' }}>
                              {fechaHoraText}
                            </div>
                          </div>
                          <p style={{ fontSize: 14, margin: 0, color: index === pasos.length - 1 ? '#111827' : '#9CA3AF' }}>
                            {detalleTexto}
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