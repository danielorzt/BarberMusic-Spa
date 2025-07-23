package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.barberspa.model.Personal;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.repository.IPersonalRepository;

@Service
public class PersonalServiceImplement implements IPersonalService {

    @Autowired
    private IPersonalRepository personalRepository;

    @Override
    public List<Personal> findAll() {
        return personalRepository.findAll();
    }

    @Override
    public Optional<Personal> findById(Long id) {
        return personalRepository.findById(id);
    }

    @Override
    public Personal save(Personal personal) {
        return personalRepository.save(personal);
    }

    @Override
    public void delete(Personal personal) {
        personalRepository.delete(personal);
    }

    @Override
    public Optional<Personal> findByUsuario(Usuario usuario) {
        return personalRepository.findByUsuario(usuario);
    }

    @Override
    public List<Personal> findBySucursalId(Long sucursalId) {
        return personalRepository.findBySucursalIdAndEstadoActivo(sucursalId);
    }

    @Override
    public boolean isUsuarioEmpleado(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        
        // Verificar si el rol es EMPLEADO, ADMIN_SUCURSAL o ADMIN_GENERAL
        String rol = usuario.getRol();
        if ("EMPLEADO".equals(rol) || "ADMIN_SUCURSAL".equals(rol) || "ADMIN_GENERAL".equals(rol)) {
            // Para EMPLEADO, verificar que tenga registro en la tabla personal
            if ("EMPLEADO".equals(rol)) {
                return personalRepository.findByUsuario(usuario).isPresent();
            }
            return true; // ADMIN_SUCURSAL y ADMIN_GENERAL no requieren registro en personal
        }
        
        return false;
    }

    @Override
    public boolean validateEmployeeAccess(Usuario usuario, Long sucursalId) {
        if (usuario == null || sucursalId == null) {
            return false;
        }
        
        String rol = usuario.getRol();
        
        // ADMIN_GENERAL tiene acceso a todas las sucursales
        if ("ADMIN_GENERAL".equals(rol)) {
            return true;
        }
        
        // ADMIN_SUCURSAL y EMPLEADO deben tener registro en personal para la sucursal específica
        if ("ADMIN_SUCURSAL".equals(rol) || "EMPLEADO".equals(rol)) {
            Optional<Personal> personal = personalRepository.findByUsuario(usuario);
            return personal.isPresent() && 
                   personal.get().getSucursalAsignada().getId().equals(sucursalId) &&
                   personal.get().getActivoEnEmpresa();
        }
        
        return false;
    }
}