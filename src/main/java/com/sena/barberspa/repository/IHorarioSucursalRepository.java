package com.sena.barberspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sena.barberspa.model.HorarioSucursal;
import com.sena.barberspa.model.Sucursal;

@Repository
public interface IHorarioSucursalRepository extends JpaRepository<HorarioSucursal, Long> {
    
    List<HorarioSucursal> findBySucursal(Sucursal sucursal);
    
    List<HorarioSucursal> findByDiaSemana(Integer diaSemana);
    
    List<HorarioSucursal> findByEstaCerradoRegularmente(Boolean estaCerradoRegularmente);
    
    @Query("SELECT h FROM HorarioSucursal h WHERE h.sucursal.id = :sucursalId AND h.estaCerradoRegularmente = false")
    List<HorarioSucursal> findBySucursalIdAndEstadoActivo(@Param("sucursalId") Integer sucursalId);
    
    @Query("SELECT h FROM HorarioSucursal h WHERE h.sucursal.id = :sucursalId AND h.diaSemana = :dia AND h.estaCerradoRegularmente = false")
    List<HorarioSucursal> findBySucursalIdAndDiaAndEstadoActivo(@Param("sucursalId") Integer sucursalId, @Param("dia") Integer dia);
    
    // Convenience methods for backward compatibility
    default List<HorarioSucursal> findByDiaSemana(String diaSemana) {
        try {
            Integer dia = Integer.parseInt(diaSemana);
            return findByDiaSemana(dia);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }
    
    default List<HorarioSucursal> findByEstado(String estado) {
        Boolean cerrado = !"activo".equalsIgnoreCase(estado);
        return findByEstaCerradoRegularmente(cerrado);
    }
}