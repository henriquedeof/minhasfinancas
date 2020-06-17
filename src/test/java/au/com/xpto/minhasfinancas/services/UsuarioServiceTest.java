package au.com.xpto.minhasfinancas.services;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test") //Used for tests. If I do not use Spring Profile (application.properties) I need to use this configuration.
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void autenticar() {
    }

    @Test
    void salvarUsuario() {
    }

    @Test
    void validarEmail() {
        this.usuarioRepository.deleteAll();

        //Any kind of exception should be thrown
        assertDoesNotThrow(() -> {
            this.usuarioService.validarEmail("email@email.com");
        });

    }

    @Test
    void verificarEmailJaCadastrado() {
        this.usuarioRepository.save(Usuario.builder().nome("usuario").email("email@email.com").build());

        //Expecting RegraDeNegocioException to be thrown. If RegraDeNegocioException is not Thrown an AssertionFailedError is show.
        assertThrows(RegraDeNegocioException.class, () -> {
            this.usuarioService.validarEmail("email@email.com");
        })

    }

}
