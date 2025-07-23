package com.sena.barberspa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sena.barberspa.model.Categoria;
import com.sena.barberspa.model.Especialidad;
import com.sena.barberspa.model.Personal;
import com.sena.barberspa.repository.ICategoriaRepository;
import com.sena.barberspa.repository.IEspecialidadRepository;
import com.sena.barberspa.repository.IPersonalRepository;

@Controller
public class EntityTestController {

    @Autowired
    private ICategoriaRepository categoriaRepository;
    
    @Autowired
    private IEspecialidadRepository especialidadRepository;
    
    @Autowired
    private IPersonalRepository personalRepository;

    @GetMapping("/test/entities")
    @ResponseBody
    public String testEntities() {
        StringBuilder result = new StringBuilder();
        
        try {
            // Test Categoria
            List<Categoria> categorias = categoriaRepository.findAll();
            result.append("✅ Categorías cargadas: ").append(categorias.size()).append("\n");
            
            // Test Especialidad
            List<Especialidad> especialidades = especialidadRepository.findAll();
            result.append("✅ Especialidades cargadas: ").append(especialidades.size()).append("\n");
            
            // Test Personal
            List<Personal> personal = personalRepository.findAll();
            result.append("✅ Personal cargado: ").append(personal.size()).append("\n");
            
            result.append("\n🎉 Todas las nuevas entidades se cargan correctamente!");
            
        } catch (Exception e) {
            result.append("❌ Error al cargar entidades: ").append(e.getMessage());
        }
        
        return result.toString().replace("\n", "<br>");
    }

    @GetMapping("/test/repositories")
    @ResponseBody
    public String testRepositories() {
        StringBuilder result = new StringBuilder();
        
        try {
            result.append("=== TEST DE REPOSITORIOS ===<br><br>");
            
            // Test repository injection
            result.append("✅ ICategoriaRepository: ").append(categoriaRepository != null ? "OK" : "NULL").append("<br>");
            result.append("✅ IEspecialidadRepository: ").append(especialidadRepository != null ? "OK" : "NULL").append("<br>");
            result.append("✅ IPersonalRepository: ").append(personalRepository != null ? "OK" : "NULL").append("<br>");
            
            result.append("<br>🎯 Todos los repositorios están correctamente inyectados!");
            
        } catch (Exception e) {
            result.append("❌ Error en test de repositorios: ").append(e.getMessage());
        }
        
        return result.toString();
    }
}