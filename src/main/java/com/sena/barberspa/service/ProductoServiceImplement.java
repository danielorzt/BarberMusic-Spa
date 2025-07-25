package com.sena.barberspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.barberspa.model.Producto;
import com.sena.barberspa.repository.IProductoRepository;

@Service
public class ProductoServiceImplement implements IProductoService {

	@Autowired
	private IProductoRepository productoRepository;

	@Override
	@Transactional
	public Producto save(Producto producto) {
		// TODO Auto-generated method stub
		return productoRepository.save(producto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Producto> get(Long id) {
		// TODO Auto-generated method stub
		return productoRepository.findById(id);
	}

	@Override
	@Transactional
	public void update(Producto producto) {
		// TODO Auto-generated method stub
		productoRepository.save(producto);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		// TODO Auto-generated method stub
		productoRepository.deleteById(id);

	}

	@Override
	@Transactional(readOnly = true)
	public List<Producto> findAll() {
		// TODO Auto-generated method stub
		return productoRepository.findAll();
	}

}
