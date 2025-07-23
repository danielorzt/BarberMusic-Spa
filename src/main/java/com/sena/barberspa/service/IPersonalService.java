package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import com.sena.barberspa.model.Personal;
import com.sena.barberspa.model.Usuario;

public interface IPersonalService {
    
    List<Personal> findAll();
    
    Optional<Personal> findById(Long id);
    
    Personal save(Personal personal);
    
    void delete(Personal personal);
    
    Optional<Personal> findByUsuario(Usuario usuario);
    
    List<Personal> findBySucursalId(Long sucursalId);
    
    boolean isUsuarioEmpleado(Usuario usuario);
    
    boolean validateEmployeeAccess(Usuario usuario, Long sucursalId);
}