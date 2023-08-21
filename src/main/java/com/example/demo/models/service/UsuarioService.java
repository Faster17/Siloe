package com.example.demo.models.service;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public void registrarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario obtenerUsuarioPorDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }
    
    public Usuario obtenerUsuarioPorCorreoODNI(String correoODNI) {
        return usuarioRepository.findByCorreoOrDni(correoODNI, correoODNI);
    }

    public void actualizarRolUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

}
