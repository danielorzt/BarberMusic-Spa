package com.sena.barberspa.model.converter;

import com.sena.barberspa.model.enums.EstadoOrden;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter para manejar estados de orden en la base de datos
 */
@Converter(autoApply = true)
public class EstadoOrdenConverter implements AttributeConverter<EstadoOrden, String> {
    
    @Override
    public String convertToDatabaseColumn(EstadoOrden estado) {
        if (estado == null) {
            return EstadoOrden.PENDIENTE_PAGO.getCodigo();
        }
        return estado.getCodigo();
    }
    
    @Override
    public EstadoOrden convertToEntityAttribute(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return EstadoOrden.PENDIENTE_PAGO;
        }
        return EstadoOrden.fromCodigo(codigo.trim());
    }
}