import React from 'react';

const ModalHistorialUsuario = ({ historial, alCerrar }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-content" style={{ maxWidth: '800px', width: '90%' }} >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h2 style={{ margin: 0 }}>Historial de Operaciones</h2>
                    <button
                        onClick={alCerrar}
                        style={{ background: 'none', border: 'none', fontSize: '24px', cursor: 'pointer', color: '#6B7280' }}>
                        ✕
                    </button>
                </div>

                <div style={{ maxHeight: '400px', overflowY: 'auto', border: '1px solid #E5E7EB', borderRadius: '8px' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                        <thead style={{ backgroundColor: '#F9FAFB', position: 'sticky', top: 0 }}>
                            <tr>
                                <th style={thStyle}>Campo modificado</th>
                                <th style={thStyle}>Valor anterior</th>
                                <th style={thStyle}>Valor actual</th>
                                <th style={thStyle}>Fecha / Hora</th>
                                <th style={thStyle}>Autor</th>
                            </tr>
                        </thead>

                        <tbody>
                            {historial.length > 0 ? (
                                historial.slice().reverse()
                                    .map((item, index) => {
                                        return (
                                            <tr key={index}>
                                                <td style={tdStyle}>
                                                    {item.campo || 'Estado'}
                                                </td>

                                                <td style={tdStyle}>
                                                    {item.valorAnterior || '-'}
                                                </td>

                                                <td style={tdStyle}>
                                                    {item.valorActual || item.estado?.replace(/_/g, ' ') || '-'}
                                                </td>

                                                <td style={tdStyle}>
                                                    {item.fecha}
                                                    <span style={{ color: '#9CA3AF', marginLeft: '5px' }}>
                                                        {item.hora}
                                                    </span>
                                                </td>

                                                <td style={{ ...tdStyle, fontWeight: '500' }}>
                                                    {item.usuario || 'Sistema'}
                                                </td>
                                            </tr>
                                        );
                                    })
                            ) : (
                                <tr>
                                    <td colSpan="4" style={{padding: '20px',textAlign: 'center',color: '#9CA3AF'}}>
                                        No hay registros en el historial
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

                <div style={{ marginTop: '20px', display: 'flex', justifyContent: 'flex-end' }}>
                    <button className="btn btn-secondary" onClick={alCerrar}>
                        Cerrar historial
                    </button>
                </div>
            </div>
        </div>
    );
};

const thStyle = {
    padding: '12px',
    fontSize: '12px',
    color: '#374151',
    textTransform: 'uppercase',
    borderBottom: '1px solid #E5E7EB'
};

const tdStyle = {
    padding: '12px',
    fontSize: '14px',
    color: '#374151'
};

export default ModalHistorialUsuario;