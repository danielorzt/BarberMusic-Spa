package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import com.sena.barberspa.model.Servicio;

public interface IServiciosService {
	public Servicio save(Servicio servicio);

	public Optional<Servicio> get(Long id);

	public void update(Servicio servicio);

	public void delete(Long id);

	public List<Servicio> findAll();
}
