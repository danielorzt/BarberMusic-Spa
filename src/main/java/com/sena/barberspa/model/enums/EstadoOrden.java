package com.sena.barberspa.model.enums;

/**
 * Estados de órdenes de compra según la lógica de negocio BarberMusic&Spa
 */
public enum EstadoOrden {
    
    /**
     * PENDIENTE_PAGO: Orden creada pero sin pago
     * - Estado inicial al crear la orden
     * - Cliente debe completar el pago
     */
    PENDIENTE_PAGO("PENDIENTE_PAGO", "Pendiente de Pago", 1),
    
    /**
     * PAGADA: Pago confirmado y procesado
     * - Pago recibido y verificado
     * - Lista para procesamiento
     */
    PAGADA("PAGADA", "Pagada", 2),
    
    /**
     * EN_PREPARACION: Orden siendo preparada
     * - Productos siendo empacados
     * - Personal trabajando en la orden
     */
    EN_PREPARACION("EN_PREPARACION", "En Preparación", 3),
    
    /**
     * EN_PROCESO: Sinónimo de EN_PREPARACION para compatibilidad con BD
     */
    EN_PROCESO("EN_PROCESO", "En Proceso", 3),
    
    /**
     * LISTA_ENTREGA: Orden lista para entrega
     * - Productos empacados y listos
     * - Esperando recogida o envío
     */
    LISTA_ENTREGA("LISTA_ENTREGA", "Lista para Entrega", 4),
    
    /**
     * EN_TRANSITO: Orden en camino al cliente
     * - Enviada por delivery o mensajería
     * - Cliente puede rastrear el envío
     */
    EN_TRANSITO("EN_TRANSITO", "En Tránsito", 5),
    
    /**
     * ENTREGADA: Orden entregada exitosamente
     * - Cliente recibió los productos
     * - Proceso completado
     */
    ENTREGADA("ENTREGADA", "Entregada", 6),
    
    /**
     * CANCELADA: Orden cancelada
     * - Cancelada por cliente o establecimiento
     * - Reembolso procesado si aplica
     */
    CANCELADA("CANCELADA", "Cancelada", 0),
    
    /**
     * DEVUELTA: Orden devuelta por el cliente
     * - Cliente devolvió productos
     * - Procesando reembolso
     */
    DEVUELTA("DEVUELTA", "Devuelta", 0);
    
    private final String codigo;
    private final String descripcion;
    private final int orden;
    
    EstadoOrden(String codigo, String descripcion, int orden) {
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
    public static EstadoOrden fromCodigo(String codigo) {
        if (codigo == null) return PENDIENTE_PAGO;
        
        for (EstadoOrden estado : values()) {
            if (estado.codigo.equalsIgnoreCase(codigo)) {
                return estado;
            }
        }
        return PENDIENTE_PAGO; // Estado por defecto
    }
    
    /**
     * Verifica si el estado permite cancelación
     */
    public boolean permiteCancelacion() {
        return this == PENDIENTE_PAGO || this == PAGADA || this == EN_PREPARACION;
    }
    
    /**
     * Verifica si el estado permite modificación
     */
    public boolean permiteModificacion() {
        return this == PENDIENTE_PAGO;
    }
    
    /**
     * Verifica si la orden está en proceso activo
     */
    public boolean isEnProceso() {
        return this == EN_PREPARACION || this == EN_PROCESO || 
               this == LISTA_ENTREGA || this == EN_TRANSITO;
    }
    
    /**
     * Verifica si la orden está finalizada
     */
    public boolean isFinalizada() {
        return this == ENTREGADA || this == CANCELADA || this == DEVUELTA;
    }
    
    /**
     * Verifica si requiere pago
     */
    public boolean requierePago() {
        return this == PENDIENTE_PAGO;
    }
    
    @Override
    public String toString() {
        return codigo;
    }
}