package au.com.xpto.minhasfinancas.repositories;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@RunWith(SpringRunner.class)  //Annotation for JUnit 4.
//@ExtendWith(SpringExtension.class)    //Substitute the annotation above (if necessary).
//@DataJpaTest        //Annotation for a JPA test that focuses ONLY on JPA components.
@ActiveProfiles("test") //Used for tests. If I do not use Spring Profile (application.properties) I need to use this configuration.
@SpringBootTest     //This annotation will bring the whole context up based on the Spring Boot configuration.
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository; //UsuarioRepository is an Interface and does not have any implementation by me. So, I needed to autowire it.

    @BeforeEach
    void setUp() {

    }

    @Test
    void existsByEmail() {
        //given - cenario
        Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").build();
        this.usuarioRepository.save(usuario);

        //when - acao/execucao
        Boolean exists = this.usuarioRepository.existsByEmail("usuario@email.com");

        //then - verificacao
        assertTrue(exists);
    }

    @Test
    void verifyNonRegisteredEmail(){
        //given
        this.usuarioRepository.deleteAll();;

        //when
        Boolean exists = this.usuarioRepository.existsByEmail("usuario@email.com");

        //then
        assertFalse(exists);
    }

}
