package com.sena.barberspa.service;

import com.sena.barberspa.model.Agendamiento;
import com.sena.barberspa.model.Recordatorio;
import com.sena.barberspa.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IRecordatorioService {
    Recordatorio save(Recordatorio recordatorio);

    Optional<Recordatorio> get(Long id);

    void update(Recordatorio recordatorio);

    void delete(Long id);

    void desactivar(Long id);

    void cambiarFijado(Long id);

    List<Recordatorio> findAll();

    List<Recordatorio> findByUsuario(Usuario usuario);

    List<Recordatorio> findProximosRecordatorios(Usuario usuario, int dias);

    Recordatorio crearRecordatorioDeAgendamiento(Agendamiento agendamiento, Usuario usuario);

    void procesarAgendamientosProximos(Usuario usuario, int dias);
}