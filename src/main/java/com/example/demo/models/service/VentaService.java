package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.entity.Lote;
import com.example.demo.models.entity.Venta;
import com.example.demo.models.repository.VentaRepository;

@Service
public class VentaService {
    
    private final VentaRepository ventaRepository;

    @Autowired
    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public Venta crearVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    public List<Venta> obtenerVentasPorUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    public Venta obtenerVentaPorId(int id) {
        return ventaRepository.findById(id);
    }
    public void guardarVenta(Venta venta) {
        ventaRepository.save(venta);
    }
    
}
