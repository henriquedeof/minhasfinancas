package au.com.xpto.minhasfinancas.services.impl;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.repositories.UsuarioRepository;
import au.com.xpto.minhasfinancas.services.UsuarioService;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        return null;
    }

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        Boolean exists = this.usuarioRepository.existsByEmail(email);
        if (exists){
            throw new RegraDeNegocioException("Ja existe um usuario cadastrado com este email");
        }
    }
}
