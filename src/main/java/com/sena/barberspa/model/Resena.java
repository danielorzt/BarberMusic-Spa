package com.sena.barberspa.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa las reseñas de usuarios
 * Permite a los clientes calificar y comentar productos, servicios y personal
 * Implementa polimorfismo para soportar diferentes tipos de elementos reseñables
 * según el Manual de Roles BarberMusic&Spa
 */
@Entity
@Table(name = "reseñas")
public class Resena {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_usuario_id", nullable = false)
    @JsonIgnore
    private Usuario clienteUsuario;
    
    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(name = "calificacion", nullable = false)
    private Integer calificacion;
    
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;
    
    @NotNull(message = "El ID del elemento reseñable es requerido")
    @Column(name = "reseñable_id", nullable = false)
    private Long resenableId;
    
    @NotNull(message = "El tipo de elemento reseñable es requerido")
    @Column(name = "reseñable_type", nullable = false, length = 255)
    private String resenableType;
    
    @Column(name = "aprobada", nullable = false)
    private Boolean aprobada = true;
    
    @Column(name = "fecha_reseña", nullable = false)
    private LocalDateTime fechaResena;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructores
    public Resena() {
        this.fechaResena = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.aprobada = true;
    }
    
    public Resena(Usuario clienteUsuario, Integer calificacion, String comentario, Long resenableId, String resenableType) {
        this();
        this.clienteUsuario = clienteUsuario;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.resenableId = resenableId;
        this.resenableType = resenableType;
    }
    
    // Constructores específicos para tipos de reseñables
    public static Resena paraProducto(Usuario usuario, Integer calificacion, String comentario, Producto producto) {
        return new Resena(usuario, calificacion, comentario, producto.getId(), "Producto");
    }
    
    public static Resena paraServicio(Usuario usuario, Integer calificacion, String comentario, Servicio servicio) {
        return new Resena(usuario, calificacion, comentario, servicio.getId(), "Servicio");
    }
    
    public static Resena paraPersonal(Usuario usuario, Integer calificacion, String comentario, Personal personal) {
        return new Resena(usuario, calificacion, comentario, personal.getId(), "Personal");
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Usuario getClienteUsuario() {
        return clienteUsuario;
    }
    
    public void setClienteUsuario(Usuario clienteUsuario) {
        this.clienteUsuario = clienteUsuario;
    }
    
    public Integer getCalificacion() {
        return calificacion;
    }
    
    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getComentario() {
        return comentario;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getResenableId() {
        return resenableId;
    }
    
    public void setResenableId(Long resenableId) {
        this.resenableId = resenableId;
    }
    
    public String getResenableType() {
        return resenableType;
    }
    
    public void setResenableType(String resenableType) {
        this.resenableType = resenableType;
    }
    
    public Boolean getAprobada() {
        return aprobada;
    }
    
    public void setAprobada(Boolean aprobada) {
        this.aprobada = aprobada;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getFechaResena() {
        return fechaResena;
    }
    
    public void setFechaResena(LocalDateTime fechaResena) {
        this.fechaResena = fechaResena;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Métodos utilitarios
    public boolean esProducto() {
        return "Producto".equalsIgnoreCase(resenableType);
    }
    
    public boolean esServicio() {
        return "Servicio".equalsIgnoreCase(resenableType);
    }
    
    public boolean esPersonal() {
        return "Personal".equalsIgnoreCase(resenableType);
    }
    
    public String getTipoResenable() {
        return resenableType != null ? resenableType : "Desconocido";
    }
    
    /**
     * Obtiene las estrellas para mostrar en UI
     */
    public String getEstrellas() {
        StringBuilder estrellas = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= calificacion) {
                estrellas.append("★");
            } else {
                estrellas.append("☆");
            }
        }
        return estrellas.toString();
    }
    
    /**
     * Verifica si la reseña tiene comentario
     */
    public boolean tieneComentario() {
        return comentario != null && !comentario.trim().isEmpty();
    }
    
    /**
     * Obtiene un resumen corto del comentario
     */
    public String getComentarioResumen(int maxLength) {
        if (!tieneComentario()) {
            return "";
        }
        if (comentario.length() <= maxLength) {
            return comentario;
        }
        return comentario.substring(0, maxLength) + "...";
    }
    
    /**
     * Verifica si el usuario puede editar esta reseña
     */
    public boolean puedeEditar(Usuario usuario) {
        return clienteUsuario != null && clienteUsuario.getId().equals(usuario.getId());
    }
    
    @Override
    public String toString() {
        return "Resena{" +
                "id=" + id +
                ", clienteUsuario=" + (clienteUsuario != null ? clienteUsuario.getNombre() : "null") +
                ", calificacion=" + calificacion +
                ", resenableType='" + resenableType + '\'' +
                ", resenableId=" + resenableId +
                ", aprobada=" + aprobada +
                ", fechaResena=" + fechaResena +
                '}';
    }
}