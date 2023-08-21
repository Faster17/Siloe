package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.Venta;
import com.example.demo.models.service.LoteService;
import com.example.demo.models.service.UsuarioService;
import com.example.demo.models.service.VentaService;
import com.example.demo.util.SessionManager;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/ventas")
public class VentasController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private LoteService loteService;
    
    @ModelAttribute("rol")
    public int agregarRol(HttpServletRequest request) {
        if (sessionManager.isUsuarioLogueado(request)) {
            Usuario usuario = sessionManager.getUsuarioLogueado(request);
            return usuario.getRol();
        }
        return 4;
    }

   @GetMapping("/mostrar")
    public String showBuscarUsuarioForm(Model model) {
        model.addAttribute("dni", "");
        model.addAttribute("usuario", null);
        return "ventas";
    }

    
    @PostMapping("/mostrar")
    public String buscarUsuario(@RequestParam String dni, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorDni(dni);
        
        if (usuario != null) {
            List<Venta> ventas = ventaService.obtenerVentasPorUsuarioId(usuario.getId());
            model.addAttribute("usuario", usuario);
            model.addAttribute("ventas", ventas);
            model.addAttribute("mensaje", "CLIENTE Encontrado");
        } else {
            model.addAttribute("mensaje", "DNI no registrado a ningún cliente");
        }
        
        return "ventas";
    }

    @GetMapping("/obtener-venta")
    @ResponseBody
    public ResponseEntity<Venta> obtenerVentaPorId(@RequestParam int id) {
        Venta venta = ventaService.obtenerVentaPorId(id);
        
        if (venta != null) {
            return ResponseEntity.ok(venta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/actualizar-estado-contrato")
    public ResponseEntity<?> actualizarEstadoContrato(@RequestParam("ventaId") int ventaId, @RequestParam("contrato") String contrato) {
        Venta venta = ventaService.obtenerVentaPorId(ventaId);
        venta.setContrato(contrato);
        venta.setFecha(LocalDate.now());
        ventaService.guardarVenta(venta);
        return ResponseEntity.ok().build();
    }

}
