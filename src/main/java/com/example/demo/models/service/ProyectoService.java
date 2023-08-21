package com.example.demo.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.entity.Imagen;
import com.example.demo.models.entity.Proyecto;
import com.example.demo.models.entity.TipoLote;
import com.example.demo.models.repository.ProyectoRepository;
import com.example.demo.models.repository.TipoLoteRepository;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProyectoService {
    
    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private TipoLoteRepository tipoLoteRepository;

    private static final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    public List<Proyecto> findAll() {
        return proyectoRepository.findAll();
    }

    public Proyecto findById(Long id) {
        return proyectoRepository.findById(id).orElse(null);
    }

    public Proyecto save(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public void deleteById(Long id) {
        proyectoRepository.deleteById(id);
    }

    public void eliminarTipoLoteEnProyecto(Long proyectoId, Long tipoLoteId) {
    Proyecto proyecto = proyectoRepository.findById(proyectoId).orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
    TipoLote tipoLote = tipoLoteRepository.findById(tipoLoteId).orElseThrow(() -> new EntityNotFoundException("Tipo de lote no encontrado"));

    proyecto.getTiposLotes().remove(tipoLote);
    tipoLoteRepository.delete(tipoLote);

    proyectoRepository.save(proyecto);
    }
    public void replaceImage(Proyecto proyecto, MultipartFile file, int index) {
        Imagen existingImage = proyecto.getImagenes().get(index);
        
        File oldImageFile = new File(uploadDir + existingImage.getFileName());
        oldImageFile.delete();
        String fileName = existingImage.getFileName();
        try {
            file.transferTo(new File(uploadDir + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

