package com.example.demo.models.service;

import com.example.demo.models.entity.Lote;
import com.example.demo.models.repository.LoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoteService {

    private final LoteRepository loteRepository;

    @Autowired
    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    public Lote obtenerLotePorId(Long id) {
        return loteRepository.findById(id).orElse(null);
    }

    public void guardarLote(Lote lote) {
        loteRepository.save(lote);
    }

}
