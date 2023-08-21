package com.example.demo.models.repository;
import com.example.demo.models.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByDni(String dni);
    Usuario findByCorreo(String correo);
    Usuario findById(int id);
    Usuario findByCorreoOrDni(String correo, String dni);
}
