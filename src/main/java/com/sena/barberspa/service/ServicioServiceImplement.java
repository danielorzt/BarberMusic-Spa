package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.repository.IServiciosRepository;

@Service
public class ServicioServiceImplement implements IServiciosService {

	@Autowired
	private IServiciosRepository servicioRepository;

	@Override
	@Transactional
	public Servicio save(Servicio servicio) {
		// TODO Auto-generated method stub
		return servicioRepository.save(servicio);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Servicio> get(Long id) {
		// TODO Auto-generated method stub
		return servicioRepository.findById(id);
	}

	@Override
	@Transactional
	public void update(Servicio servicio) {
		// TODO Auto-generated method stub
		servicioRepository.save(servicio);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		// TODO Auto-generated method stub
		servicioRepository.deleteById(id);

	}

	@Override
	@Transactional(readOnly = true)
	public List<Servicio> findAll() {
		return servicioRepository.findAll();
	}

}
