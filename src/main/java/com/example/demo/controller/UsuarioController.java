package com.example.demo.controller;

import com.example.demo.models.entity.Lote;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.Venta;
import com.example.demo.models.service.ProyectoService;
import com.example.demo.models.service.StripeService;
import com.example.demo.models.service.UsuarioService;
import com.example.demo.models.service.VentaService;
import com.example.demo.util.DatosRecuperacion;
import com.example.demo.util.Response;
import com.example.demo.util.SessionManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UsuarioController {

    @Value("${stripe.key.public}")
    private String API_PUBLIC_KEY;

    @Autowired
    private StripeService stripeService;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private VentaService ventaService;
    
    @ModelAttribute("rol")
    public int agregarRol(HttpServletRequest request) {
        if (sessionManager.isUsuarioLogueado(request)) {
            Usuario usuario = sessionManager.getUsuarioLogueado(request);
            return usuario.getRol();
        }
        return 4;
    }

    @GetMapping("/cambiar-rol")
    public String showBuscarUsuarioForm(Model model) {
        model.addAttribute("correo", "");
        model.addAttribute("usuario", null);
        return "cambiar_rol";
    }

    
    @PostMapping("/cambiar-rol")
    public String buscarUsuario(@RequestParam String correo, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correo);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", Arrays.asList("usuario", "admin"));
        } else {
            model.addAttribute("mensaje", "Usuario no registrado");
        }
        
        return "cambiar_rol";
    }


    @PostMapping("/procesar")
    public String procesarUsuario(@ModelAttribute Usuario usuario, Model model) {
        Usuario usuarioExistente = usuarioService.obtenerUsuarioPorCorreo(usuario.getCorreo());
        if (usuarioExistente != null) {
            usuarioExistente.setRol(usuario.getRol());
            usuarioService.actualizarRolUsuario(usuarioExistente);
            model.addAttribute("mensajeExito", "Rol del usuario actualizado exitosamente.");
        } else {
            model.addAttribute("mensajeError", "No se encontró el usuario con el correo proporcionado.");
        }
        model.addAttribute("roles", Arrays.asList("usuario", "admin"));
        return "cambiar_rol";
    }


    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        if (sessionManager.isUsuarioLogueado(request)) {
            Usuario usuario = sessionManager.getUsuarioLogueado(request);
            model.addAttribute("rol", usuario.getRol());
        }
        else{
            model.addAttribute("rol", 4);
        }
        model.addAttribute("proyectos", proyectoService.findAll());
        return "home";
    }
    
    @GetMapping("/contacto")
    public String index(){
        return "contacto";
        
    }
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model, HttpServletRequest request) {
        Usuario usuarioExistente = usuarioService.obtenerUsuarioPorCorreo(usuario.getCorreo());
        if (usuarioExistente != null) {
            model.addAttribute("errorCorreo", "El correo ya está registrado");
            return "registro";
        }
        Usuario usuarioDniExistente = usuarioService.obtenerUsuarioPorDni(usuario.getDni());
        if (usuarioDniExistente != null) {
            model.addAttribute("errorDni", "El DNI ya está registrado");
            return "registro";
        }
        usuarioService.registrarUsuario(usuario);
        usuarioService.registrarUsuario(usuario);
        sessionManager.iniciarSesion(request, usuario);

        return "redirect:/perfil";
    }
    @GetMapping("/inicio-sesion")
    public String mostrarFormularioInicioSesion(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("errorInicioSesionContrasena", "");
        model.addAttribute("errorInicioSesionCorreo", "");
        return "inicio_sesion";
    }
    @PostMapping("/inicio-sesion")
    public String iniciarSesion(@ModelAttribute("usuario") Usuario usuario, Model model, HttpServletRequest request) {
        Usuario usuarioRegistrado = usuarioService.obtenerUsuarioPorCorreo(usuario.getCorreo());
        if (usuarioRegistrado != null && usuarioRegistrado.getContrasena().equals(usuario.getContrasena())) {
            sessionManager.iniciarSesion(request, usuarioRegistrado);
            model.addAttribute("rol", usuario.getRol());

            return "redirect:/perfil";
        } else {
            if (usuarioRegistrado == null) {
                model.addAttribute("errorInicioSesionCorreo", "El correo no está registrado");
            } else {
                model.addAttribute("errorInicioSesionContrasena", "Contraseña incorrecta");
            }
            return "inicio_sesion";
        }
    }
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, HttpServletRequest request) {
        if (sessionManager.isUsuarioLogueado(request)) {
            Usuario usuario = sessionManager.getUsuarioLogueado(request);
            List<Venta> ventas = ventaService.obtenerVentasPorUsuarioId(usuario.getId());
            model.addAttribute("ventas", ventas);
            model.addAttribute("usuario", usuario);
            model.addAttribute("stripePublicKey", "pk_test_51NgB8XD3DWSmto6vKowYIFT4d50cuJtEIW6W9xq4gUciixGIgPaYmc8bBfnIYgTndd7gbvoXU43R985vhni8Bytt00ypb124hK");
            
            if (ventas.isEmpty()) {
                model.addAttribute("ventasMessage", 1);
            } else {
                model.addAttribute("ventasMessage", 2);
            }
            
            return "perfil";
        } else {
            return "redirect:/inicio-sesion";
        }
    }



    @PostMapping("/create-charge")
    public @ResponseBody Response createCharge(String dni, String token, int monto) {
        if (token == null) {
            return new Response(false, "El token de Stripe no es admitido: ERROR");
        }
        String chargeId = stripeService.createCharge(dni, token, monto);

        if (chargeId == null) {
            return new Response(false, "Ocurrió un error al realizar el pago");
        }

        return new Response(true, "Pago Realizado -  id del pago:" + chargeId);
    }

    @PostMapping("/update-cuotas-pagadas")
    public ResponseEntity<?> updateCuotasPagadas(@RequestParam int ventaId) {
        Venta venta = ventaService.obtenerVentaPorId(ventaId);
        venta.setCuotasPagadas(venta.getCuotasPagadas() + 1);
        ventaService.guardarVenta(venta);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-contratoT")
    public ResponseEntity<?> updateContrato(@RequestParam int ventaId) {
        Venta venta = ventaService.obtenerVentaPorId(ventaId);
        venta.setContrato("Finalizado");
        venta.setFecha(LocalDate.now());
        ventaService.guardarVenta(venta);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/recuperar-contrasena")
    public String mostrarFormularioRecuperarContrasena(Model model) {
        model.addAttribute("datosRecuperacion", new DatosRecuperacion());
        return "recuperar_contrasena";
    }
    @PostMapping("/recuperar-contrasena")
    public String recuperarContrasena(@ModelAttribute("datosRecuperacion") DatosRecuperacion datosRecuperacion, Model model) {
        String correoODNI = datosRecuperacion.getCorreoODNI();
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreoODNI(correoODNI);
        if (usuario != null) {
            if (correoODNI.contains("@")) {
                String mensajeRecuperacionCorreo = "¿Desea recuperar la contraseña de " + usuario.getNombres() + " " + usuario.getApellidos() + " a través de correo electrónico?";
                model.addAttribute("mensajeRecuperacion", mensajeRecuperacionCorreo);
                model.addAttribute("correo", usuario.getCorreo());
                return "recuperar_contrasena";
            } else {
                String mensajeRecuperacionDNI = "¿Desea recuperar la contraseña de " + usuario.getNombres() + " " + usuario.getApellidos() + " a través del DNI?";
                model.addAttribute("mensajeRecuperacion", mensajeRecuperacionDNI);
                model.addAttribute("dni", usuario.getDni());
                return "recuperar_contrasena";
            }
        } else {
            model.addAttribute("errorRecuperacion", "El correo o DNI no está registrado");
            return "recuperar_contrasena";
        }
    }
    @GetMapping("/datos-sesion")
    public String mostrarDatosSesion(@RequestParam("dni") String dni, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorDni(dni);
        model.addAttribute("usuario", usuario);
        return "datos_sesion";
    }
    @GetMapping("/enviar-correo")
    public String enviarCorreo(@RequestParam("correo") String correo, Model model) {
        model.addAttribute("correo", correo);
        return "enviar_correo";
    }

    @GetMapping("/confirmar-continuar")
    public String confirmarContinuar() {
        return "confirmar_continuar";
    }

    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpServletRequest request) {
        sessionManager.cerrarSesion(request);
        return "redirect:/inicio-sesion";
    }
}


