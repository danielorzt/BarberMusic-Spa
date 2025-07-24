package com.sena.barberspa.model.enums;

/**
 * Enumeración de roles según el Manual de Roles y Permisos del Sistema BarberMusic&Spa
 * 
 * Jerarquía de roles (de menor a mayor privilegio):
 * 1. CLIENTE - Usuario final y consumidor de servicios/productos
 * 2. EMPLEADO - Personal operativo que presta servicios
 * 3. ADMIN_SUCURSAL - Administrador de una sucursal específica
 * 4. GERENTE - Rol de máxima autoridad con acceso global (Super Admin)
 */
public enum RolUsuario {
    
    /**
     * CLIENTE: Rol por defecto para usuarios registrados
     * - Gestión de cuenta propia
     * - Explorar catálogo de servicios y productos
     * - Agendar citas y realizar órdenes
     * - Dejar reseñas y realizar pagos
     */
    CLIENTE("CLIENTE", "Cliente", 1),
    
    /**
     * EMPLEADO: Personal operativo (barberos, estilistas, etc.)
     * - Panel de control para gestión de actividades
     * - Consultar agendamientos asignados
     * - Gestionar órdenes y recordatorios
     */
    EMPLEADO("EMPLEADO", "Empleado", 2),
    
    /**
     * ADMIN_SUCURSAL: Encargado/administrador de sucursal específica
     * - Gestión de catálogo de su sucursal
     * - Configuración de horarios y operaciones
     * - Gestión de personal de su sucursal
     * - Moderación de reseñas
     */
    ADMIN_SUCURSAL("ADMIN_SUCURSAL", "Administrador de Sucursal", 3),
    
    /**
     * GERENTE: Super Admin con control total del sistema
     * - Acceso global a todas las sucursales
     * - Gestión completa de negocio
     * - Administración de personal global
     * - Auditoría y trazabilidad
     */
    GERENTE("GERENTE", "Gerente General", 4);
    
    private final String codigo;
    private final String descripcion;
    private final int nivelJerarquia;
    
    RolUsuario(String codigo, String descripcion, int nivelJerarquia) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivelJerarquia = nivelJerarquia;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public int getNivelJerarquia() {
        return nivelJerarquia;
    }
    
    /**
     * Verifica si este rol tiene mayor o igual jerarquía que otro rol
     */
    public boolean tienePermisoSobre(RolUsuario otroRol) {
        return this.nivelJerarquia >= otroRol.nivelJerarquia;
    }
    
    /**
     * Convierte string de BD a enum
     */
    public static RolUsuario fromCodigo(String codigo) {
        if (codigo == null) return CLIENTE;
        
        for (RolUsuario rol : values()) {
            if (rol.codigo.equalsIgnoreCase(codigo)) {
                return rol;
            }
        }
        return CLIENTE; // Valor por defecto
    }
    
    /**
     * Obtiene el siguiente rol en la jerarquía (para ascensos)
     */
    public RolUsuario getSiguienteRol() {
        switch (this) {
            case CLIENTE: return EMPLEADO;
            case EMPLEADO: return ADMIN_SUCURSAL;
            case ADMIN_SUCURSAL: return GERENTE;
            case GERENTE: return GERENTE; // Ya es el máximo
            default: return CLIENTE;
        }
    }
    
    @Override
    public String toString() {
        return codigo;
    }
}