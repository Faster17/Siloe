package com.example.demo.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.example.demo.models.entity.TipoLote;
import com.example.demo.models.repository.TipoLoteRepository;

@Service
public class TipoLoteService {
    @Autowired
    private TipoLoteRepository tipoLoteRepository;

    public List<TipoLote> findAll() {
        return tipoLoteRepository.findAll();
    }

    public TipoLote save(TipoLote tipoLote) {
        return tipoLoteRepository.save(tipoLote);
    }

    public void deleteById(Long id) {
        tipoLoteRepository.deleteById(id);
    }

    public TipoLote findById(Long id) {
        return tipoLoteRepository.findById(id).orElse(null);
    }
    
}
