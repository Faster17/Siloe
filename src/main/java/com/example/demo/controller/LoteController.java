package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.models.entity.Lote;
import com.example.demo.models.entity.Proyecto;
import com.example.demo.models.entity.TipoLote;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.Venta;
import com.example.demo.models.service.LoteService;
import com.example.demo.models.service.StripeService;
import com.example.demo.models.service.VentaService;
import com.example.demo.util.Response;
import com.example.demo.util.SessionManager;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/lotes")
public class LoteController {

    @Value("${stripe.key.public}")
    private String API_PUBLIC_KEY;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private LoteService loteService;

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

    @GetMapping("/separar")
    public String irAFormularioSeparar(@RequestParam("idLote") Long idLote, Model model, HttpServletRequest request) {
        Lote lote = loteService.obtenerLotePorId(idLote);
        TipoLote tipoLote = lote.getTipoLote();
        Proyecto proyecto = tipoLote.getProyecto();
        Usuario usuario = sessionManager.getUsuarioLogueado(request);
        model.addAttribute("usuario", usuario);
        model.addAttribute("proyectoNombre", proyecto.getNombre());
        model.addAttribute("proyectoUbi", proyecto.getUbicacion());
        model.addAttribute("loteIndice", lote.getIndice());
        model.addAttribute("loteId", lote.getId());
        model.addAttribute("tipoLoteTamano", tipoLote.getTamano());
        model.addAttribute("tipoLotePrecio", tipoLote.getPrecio());
        model.addAttribute("stripePublicKey", "pk_test_51NgB8XD3DWSmto6vKowYIFT4d50cuJtEIW6W9xq4gUciixGIgPaYmc8bBfnIYgTndd7gbvoXU43R985vhni8Bytt00ypb124hK");
        return "separar";
    }

    @PostMapping("/actualizar-estado-lote")
    public ResponseEntity<?> actualizarEstadoLote(@RequestParam("loteId") Long loteId,
                                                @RequestParam("cuotasTotales") Integer cuotasTotales,
                                                HttpServletRequest request) {
        Lote lote = loteService.obtenerLotePorId(loteId);
        Usuario usuario = sessionManager.getUsuarioLogueado(request);
        lote.setEstado("Separado");
        loteService.guardarLote(lote);

        Venta venta = new Venta();
        venta.setLote(lote);
        venta.setCuotasPagadas(0);
        
        // Set cuotasTotales using the value received from the client
        venta.setCuotasTotales(cuotasTotales);
        
        venta.setContrato("Espera");
        venta.setCosto(lote.getTipoLote().getPrecio());
        venta.setUsuario(usuario);
        ventaService.crearVenta(venta);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/create-charge")
    public @ResponseBody Response createCharge(String dni, String token) {
        if (token == null) {
            return new Response(false, "El token de Stripe no es admitido: ERROR");
        }

        String chargeId = stripeService.createCharge(dni, token, 50000);

        if (chargeId == null) {
            return new Response(false, "Ocurrió un error al realizar la Separación");
        }

        return new Response(true, "Separacion Realizada -  id del pago:" + chargeId);
    }
}
