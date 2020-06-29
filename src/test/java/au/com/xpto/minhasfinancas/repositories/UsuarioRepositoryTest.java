package au.com.xpto.minhasfinancas.repositories;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
@RunWith(SpringRunner.class)  //Annotation for JUnit 4.
@ExtendWith(SpringExtension.class)    //Substitute the annotation above (if necessary).
@SpringBootTest     //This annotation will bring the whole context up based on the Spring Boot configuration.
 */
@DataJpaTest    //Annotation for a JPA test that focuses ONLY on JPA components. Transactional command is applied on this annotation.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //It will not replace my configs (application-test.properties) for a default config. Specific for Repository tests.
@ActiveProfiles("test") //Used for tests. If I do not use Spring Profile (application.properties) I need to use this configuration.
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository; //UsuarioRepository is an Interface and does not have any implementation by me. So, I needed to autowire it.

    private final Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").build();;

    @BeforeEach
    void setUp() {

        //Example if I want to load pre-configured data. Useful when the annotation @DataJpaTest is in use.
        //Bootstrap bootstrap = new Bootstrap(dependency1, dependency2, dependencyN);
        //bootstrap.run(); //load data
    }

    @Test
    void existsByEmail() {
        //given - cenario
        this.usuarioRepository.save(usuario);

        //when - acao/execucao
        Boolean exists = this.usuarioRepository.existsByEmail("usuario@email.com");

        //then - verificacao
        assertTrue(exists);
    }

    @Test
    void verifyNonRegisteredEmail(){
        //when
        Boolean exists = this.usuarioRepository.existsByEmail("usuario@email.com");

        //then
        assertFalse(exists);
    }

    @Test
    void saveUsuario(){
        Usuario save = this.usuarioRepository.save(usuario);
        assertNotNull(save.getId());
    }

    @Test
    void getUsuarioByEmail(){
        this.usuarioRepository.save(usuario);
        Usuario savedUsuario = this.usuarioRepository.findByEmail("usuario@email.com").get();

        assertNotNull(savedUsuario.getEmail());
        assertEquals("usuario@email.com", savedUsuario.getEmail());
    }

    @Test
    void verifyNonRegisteredUsuarioByEmail(){
        Optional<Usuario> savedUsuario = this.usuarioRepository.findByEmail("usuario@email.com");
        assertFalse(savedUsuario.isPresent());
    }


}
