import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
//import { getClienteById, updateCliente } from '../../services/api';

function EditarCliente() {

    const { id } = useParams();
    const navigate = useNavigate();

    const [form, setForm] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {

        /*getClienteById(id)
            .then(setForm)
            .catch(() =>
                setError('Error al cargar datos del cliente.')
            );*/

    }, [id]);

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    const handleGuardar = async () => {

        try {

            //await updateCliente(id, form);

            navigate('/clientes');

        } catch (err) {

            setError(err.message || 'Error al actualizar cliente.');
        }
    };

    if (!form)
        return <div className="container">Cargando...</div>;

    return (
        <div className="container">

            <div className="page-header">
                <h1>Editar cliente</h1>
            </div>

            <div className="card">

                {error && (
                    <div
                        style={{
                            color: '#dc3545',
                            backgroundColor: '#f8d7da',
                            border: '1px solid #f5c6cb',
                            padding: '10px',
                            borderRadius: '4px',
                            marginBottom: '15px',
                            fontWeight: 'bold'
                        }}
                    >
                        {error}
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
                            width: '110px',
                            height: '110px',
                            borderRadius: '50%',
                            background: '#DCFCE7',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '34px',
                            fontWeight: '700',
                            color: '#166534',
                            border: '1px solid #BBF7D0'
                        }}
                    >
                        {form.nombre?.charAt(0)}
                        {form.apellido?.charAt(0)}
                    </div>

                    <div>

                        <h2
                            style={{
                                margin: 0,
                                fontSize: '22px',
                                fontWeight: '700',
                                color: '#111827'
                            }}
                        >
                            {form.nombre} {form.apellido}
                        </h2>

                        <p
                            style={{
                                marginTop: '6px',
                                color: '#6B7280'
                            }}
                        >
                            DNI: {form.dni || 'Sin DNI'}
                        </p>

                    </div>
                </div>

                <div className="form-grid">

                    <div className="form-group form-full">
                        <label>ID</label>

                        <input
                            value={form.id}
                            disabled
                            className="input-locked"
                        />
                    </div>

                    <div className="form-group">
                        <label>Nombre *</label>

                        <input
                            name="nombre"
                            value={form.nombre || ''}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label>Apellido *</label>

                        <input
                            name="apellido"
                            value={form.apellido || ''}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label>DNI *</label>

                        <input
                            name="dni"
                            value={form.dni || ''}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label>Email</label>

                        <input
                            type="email"
                            name="email"
                            value={form.email || ''}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label>Teléfono</label>

                        <input
                            name="telefono"
                            value={form.telefono || ''}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label>Fecha de nacimiento</label>

                        <input
                            type="date"
                            name="fechaNacimiento"
                            value={form.fechaNacimiento || ''}
                            onChange={handleChange}
                        />
                    </div>

                    <div
                        className="form-group"
                        style={{ gridColumn: '1 / -1' }}
                    >
                        <label>Dirección</label>

                        <input
                            name="direccion"
                            value={form.direccion || ''}
                            onChange={handleChange}
                        />
                    </div>

                </div>

                <div
                    className="form-group"
                    style={{ marginTop: '20px' }}
                >

                    <label>Observaciones</label>

                    <textarea
                        name="observaciones"
                        value={form.observaciones || ''}
                        onChange={handleChange}
                        rows="4"
                        placeholder="Observaciones del cliente..."
                        style={{
                            width: '100%',
                            padding: '10px',
                            borderRadius: '8px',
                            border: '1px solid #D1D5DB',
                            resize: 'vertical'
                        }}
                    />
                </div>

                <div
                    style={{
                        display: 'flex',
                        justifyContent: 'flex-end',
                        gap: '6px',
                        marginTop: '25px',
                        paddingTop: '20px',
                        borderTop: '1px solid #eee'
                    }}
                >

                    <button
                        className="btn btn-secondary"
                        onClick={() => navigate('/clientes')}
                    >
                        CANCELAR
                    </button>

                    <button
                        className="btn btn-primary"
                        onClick={handleGuardar}
                    >
                        GUARDAR
                    </button>

                </div>

            </div>
        </div>
    );
}

export default EditarCliente;