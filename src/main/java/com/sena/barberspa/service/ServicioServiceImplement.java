package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.barberspa.model.Servicio;
import com.sena.barberspa.repository.IServiciosRepository;

@Service
@Transactional
public class ServicioServiceImplement implements IServiciosService {

	@Autowired
	private IServiciosRepository servicioRepository;

	@Override
	public Servicio save(Servicio servicio) {
		// TODO Auto-generated method stub
		return servicioRepository.save(servicio);
	}

	@Override
	public Optional<Servicio> get(Long id) {
		// TODO Auto-generated method stub
		return servicioRepository.findById(id);
	}

	@Override
	public void update(Servicio servicio) {
		// TODO Auto-generated method stub
		servicioRepository.save(servicio);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		servicioRepository.deleteById(id);

	}

	@Override
	public List<Servicio> findAll() {
		// TODO Auto-generated method stub
		return servicioRepository.findAll();
	}

}
