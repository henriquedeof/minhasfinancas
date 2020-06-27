package au.com.xpto.minhasfinancas.services;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.exceptions.ErroAutenticacaoException;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.repositories.UsuarioRepository;
import au.com.xpto.minhasfinancas.services.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
@SpringBootTest //This annotation brings the whole context up based on the Spring Boot configuration.
@ExtendWith(MockitoExtension.class)   //This annotation enables the use of Mockito annotation. I can user this annotation or the method (best option) MockitoAnnotations.initMocks(this);
*/
@ActiveProfiles("test") //Used for tests. If I do not use Spring Profile (application.properties) I need to use this configuration.
class UsuarioServiceTest {
    /*
    - I should Autowire both attributes below if I were using the annotation @SpringBootTest. But with Mockito, I can use the annotation @Mock and use the Constructor injection.

    - Another way of injecting dependencies is through the annotation @InjectMocks (in conjunction with @ExtendWith(MockitoExtension.class)). Example:
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OtherRepository otherRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    - The two lines below are used to test Controllers, regarding HTTP Status, Models, Views, Redirects, etc.
    private org.springframework.test.web.servlet.MockMvc mockMvc;
    mockMvc = MockMvcBuilders.standaloneSetup(myController).build(); -> This line is normally used on setUp() method


    ============================= @Spy example =============================

    @SpyBean private UsuarioService usuarioService;
    @MockBean private UsuarioRepository usuarioRepository;

    - There is no need to use MockitoAnnotations.initMocks(this), however, I need to annotate the class with @ExtendWith(MockitoExtension.class).
    - Also, I do not need to create a Controller Injection.


    */

    //===================================================================================

    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

     public static final String EMAIL = "email@email.com";
     public static final String SENHA = "senha";
     public static final String NOME = "nome";
     public static final Long ID = 1L;
     private final Usuario usuario = Usuario.builder().email(EMAIL).senha(SENHA).id(ID).nome(NOME).build();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); //I can use this method (best option) or the annotation @ExtendWith(MockitoExtension.class)
        //this.usuarioService = new UsuarioServiceImpl(this.usuarioRepository); //Using Constructor injection (best option). Not using this because I need it as SPY. See SPY NOTE below.

        //Another way to create mock is using Mockito.mock(UsuarioRepository.class) instead of MockitoAnnotations.initMocks().
        // In this way, I do not need to annotate the attribute usuarioRepository with @Mock

        //Finally, I also could use the annotation @MockBean on usuarioRepository attribute along with @ExtendWith(SpringExtension.class) @ExtendWith(MockitoExtension.class).
        //This way I can also inject the dependencies using Constructor

        //SPY NOTE: I can add my usuarioService as a spy by using the way below.
        this.usuarioService = spy(new UsuarioServiceImpl(this.usuarioRepository)); //Using Constructor injection for Spy
    }

    @Test
    void autenticarUsuarioSucesso() {
        //given
        when(this.usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));

        //when
        Usuario usuario = this.usuarioService.autenticar(EMAIL, SENHA);

        //then
        assertNotNull(usuario);
//        assertDoesNotThrow(() -> {
//            this.usuarioService.autenticar(EMAIL, SENHA)
//        });
    }

     @Test
     void usuarioNaoEncontrado() {
        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

         ErroAutenticacaoException exception = assertThrows(ErroAutenticacaoException.class, () -> {
             this.usuarioService.autenticar(EMAIL, SENHA);
         });

         assertEquals("Usuario nao encontrado para o email informado.", exception.getMessage());
     }

     @Test
     void senhaInvalida() {
         when(this.usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));

         ErroAutenticacaoException exception = assertThrows(ErroAutenticacaoException.class, () -> {
             this.usuarioService.autenticar(EMAIL, "senha2");
         });

         assertEquals("Senha invalida.", exception.getMessage());
     }


    @Test
    void salvarUsuario() {
        /*
        This is a special case. The method salvarUsuario() validates an email through validarEmail() before saving an Usuario.
        However, I need to mock this method because I do not need to execute it, only the save() method. But the method validarEmail() is implemented on my UsuarioServiceImpl (not mocked)
        Therefore, I can use the annotation @Spy as UsuarioServiceImpl will not be totally mocked, only the methods that I need. The rest will work normally.
        */

        //Adding spy() directly on my usuarioService reference.
        //doNothing().when(spy(this.usuarioService)).validarEmail(anyString());

        //If I want this.usuarioService global, see the SPY NOTE on setUp() method.
        doNothing().when(this.usuarioService).validarEmail(anyString());

        when(this.usuarioRepository.save(any())).thenReturn(this.usuario);

        Usuario usuarioSalvo = this.usuarioService.salvarUsuario(this.usuario);

        assertNotNull(usuarioSalvo);
        assertEquals(usuarioSalvo.getId(), ID);
        assertEquals(usuarioSalvo.getNome(), NOME);
        assertEquals(usuarioSalvo.getEmail(), EMAIL);
        assertEquals(usuarioSalvo.getSenha(), SENHA);
    }

    @Test
    void naoSalvarUsuarioComEmailCadastrado() {
        doThrow(RegraDeNegocioException.class).when(this.usuarioService).validarEmail(EMAIL);

        assertThrows(RegraDeNegocioException.class, () -> usuarioService.salvarUsuario(usuario) ) ;

        verify(this.usuarioRepository, never()).save(this.usuario);
    }

    @Test
    void validarEmail() {
        when(this.usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        assertDoesNotThrow(() -> {
            this.usuarioService.validarEmail(EMAIL);//I could use any String here.
        });
    }

    @Test
    void verificarEmailJaCadastrado() {
        when(this.usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        //Expecting RegraDeNegocioException to be thrown. If RegraDeNegocioException is not Thrown an AssertionFailedError is show.
        assertThrows(RegraDeNegocioException.class, () -> {
            this.usuarioService.validarEmail(EMAIL);
        });
    }

}
