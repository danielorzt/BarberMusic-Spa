package com.sena.barberspa.model.converter;

import com.sena.barberspa.model.enums.EstadoAgendamiento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter para manejar estados de agendamiento en la base de datos
 */
@Converter(autoApply = true)
public class EstadoAgendamientoConverter implements AttributeConverter<EstadoAgendamiento, String> {
    
    @Override
    public String convertToDatabaseColumn(EstadoAgendamiento estado) {
        if (estado == null) {
            return EstadoAgendamiento.PROGRAMADA.getCodigo();
        }
        return estado.getCodigo();
    }
    
    @Override
    public EstadoAgendamiento convertToEntityAttribute(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return EstadoAgendamiento.PROGRAMADA;
        }
        return EstadoAgendamiento.fromCodigo(codigo.trim());
    }
}