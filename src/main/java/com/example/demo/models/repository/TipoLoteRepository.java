package com.example.demo.models.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.entity.TipoLote;

@Repository
public interface TipoLoteRepository extends JpaRepository<TipoLote, Long> {

    Optional<TipoLote> findById(Long id);
}