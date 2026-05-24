import { useState } from 'react';

// Formatea un string de dígitos como CUIT: 30-XXXXXXXX-X
function formatearCuit(valor) {
    // Solo dígitos
    const digits = valor.replace(/\D/g, '').substring(0, 11);

    if (digits.length <= 2) return digits;
    if (digits.length <= 10) return `${digits.slice(0, 2)}-${digits.slice(2)}`;
    return `${digits.slice(0, 2)}-${digits.slice(2, 10)}-${digits.slice(10)}`;
}

// Elimina los guiones para guardar en el backend
export function limpiarCuit(cuitFormateado) {
    return cuitFormateado.replace(/\D/g, '');
}

// Componente de input con máscara de CUIT
// Props:
//   value        → valor del form (con guiones, ej: "30-12345678-9")
//   onChange     → función que recibe el nuevo valor formateado
//   error        → mensaje de error a mostrar debajo (opcional)
//   disabled     → deshabilita el input (opcional)
function CuitInput({ value = '', onChange, error, disabled = false }) {

    const handleChange = (e) => {
        const formateado = formatearCuit(e.target.value);
        onChange(formateado);
    };

    return (
        <>
            <input
                value={value}
                onChange={handleChange}
                placeholder="30-XXXXXXXX-X"
                maxLength={13}
                disabled={disabled}
                style={error ? { borderColor: '#dc3545' } : {}}
            />
            {error && (
                <span style={{ color: '#dc3545', fontSize: '12px', marginTop: '4px', display: 'block' }}>
                    {error}
                </span>
            )}
        </>
    );
}

export default CuitInput;
