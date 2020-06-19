package au.com.xpto.minhasfinancas.services.impl;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.exceptions.ErroAutenticacaoException;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.repositories.UsuarioRepository;
import au.com.xpto.minhasfinancas.services.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuarioOptional = this.usuarioRepository.findByEmail(email);

        if(!usuarioOptional.isPresent()){
            throw new ErroAutenticacaoException("Usuario nao encontrado para o email informado.");
        }

        if (!usuarioOptional.get().getSenha().equals(senha)){
            throw new ErroAutenticacaoException("Senha invalida.");
        }

        return usuarioOptional.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        this.validarEmail(usuario.getEmail());
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
