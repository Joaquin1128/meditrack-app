import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createMedicamento } from '../../services/api';

const PRESENTACIONES = [
    'Comprimidos', 'Cápsulas', 'Ampollas', 'Solución oral',
    'Crema', 'Pomada', 'Parche', 'Supositorio', 'Colirio'
];

const FORM_INICIAL = {
    gtin: '',
    nombre: '',
    monodroga: '',
    laboratorio: '',
    presentacion: '',
    descripcion: '',
    detallesAdicionales: '',
    cantidad: '',
    unidadMedida: '',
    cadenaFrio: false,
    temperaturaMinima: '',
    temperaturaMaxima: '',
    esFragil: false,
    esControlado: false,
    volumen: '',
};

function NuevoMedicamento() {
    const navigate = useNavigate();
    const [form, setForm] = useState(FORM_INICIAL);
    const [error, setError] = useState('');
    const [preview, setPreview] = useState('');
    const [imagen, setImagen] = useState(null);

    const handleChange = e => {
        const { name, value, type, checked } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
            // Limpiar temperaturas si se desactiva cadena de frío
            ...(name === 'cadenaFrio' && !checked
                ? { temperaturaMinima: '', temperaturaMaxima: '' }
                : {}),
        }));
    };

    const handleImageChange = e => {
        const file = e.target.files[0];
        if (!file) return;
        setImagen(file);
        setPreview(URL.createObjectURL(file));
    };

    const handleGuardar = async () => {
        // Validaciones mínimas en frontend
        if (!form.gtin?.trim())        return setError('El GTIN es obligatorio.');
        if (!form.nombre?.trim())      return setError('El nombre comercial es obligatorio.');
        if (!form.monodroga?.trim())   return setError('La monodroga es obligatoria.');
        if (!form.laboratorio?.trim()) return setError('El laboratorio es obligatorio.');
        if (!form.presentacion)        return setError('La presentación es obligatoria.');

        if (form.cadenaFrio) {
            if (!form.temperaturaMinima && form.temperaturaMinima !== 0)
                return setError('Debe indicar la temperatura mínima.');
            if (!form.temperaturaMaxima && form.temperaturaMaxima !== 0)
                return setError('Debe indicar la temperatura máxima.');
            if (Number(form.temperaturaMinima) >= Number(form.temperaturaMaxima))
                return setError('La temperatura mínima debe ser menor a la máxima.');
        }

        setError('');

        try {
            const formData = new FormData();

            Object.keys(form).forEach(key => {
                if (form[key] != null && form[key] !== '')
                    formData.append(key, form[key]);
            });

            if (imagen) formData.append('imagen', imagen);

            await createMedicamento(formData);
            navigate('/medicamentos');
        } catch (err) {
            setError(err.message || 'Error al crear medicamento.');
        }
    };

    return (
        <div className="container">
            <div className="page-header">
                <h1>Nuevo medicamento</h1>
            </div>

            <div className="card">
                {error && (
                    <div style={{
                        color: '#dc3545', backgroundColor: '#f8d7da',
                        border: '1px solid #f5c6cb', padding: '10px',
                        borderRadius: '4px', marginBottom: '15px', fontWeight: 'bold'
                    }}>
                        {error}
                    </div>
                )}

                {/* Imagen y preview */}
                <div style={{
                    display: 'flex', alignItems: 'center', gap: '20px',
                    marginBottom: '30px', paddingBottom: '20px',
                    borderBottom: '1px solid #E5E7EB'
                }}>
                    <div style={{
                        width: '110px', height: '110px', borderRadius: '18px',
                        overflow: 'hidden', border: '1px solid #E5E7EB',
                        background: '#F9FAFB', display: 'flex',
                        alignItems: 'center', justifyContent: 'center'
                    }}>
                        <img
                            src={preview || 'https://placehold.co/200x200?text=%F0%9F%92%8A'}
                            alt="Medicamento"
                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                    </div>
                    <div>
                        <h2 style={{ margin: 0, fontSize: '22px', fontWeight: '700', color: '#111827' }}>
                            {form.nombre || 'Nuevo medicamento'}
                        </h2>
                        <p style={{ marginTop: '6px', color: '#6B7280' }}>
                            {form.monodroga || 'Monodroga'}
                        </p>
                        <label style={{
                            display: 'inline-block', marginTop: '12px',
                            padding: '10px 14px', background: '#10B981',
                            color: 'white', borderRadius: '10px',
                            cursor: 'pointer', fontWeight: '600', fontSize: '14px'
                        }}>
                            Subir imagen
                            <input type="file" accept="image/*" hidden onChange={handleImageChange} />
                        </label>
                    </div>
                </div>

                {/* Sección: Identificación */}
                <SectionTitle title="Identificación" />
                <div className="form-grid">
                    <div className="form-group">
                        <label>GTIN *</label>
                        <input name="gtin" value={form.gtin} onChange={handleChange}
                            placeholder="Ej: 07790001234567" />
                    </div>

                    <div className="form-group">
                        <label>Nombre comercial *</label>
                        <input name="nombre" value={form.nombre} onChange={handleChange} />
                    </div>

                    <div className="form-group">
                        <label>Monodroga *</label>
                        <input name="monodroga" value={form.monodroga} onChange={handleChange} />
                    </div>

                    <div className="form-group">
                        <label>Laboratorio titular *</label>
                        <input name="laboratorio" value={form.laboratorio} onChange={handleChange} />
                    </div>

                    <div className="form-group">
                        <label>Presentación *</label>
                        <select name="presentacion" value={form.presentacion} onChange={handleChange}>
                            <option value="">-- Seleccionar --</option>
                            {PRESENTACIONES.map(p => <option key={p} value={p}>{p}</option>)}
                        </select>
                    </div>
                </div>

                {/* Sección: Logística (cantidad/unidad a discutir con el equipo) */}
                <SectionTitle title="Logística" />
                <div className="form-grid">
                    <div className="form-group">
                        <label>Cantidad</label>
                        <input type="number" name="cantidad" value={form.cantidad}
                            onChange={handleChange} min="0" />
                    </div>

                    <div className="form-group">
                        <label>Unidad de medida</label>
                        <input name="unidadMedida" value={form.unidadMedida}
                            onChange={handleChange} placeholder="mg, ml, unidades..." />
                    </div>

                    <div className="form-group">
                        <label>Volumen (cm³)</label>
                        <input type="number" name="volumen" value={form.volumen}
                            onChange={handleChange} min="0" step="0.01"
                            placeholder="Ej: 150.00" />
                    </div>
                </div>

                {/* Sección: Características */}
                <SectionTitle title="Características" />
                <div className="form-grid">
                    <CheckboxField
                        name="esFragil"
                        checked={form.esFragil}
                        onChange={handleChange}
                        label="¿Es frágil?"
                    />
                    <CheckboxField
                        name="esControlado"
                        checked={form.esControlado}
                        onChange={handleChange}
                        label="¿Es controlado? (psicofármaco u otro regulado)"
                    />
                    <CheckboxField
                        name="cadenaFrio"
                        checked={form.cadenaFrio}
                        onChange={handleChange}
                        label="¿Requiere cadena de frío?"
                    />

                    {/* Temperaturas: solo si cadenaFrio = true */}
                    {form.cadenaFrio && (
                        <>
                            <div className="form-group">
                                <label>Temperatura mínima (°C) *</label>
                                <input type="number" name="temperaturaMinima"
                                    value={form.temperaturaMinima}
                                    onChange={handleChange} step="0.1"
                                    placeholder="Ej: 2" />
                            </div>
                            <div className="form-group">
                                <label>Temperatura máxima (°C) *</label>
                                <input type="number" name="temperaturaMaxima"
                                    value={form.temperaturaMaxima}
                                    onChange={handleChange} step="0.1"
                                    placeholder="Ej: 8" />
                            </div>
                        </>
                    )}
                </div>

                {/* Sección: Información adicional */}
                <SectionTitle title="Información adicional" />
                <div className="form-group">
                    <label>Descripción</label>
                    <textarea name="descripcion" value={form.descripcion} onChange={handleChange}
                        rows="3" placeholder="Descripción del medicamento..."
                        style={{ width: '100%', padding: '10px', borderRadius: '8px',
                            border: '1px solid #D1D5DB', resize: 'vertical' }}
                    />
                </div>
                <div className="form-group" style={{ marginTop: '12px' }}>
                    <label>Detalles adicionales</label>
                    <textarea name="detallesAdicionales" value={form.detallesAdicionales}
                        onChange={handleChange} rows="3"
                        placeholder="Instrucciones especiales, observaciones, etc."
                        style={{ width: '100%', padding: '10px', borderRadius: '8px',
                            border: '1px solid #D1D5DB', resize: 'vertical' }}
                    />
                </div>

                <div style={{
                    display: 'flex', justifyContent: 'flex-end', gap: '6px',
                    marginTop: '25px', paddingTop: '20px', borderTop: '1px solid #eee'
                }}>
                    <button className="btn btn-secondary" onClick={() => navigate('/medicamentos')}>
                        CANCELAR
                    </button>
                    <button className="btn btn-primary" onClick={handleGuardar}>
                        GUARDAR
                    </button>
                </div>
            </div>
        </div>
    );
}

// --- Componentes auxiliares ---

function SectionTitle({ title }) {
    return (
        <h3 style={{
            fontSize: '14px', fontWeight: '600', color: '#6B7280',
            textTransform: 'uppercase', letterSpacing: '0.05em',
            margin: '24px 0 12px', borderBottom: '1px solid #E5E7EB', paddingBottom: '6px'
        }}>
            {title}
        </h3>
    );
}

function CheckboxField({ name, checked, onChange, label }) {
    return (
        <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <input type="checkbox" id={name} name={name} checked={checked} onChange={onChange}
                style={{ width: '18px', height: '18px', cursor: 'pointer' }} />
            <label htmlFor={name} style={{ margin: 0, cursor: 'pointer' }}>{label}</label>
        </div>
    );
}

export default NuevoMedicamento;
