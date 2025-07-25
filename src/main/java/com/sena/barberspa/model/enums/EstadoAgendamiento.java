package com.sena.barberspa.model.enums;

/**
 * Estados de agendamientos según la lógica de negocio BarberMusic&Spa
 */
public enum EstadoAgendamiento {
    
    /**
     * PROGRAMADA: Cita agendada pero no confirmada
     * - Estado inicial cuando el cliente reserva
     * - Puede ser cancelada sin penalización
     */
    PROGRAMADA("PROGRAMADA", "Programada", 1),
    
    /**
     * CONFIRMADA: Cita confirmada por el cliente o personal
     * - Cliente ha confirmado asistencia
     * - Personal asignado y disponible
     */
    CONFIRMADA("CONFIRMADA", "Confirmada", 2),
    
    /**
     * EN_PROCESO: Servicio en ejecución
     * - Cliente presente, servicio iniciado
     * - No se puede cancelar
     */
    EN_PROCESO("EN_PROCESO", "En Proceso", 3),
    
    /**
     * COMPLETADA: Servicio finalizado exitosamente
     * - Servicio terminado
     * - Cliente satisfecho
     */
    COMPLETADA("COMPLETADA", "Completada", 4),
    
    /**
     * CANCELADA: Cita cancelada
     * - Cancelada por cliente o establecimiento
     * - Puede tener penalizaciones según política
     */
    CANCELADA("CANCELADA", "Cancelada", 0),
    
    /**
     * NO_PRESENTADO: Cliente no se presentó
     * - Cliente no llegó a la cita
     * - Puede tener penalizaciones
     */
    NO_PRESENTADO("NO_PRESENTADO", "No se presentó", 0);
    
    private final String codigo;
    private final String descripcion;
    private final int orden;
    
    EstadoAgendamiento(String codigo, String descripcion, int orden) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.orden = orden;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public int getOrden() {
        return orden;
    }
    
    /**
     * Convierte string de BD a enum
     */
    public static EstadoAgendamiento fromCodigo(String codigo) {
        if (codigo == null) return PROGRAMADA;
        
        for (EstadoAgendamiento estado : values()) {
            if (estado.codigo.equalsIgnoreCase(codigo)) {
                return estado;
            }
        }
        return PROGRAMADA; // Estado por defecto
    }
    
    /**
     * Verifica si el estado permite cancelación
     */
    public boolean permiteCancelacion() {
        return this == PROGRAMADA || this == CONFIRMADA;
    }
    
    /**
     * Verifica si el estado permite modificación
     */
    public boolean permiteModificacion() {
        return this == PROGRAMADA;
    }
    
    /**
     * Verifica si el estado está activo (no finalizado)
     */
    public boolean isActivo() {
        return this != COMPLETADA && this != CANCELADA && this != NO_PRESENTADO;
    }
    
    @Override
    public String toString() {
        return codigo;
    }
}