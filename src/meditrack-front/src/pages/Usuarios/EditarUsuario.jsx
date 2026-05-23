import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getUsuarioById, updateUsuario } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

function FieldError({ errores, campo }) {
    if (!errores?.[campo]) return null;
    return (
        <span style={{ color: '#dc3545', fontSize: '12px', marginTop: '4px', display: 'block' }}>
            {errores[campo]}
        </span>
    );
}

function EditarUsuario() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [form, setForm] = useState(null);
    const [erroresApi, setErroresApi] = useState({});
    const [errorGeneral, setErrorGeneral] = useState('');

    useEffect(() => {
        getUsuarioById(id)
            .then(data => setForm({ ...data, password: '' }))
            .catch(() => setErrorGeneral('Error al cargar datos del usuario.'));
    }, [id]);

    const getRolesPermitidos = (currentUserRole) => {
        switch (currentUserRole) {
            case 'ADMINISTRADOR': return ['ADMINISTRADOR', 'SUPERVISOR', 'OPERADOR', 'REPARTIDOR'];
            case 'SUPERVISOR': return ['SUPERVISOR', 'OPERADOR', 'REPARTIDOR'];
            case 'OPERADOR': return ['OPERADOR', 'REPARTIDOR'];
            default: return [];
        }
    };

    const rolesPermitidos = getRolesPermitidos(user?.role);

    const handleChange = e => {
        setForm({ ...form, [e.target.name]: e.target.value });
        if (erroresApi[e.target.name]) {
            setErroresApi({ ...erroresApi, [e.target.name]: undefined });
        }
    };

    const handleGuardar = async () => {
        setErroresApi({});
        setErrorGeneral('');

        try {
            await updateUsuario(id, form);
            navigate('/usuarios');
        } catch (err) {
            if (err.type === 'validation') {
                setErroresApi(err.errores);
            } else {
                setErrorGeneral(err.message || 'Error al actualizar usuario.');
            }
        }
    };

    if (!form) return <div className="container">Cargando...</div>;

    return (
        <div className="container">
            <div className="page-header">
                <h1>Editar usuario</h1>
            </div>

            <div className="card">
                {errorGeneral && (
                    <div style={{ color: '#dc3545', backgroundColor: '#f8d7da', border: '1px solid #f5c6cb', padding: '10px', borderRadius: '4px', marginBottom: '15px', fontWeight: 'bold' }}>
                        {errorGeneral}
                    </div>
                )}

                <div className="form-grid">
                    <div className="form-group form-full">
                        <label>ID de Usuario</label>
                        <input value={form.id} disabled className="input-locked" />
                    </div>

                    <div className="form-group">
                        <label>Nombre completo *</label>
                        <input
                            name="nombre"
                            value={form.nombre || ''}
                            onChange={handleChange}
                            style={erroresApi.nombre ? { borderColor: '#dc3545' } : {}}
                        />
                        <FieldError errores={erroresApi} campo="nombre" />
                    </div>

                    <div className="form-group">
                        <label>Email *</label>
                        <input
                            type="email"
                            name="email"
                            value={form.email || ''}
                            onChange={handleChange}
                            style={erroresApi.email ? { borderColor: '#dc3545' } : {}}
                        />
                        <FieldError errores={erroresApi} campo="email" />
                    </div>

                    <div className="form-group">
                        <label>DNI *</label>
                        <input
                            name="dni"
                            value={form.dni || ''}
                            onChange={handleChange}
                            placeholder="Sin puntos ni espacios"
                            style={erroresApi.dni ? { borderColor: '#dc3545' } : {}}
                        />
                        <FieldError errores={erroresApi} campo="dni" />
                    </div>

                    <div className="form-group">
                        <label>Rol *</label>
                        <select
                            name="role"
                            value={form.role || ''}
                            onChange={handleChange}
                            style={{ width: '100%', padding: '10px', borderRadius: '6px', border: erroresApi.role ? '1px solid #dc3545' : '1px solid #ddd' }}
                        >
                            <option value="">-- Seleccionar Rol --</option>
                            {rolesPermitidos.map(rol => (
                                <option key={rol} value={rol}>{rol}</option>
                            ))}
                        </select>
                        <FieldError errores={erroresApi} campo="role" />
                    </div>

                    <div className="form-group">
                        <label>Nueva Contraseña (Opcional)</label>
                        <input
                            type="password"
                            name="password"
                            value={form.password || ''}
                            onChange={handleChange}
                            placeholder="Dejar en blanco para mantener la actual"
                            style={erroresApi.password ? { borderColor: '#dc3545' } : {}}
                        />
                        <FieldError errores={erroresApi} campo="password" />
                    </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '6px', marginTop: '25px', paddingTop: '20px', borderTop: '1px solid #eee' }}>
                    <button className="btn btn-secondary" onClick={() => navigate('/usuarios')}>CANCELAR</button>
                    <button className="btn btn-primary" onClick={handleGuardar}>GUARDAR</button>
                </div>
            </div>
        </div>
    );
}

export default EditarUsuario;