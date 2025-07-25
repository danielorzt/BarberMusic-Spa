package com.sena.barberspa.model.converter;

import com.sena.barberspa.model.enums.RolUsuario;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter para manejar roles legacy en la base de datos
 * Convierte entre String (base de datos) y RolUsuario enum
 */
@Converter(autoApply = true)
public class RolUsuarioConverter implements AttributeConverter<RolUsuario, String> {
    
    @Override
    public String convertToDatabaseColumn(RolUsuario rol) {
        if (rol == null) {
            return RolUsuario.CLIENTE.getCodigo();
        }
        return rol.getCodigo();
    }
    
    @Override
    public RolUsuario convertToEntityAttribute(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return RolUsuario.CLIENTE;
        }
        return RolUsuario.fromCodigo(codigo.trim());
    }
}