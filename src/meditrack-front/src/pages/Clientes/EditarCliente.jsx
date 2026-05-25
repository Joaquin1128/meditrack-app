import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import DireccionAutocomplete from '../../components/DireccionAutocomplete';
import CuitInput, { limpiarCuit } from '../../components/CuitInput';
import { getClienteById, updateCliente } from '../../services/api';
import { getTipoStyles, iconos, DefaultIcon, PinIcon } from '../../util/Util';

function FieldError({ errores, campo }) {
    if (!errores?.[campo]) return null;
    return <span style={{ color: '#dc3545', fontSize: '12px', marginTop: '4px', display: 'block' }}>{errores[campo]}</span>;
}

function formatearCuitDesdeDB(cuit) {
    if (!cuit) return '';
    const digits = cuit.replace(/\D/g, '');
    if (digits.length !== 11) return cuit;
    return `${digits.slice(0, 2)}-${digits.slice(2, 10)}-${digits.slice(10)}`;
}

function EditarCliente() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [form, setForm] = useState(null);
    const [erroresApi, setErroresApi] = useState({});
    const [errorGeneral, setErrorGeneral] = useState('');

    useEffect(() => {
        getClienteById(id)
            .then(data => setForm({ ...data, cuit: formatearCuitDesdeDB(data.cuit) }))
            .catch(() => setErrorGeneral('Error al cargar datos del cliente.'));
    }, [id]);

    const handleDireccionSeleccionada = (data) => {
        setForm(prev => ({ ...prev, direccion: data.direccion, latitud: data.latitud, longitud: data.longitud, placeId: data.placeId }));
    };

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        if (erroresApi[e.target.name]) setErroresApi({ ...erroresApi, [e.target.name]: undefined });
    };

    const handleCuitChange = (valor) => {
        setForm({ ...form, cuit: valor });
        if (erroresApi.cuit) setErroresApi({ ...erroresApi, cuit: undefined });
    };

    const handleGuardar = async () => {
        setErroresApi({});
        setErrorGeneral('');
        try {
            await updateCliente(id, { ...form, cuit: limpiarCuit(form.cuit) });
            navigate('/clientes');
        } catch (err) {
            if (err.type === 'validation') setErroresApi(err.errores);
            else setErrorGeneral(err.message || 'Error al actualizar cliente.');
        }
    };

    if (!form) return <div className="container">Cargando...</div>;

    return (
        <div className="container">
            <div className="page-header"><h1>Editar Cliente</h1></div>
            <div className="card">
                {errorGeneral && (
                    <div style={{ color: '#dc3545', backgroundColor: '#f8d7da', border: '1px solid #f5c6cb', padding: '10px', borderRadius: '4px', marginBottom: '15px', fontWeight: 'bold' }}>
                        {errorGeneral}
                    </div>
                )}

                <div
                    style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '20px',
                        marginBottom: '30px',
                        paddingBottom: '20px',
                        borderBottom: '1px solid #E5E7EB'
                    }}
                >

                    <div
                        style={{
                            width: '90px',
                            height: '90px',
                            borderRadius: '50%',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            border: '1px solid #E5E7EB',
                            ...getTipoStyles(form.tipoEstablecimiento)
                        }}
                    >
                        {(() => {
                            const IconComponent = iconos[form.tipoEstablecimiento] || DefaultIcon;
                            return <IconComponent size={36} />;
                        })()}
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px', marginBottom: '30px', paddingBottom: '20px', borderBottom: '1px solid #E5E7EB' }}>
                    <div style={{ width: '90px', height: '90px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '34px', fontWeight: '700', border: '1px solid #E5E7EB', ...getTipoStyles(form.tipoEstablecimiento) }}>
                        {iconos[form.tipoEstablecimiento] || '🏢'}
                    </div>
                    <div>
                        <h2 style={{ margin: 0, fontSize: '22px', fontWeight: '700', color: '#111827' }}>{form.nombre || 'Cliente'}</h2>
                        <div style={{ marginTop: '8px' }}>
                            <span style={{ padding: '6px 10px', borderRadius: '999px', fontWeight: '600', fontSize: '12px', ...getTipoStyles(form.tipoEstablecimiento) }}>
                                {form.tipoEstablecimiento || 'SIN TIPO'}
                            </span>
                        </div>
                    </div>
                </div>

                <div className="form-grid">
                    <div className="form-group form-full">
                        <label>ID</label>
                        <input value={form.id || ''} disabled className="input-locked" />
                    </div>

                    <div className="form-group">
                        <label>Nombre *</label>
                        <input name="nombre" value={form.nombre || ''} onChange={handleChange} style={erroresApi.nombre ? { borderColor: '#dc3545' } : {}} />
                        <FieldError errores={erroresApi} campo="nombre" />
                    </div>

                    <div className="form-group">
                        <label>Tipo de establecimiento *</label>
                        <select name="tipoEstablecimiento" value={form.tipoEstablecimiento || ''} onChange={handleChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: erroresApi.tipoEstablecimiento ? '1px solid #dc3545' : '1px solid #ddd' }}>
                            <option value="">Seleccione</option>
                            <option value="LABORATORIO">Laboratorio</option>
                            <option value="DEPOSITO">Depósito</option>
                            <option value="HOSPITAL">Hospital</option>
                            <option value="FARMACIA">Farmacia</option>
                        </select>
                        <FieldError errores={erroresApi} campo="tipoEstablecimiento" />
                    </div>

                    <div className="form-group">
                        <label>CUIT *</label>
                        <CuitInput value={form.cuit || ''} onChange={handleCuitChange} error={erroresApi.cuit} />
                    </div>

                    <div className="form-group">
                        <label>GLN *</label>
                        <input name="gln" value={form.gln || ''} onChange={handleChange} placeholder="13 dígitos" maxLength={13} style={erroresApi.gln ? { borderColor: '#dc3545' } : {}} />
                        <FieldError errores={erroresApi} campo="gln" />
                    </div>

                    <div className="form-group">
                        <label>Teléfono *</label>
                        <input name="telefono" value={form.telefono || ''} onChange={handleChange} style={erroresApi.telefono ? { borderColor: '#dc3545' } : {}} />
                        <FieldError errores={erroresApi} campo="telefono" />
                    </div>

                    <div className="form-group">
                        <label>Email *</label>
                        <input type="email" name="email" value={form.email || ''} onChange={handleChange} style={erroresApi.email ? { borderColor: '#dc3545' } : {}} />
                        <FieldError errores={erroresApi} campo="email" />
                    </div>

                    <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                        <label>Dirección *</label>
                        <DireccionAutocomplete onSelect={handleDireccionSeleccionada} />
                        {form.direccion && (

                            <div
                                style={{
                                    marginTop: '12px',
                                    padding: '12px',
                                    background: '#F9FAFB',
                                    border: '1px solid #E5E7EB',
                                    borderRadius: '10px',
                                    color: '#374151',
                                    fontSize: '14px',
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: '8px'
                                }}
                            >
                                <PinIcon size={18} style={{ color: '#EF4444', flexShrink: 0 }} />
                                <span>{form.direccion}</span>
                            <div style={{ marginTop: '12px', padding: '12px', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '10px', color: '#374151', fontSize: '14px' }}>
                                📍 {form.direccion}
                            </div>
                        )}
                        <FieldError errores={erroresApi} campo="direccion" />
                    </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '6px', marginTop: '25px', paddingTop: '20px', borderTop: '1px solid #eee' }}>
                    <button className="btn btn-secondary" onClick={() => navigate('/clientes')}>CANCELAR</button>
                    <button className="btn btn-primary" onClick={handleGuardar}>GUARDAR</button>
                </div>
            </div>
        </div>
    );
}

export default EditarCliente;
