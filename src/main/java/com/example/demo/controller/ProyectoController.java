package com.example.demo.controller;

import com.example.demo.models.entity.Imagen;
import com.example.demo.models.entity.Lote;
import com.example.demo.models.entity.Proyecto;
import com.example.demo.models.entity.TipoLote;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.service.LoteService;
import com.example.demo.models.service.ProyectoService;
import com.example.demo.models.service.TipoLoteService;
import com.example.demo.util.SessionManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/proyectos")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private TipoLoteService tipoLoteService;
    @Autowired
    private LoteService loteService;
    @Autowired
    private SessionManager sessionManager;

    private int numeroDeTipos = 0;

    private static final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    @ModelAttribute("rol")
    public int agregarRol(HttpServletRequest request) {
        if (sessionManager.isUsuarioLogueado(request)) {
            Usuario usuario = sessionManager.getUsuarioLogueado(request);
            return usuario.getRol();
        }
        return 4;
    }
    @GetMapping
    public String showAll(Model model) {
        model.addAttribute("proyectos", proyectoService.findAll());
        return "proyectos";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
    Proyecto proyecto = proyectoService.findById(id);
        model.addAttribute("proyecto", proyecto);
        return "proyectos/" + id + ".html";
    }


    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("proyecto", new Proyecto());
        return "proyecto_form";
    }

    @PostMapping
public String create(@ModelAttribute @Valid Proyecto proyecto, BindingResult result, @RequestParam(name = "files") MultipartFile[] files,
                     @RequestParam Map<String, String> allParams) {
    if (result.hasErrors()) {
        return "proyecto_form";
    }
    for (MultipartFile file : files) {
        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename().split("\\.")[1];

            try {
                file.transferTo(new File(uploadDir + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Imagen img = new Imagen();
            img.setFileName(fileName);
            img.setProyecto(proyecto);
            proyecto.getImagenes().add(img);
        }

    }
    numeroDeTipos++;

    allParams.forEach((key, value) -> {
        if (key.startsWith("tamanio_")) {
            int index = Integer.parseInt(key.split("_")[1]);
            String tamaño = value;
            String precio = allParams.get("precio_" + index);
            String loteInicial = allParams.get("inicial_" + index);
            String loteFinal = allParams.get("final_" + index);

            TipoLote tipoLote = new TipoLote();
            tipoLote.setTamano(tamaño);
            tipoLote.setPrecio(Double.parseDouble(precio));
            tipoLote.setLoteInicial(Integer.parseInt(loteInicial));
            tipoLote.setLoteFinal(Integer.parseInt(loteFinal));
            tipoLote.setProyecto(proyecto);
            proyecto.getTiposLotes().add(tipoLote);

            for (int i = tipoLote.getLoteInicial(); i <= tipoLote.getLoteFinal(); i++) {
                Lote lote = new Lote();
                lote.setIndice(i);
                lote.setTipoLote(tipoLote);
                tipoLote.getLotes().add(lote);
            }
        }
        
    });
    


    proyectoService.save(proyecto);
    String templateFileName = "proyecto_template.html";
File templateFile = new File("src/main/resources/templates/proyectos/" + templateFileName);
String templateContent = "";

try (BufferedReader reader = new BufferedReader(new FileReader(templateFile))) {
    String line;
    while ((line = reader.readLine()) != null) {
        templateContent += line + "\n";
    }
} catch (IOException e) {
    e.printStackTrace();
}
String htmlContent = templateContent
        .replace("{{proyecto.nombre}}", proyecto.getNombre())
        .replace("{{proyecto.descripcion}}", proyecto.getDescripcion());
String htmlFileName = proyecto.getId() + ".html";
File htmlFile = new File("src/main/resources/templates/proyectos/" + htmlFileName);

try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile))) {
    writer.write(htmlContent);
} catch (IOException e) {
    e.printStackTrace();
}
    return "redirect:/proyectos";
}

@GetMapping("/{id}/edit")
public String showForm(@PathVariable Long id, Model model) {
    Proyecto proyecto = proyectoService.findById(id);
    model.addAttribute("proyecto", proyecto);
    model.addAttribute("tiposLotes", proyecto.getTiposLotes());
    model.addAttribute("editMode", true);
    numeroDeTipos = proyecto.getTiposLotes().size();
    List<String> imageNames = proyecto.getImagenes().stream()
        .map(Imagen::getFileName)
        .collect(Collectors.toList());
    for (int i = 0; i < imageNames.size(); i++) {
        model.addAttribute("imagen" + (i + 1), imageNames.get(i));
    }
    
    return "proyecto_form";
}



@PostMapping("/{id}")
public String update(@PathVariable Long id, @ModelAttribute @Valid Proyecto proyecto, BindingResult result, @RequestParam(name = "files") MultipartFile[] files,
                     @RequestParam Map<String, String> allParams) {
    if (result.hasErrors()) {
        return "proyecto_form";
    }
    Proyecto existingProyecto = proyectoService.findById(id);
    existingProyecto.setNombre(proyecto.getNombre());
    existingProyecto.setDistrito(proyecto.getDistrito());
    existingProyecto.setUbicacion(proyecto.getUbicacion());
    existingProyecto.setDescripcion(proyecto.getDescripcion());
    
    for (int i = 0; i < files.length; i++) {
        MultipartFile file = files[i];
        
        if (!file.isEmpty()) {
            if (i < existingProyecto.getImagenes().size()) {
                // Replace an existing image with the new image
                proyectoService.replaceImage(existingProyecto, file, i);
            } else {
                // Add a new image
                String fileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename().split("\\.")[1];
                try {
                    file.transferTo(new File(uploadDir + fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Imagen img = new Imagen();
                img.setFileName(fileName);
                img.setProyecto(existingProyecto);
                existingProyecto.getImagenes().add(img);
            }
        }
    }
    
    
    existingProyecto.getTiposLotes().clear();

    allParams.forEach((key, value) -> {
        if (key.startsWith("tamanio_")) {
            int index = Integer.parseInt(key.split("_")[1]);
            String tamaño = value;
            String precio = allParams.get("precio_" + index);
            String loteInicial = allParams.get("inicial_" + index);
            String loteFinal = allParams.get("final_" + index);

            TipoLote tipoLote = new TipoLote();
            tipoLote.setTamano(tamaño);
            tipoLote.setPrecio(Double.parseDouble(precio));
            tipoLote.setLoteInicial(Integer.parseInt(loteInicial));
            tipoLote.setLoteFinal(Integer.parseInt(loteFinal));
            tipoLote.setProyecto(existingProyecto);
            existingProyecto.getTiposLotes().add(tipoLote);

            for (int i = tipoLote.getLoteInicial(); i <= tipoLote.getLoteFinal(); i++) {
                Lote lote = new Lote();
                lote.setIndice(i);
                lote.setTipoLote(tipoLote);
                tipoLote.getLotes().add(lote);
            }
        }
    });
    
    return "redirect:/proyectos";
}


@PostMapping("/{id}/delete")
public String delete(@PathVariable Long id, Long idLote) {
    Proyecto proyecto = proyectoService.findById(id);
    for (Imagen img : proyecto.getImagenes()) {
        File imgFile = new File(uploadDir + img.getFileName());
        imgFile.delete();
    }
    File htmlFile = new File("src/main/resources/templates/proyectos/" + id + ".html");
    htmlFile.delete();
    proyectoService.deleteById(id);
    return "redirect:/proyectos";
}

@PostMapping("/{id}/eliminarTipoLote/{tipoLoteId}")
public String eliminarTipoLoteEnProyecto(@PathVariable Long id, @PathVariable Long tipoLoteId) {
    proyectoService.eliminarTipoLoteEnProyecto(id, tipoLoteId);
    return "redirect:/proyectos/{id}/edit";
}

@PostMapping("/actualizar-estado-lote")
    public ResponseEntity<?> actualizarEstadoLote(@RequestParam("loteId") Long loteId, @RequestParam("estado") String estado) {
        Lote lote = loteService.obtenerLotePorId(loteId);
        lote.setEstado(estado);
        loteService.guardarLote(lote);
        return ResponseEntity.ok().build();
    }
}
