package com.sena.barberspa.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RoleTestController {

    @GetMapping("/test/admin-general")
    @PreAuthorize("hasRole('ADMIN_GENERAL')")
    @ResponseBody
    public String testAdminGeneral() {
        return "✅ Acceso ADMIN_GENERAL correcto";
    }

    @GetMapping("/test/admin-sucursal")
    @PreAuthorize("hasRole('ADMIN_SUCURSAL')")
    @ResponseBody
    public String testAdminSucursal() {
        return "✅ Acceso ADMIN_SUCURSAL correcto";
    }

    @GetMapping("/test/empleado")
    @PreAuthorize("hasRole('EMPLEADO')")
    @ResponseBody
    public String testEmpleado() {
        return "✅ Acceso EMPLEADO correcto";
    }

    @GetMapping("/test/cliente")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseBody
    public String testCliente() {
        return "✅ Acceso CLIENTE correcto";
    }

    @GetMapping("/test/any-admin")
    @PreAuthorize("hasAnyRole('ADMIN_GENERAL', 'ADMIN_SUCURSAL')")
    @ResponseBody
    public String testAnyAdmin() {
        return "✅ Acceso cualquier ADMIN correcto";
    }

    @GetMapping("/test/all-roles")
    @ResponseBody
    public String testAllRoles() {
        return "✅ Endpoint público - accesible para todos los usuarios autenticados";
    }
}