package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.barberspa.model.Agendamiento;
import com.sena.barberspa.model.Usuario;
import com.sena.barberspa.repository.IAgendamientosRepository;

@Service
public class AgendamientosServiceImplement implements IAgendamientosService {

	@Autowired
	private IAgendamientosRepository agendamientoRepository;

	@Override
	@Transactional
	public Agendamiento save(Agendamiento agendamiento) {
		// TODO Auto-generated method stub
		return agendamientoRepository.save(agendamiento);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Agendamiento> get(Long id) {
		// TODO Auto-generated method stub
		return agendamientoRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Agendamiento findById(Long id) {
		// TODO Auto-generated method stub
		return agendamientoRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void update(Agendamiento agendamiento) {
		// TODO Auto-generated method stub
		agendamientoRepository.save(agendamiento);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		// TODO Auto-generated method stub
		agendamientoRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Agendamiento> findAll() {
		// TODO Auto-generated method stub
		return agendamientoRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Agendamiento> findByUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return agendamientoRepository.findByUsuario(usuario);
	}

}