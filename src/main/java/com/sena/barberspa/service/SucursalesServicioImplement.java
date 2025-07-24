package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.barberspa.model.Sucursal;
import com.sena.barberspa.repository.ISucursalesRepository;

@Service
@Transactional
public class SucursalesServicioImplement implements ISucursalesService {

	@Autowired
	private ISucursalesRepository sucursalRepository;

	@Override
	public Sucursal save(Sucursal sucursal) {
		// TODO Auto-generated method stub
		return sucursalRepository.save(sucursal);
	}

	@Override
	public Optional<Sucursal> get(Long id) {
		// TODO Auto-generated method stub
		return sucursalRepository.findById(id);
	}

	@Override
	public Sucursal findById(Long id) {
		// TODO Auto-generated method stub
		return sucursalRepository.findById(id).orElse(null);
	}

	@Override
	public void update(Sucursal sucursal) {
		// TODO Auto-generated method stub
		sucursalRepository.save(sucursal);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		sucursalRepository.deleteById(id);
	}

	@Override
	public List<Sucursal> findAll() {
		// TODO Auto-generated method stub
		return sucursalRepository.findAll();
	}

	// @Override
	// public List<Sucursal> findbyNombre(String Nombre) {
	// TODO Auto-generated method stub
	// return null;
	// }

}
