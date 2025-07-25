package com.sena.barberspa.service;

import com.sena.barberspa.model.Agendamiento;
import com.sena.barberspa.model.Recordatorio;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.repository.IRecordatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecordatorioServiceImplement implements IRecordatorioService {

    @Autowired
    private IRecordatorioRepository recordatorioRepository;

    @Override
    @Transactional
    public Recordatorio save(Recordatorio recordatorio) {
        return recordatorioRepository.save(recordatorio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recordatorio> get(Long id) {
        return recordatorioRepository.findById(id);
    }

    @Override
    @Transactional
    public void update(Recordatorio recordatorio) {
        recordatorioRepository.save(recordatorio);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        recordatorioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Optional<Recordatorio> recordatorioOpt = recordatorioRepository.findById(id);
        if (recordatorioOpt.isPresent()) {
            Recordatorio recordatorio = recordatorioOpt.get();
            recordatorio.setActivo(false);
            recordatorioRepository.save(recordatorio);
        }
    }

    @Override
    @Transactional
    public void cambiarFijado(Long id) {
        Optional<Recordatorio> recordatorioOpt = recordatorioRepository.findById(id);
        if (recordatorioOpt.isPresent()) {
            Recordatorio recordatorio = recordatorioOpt.get();
            recordatorio.setFijado(!recordatorio.isFijado());
            recordatorioRepository.save(recordatorio);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recordatorio> findAll() {
        return recordatorioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recordatorio> findByUsuario(Usuario usuario) {
        return recordatorioRepository.findByUsuarioAndActivoTrueOrderByFijadoDescFechaHoraAsc(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recordatorio> findProximosRecordatorios(Usuario usuario, int dias) {
        LocalDateTime fechaLimite = LocalDateTime.now().plusDays(dias);
        return recordatorioRepository.findProximosRecordatorios(usuario, LocalDateTime.now(), fechaLimite);
    }

    @Override
    @Transactional
    public Recordatorio crearRecordatorioDeAgendamiento(Agendamiento agendamiento, Usuario usuario) {
        // Verificar si ya existe un recordatorio para este agendamiento
        List<Recordatorio> existentes = recordatorioRepository.findByAgendamientoIdAndActivoTrue(agendamiento.getId());
        if (!existentes.isEmpty()) {
            return existentes.get(0); // Devolver el existente si ya hay uno
        }

        // Crear nuevo recordatorio
        Recordatorio recordatorio = new Recordatorio(agendamiento, usuario);
        return recordatorioRepository.save(recordatorio);
    }

    @Override
    @Transactional
    public void procesarAgendamientosProximos(Usuario usuario, int dias) {
        LocalDateTime fechaLimite = LocalDateTime.now().plusDays(dias);
        // Por ahora, simplemente creamos recordatorios para los próximos días
        // En una implementación real, necesitarías obtener los agendamientos del
        // usuario
        List<Recordatorio> recordatoriosExistentes = findByUsuario(usuario);

        // Aquí podrías implementar la lógica para crear recordatorios basados en
        // agendamientos
        // Por ahora, solo retornamos sin hacer nada específico
    }
}