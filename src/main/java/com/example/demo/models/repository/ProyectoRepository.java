package com.example.demo.models.repository;
import com.example.demo.models.entity.Proyecto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
}