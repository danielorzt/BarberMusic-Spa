package com.sena.barberspa.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "horarios_sucursal")
public class HorarioSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(name = "dia_semana")
    private Integer diaSemana; // 0 = Domingo, 1 = Lunes, ... 6 = Sábado

    @Column(name = "hora_apertura")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;

    @Column(name = "esta_cerrado_regularmente")
    private Boolean estaCerradoRegularmente;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public HorarioSucursal() {
    }

    public HorarioSucursal(Sucursal sucursal, Integer diaSemana, LocalTime horaApertura, LocalTime horaCierre,
            Boolean estaCerradoRegularmente) {
        this.sucursal = sucursal;
        this.diaSemana = diaSemana;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.estaCerradoRegularmente = estaCerradoRegularmente;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public Boolean getEstaCerradoRegularmente() {
        return estaCerradoRegularmente;
    }

    public void setEstaCerradoRegularmente(Boolean estaCerradoRegularmente) {
        this.estaCerradoRegularmente = estaCerradoRegularmente;
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

    @Override
    public String toString() {
        return "HorarioSucursal{" +
                "id=" + id +
                ", diaSemana=" + diaSemana +
                ", horaApertura=" + horaApertura +
                ", horaCierre=" + horaCierre +
                ", estaCerradoRegularmente=" + estaCerradoRegularmente +
                '}';
    }
}