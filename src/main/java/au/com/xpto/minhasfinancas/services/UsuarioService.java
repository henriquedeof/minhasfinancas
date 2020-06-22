package au.com.xpto.minhasfinancas.services;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;

import java.util.Optional;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);

    Optional<Usuario> usuarioPorId(Long id);

}
