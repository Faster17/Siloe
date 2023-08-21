package com.example.demo.models.repository;

import com.example.demo.models.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteRepository extends JpaRepository<Lote, Long> {
}
