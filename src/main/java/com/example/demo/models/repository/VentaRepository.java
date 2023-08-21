package com.example.demo.models.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByUsuarioId(Long usuarioId);
    Venta findById(int id);
}
