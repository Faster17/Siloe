package com.example.demo.util;
import com.example.demo.models.entity.Usuario;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionManager {

    private static final String SESSION_ATTRIBUTE_USER = "loggedInUser";

    public void iniciarSesion(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_ATTRIBUTE_USER, usuario);
    }

    public void cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(SESSION_ATTRIBUTE_USER);
        session.invalidate();
    }

    public boolean isUsuarioLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getAttribute(SESSION_ATTRIBUTE_USER) != null;
    }

    public Usuario getUsuarioLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Usuario) session.getAttribute(SESSION_ATTRIBUTE_USER);
    }
}
