package au.com.xpto.minhasfinancas.api.controllers;

import au.com.xpto.minhasfinancas.api.dto.UsuarioDTO;
import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.exceptions.ErroAutenticacaoException;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.services.LancamentoService;
import au.com.xpto.minhasfinancas.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test") //Used for tests. If I do not use Spring Profile (application.properties) I need to use this configuration.
class UsuarioControllerTest {

/*
     =============== Example 1 ===============

    @RunWith(SpringRunner.class)//Used in conjunction with the annotation below
    @WebMvcTest(controllers = {HenriqueController.class})//This annotation provides tests for SpringMVC Controllers. Only HenriqueController is considered (creates isolation).
        public class HenriqueControllerTest {

        @MockBean HenriqueService service; //provided by Spring Context
        @Autowired MockMvc mockMvc; //provided by Spring Contex

        With the configuration above, I do not need to use mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
        NOTE: I could use @AutoConfigureMockMvc as a class annotation and then @Autowired MockMvc mockMvc.
    }

    =============== Example 2 ===============

    @ExtendWith(MockitoExtension.class)//This annotation allows me to use the annotations @Mock and @InjectMocks
    public class HenriqueControllerTest {

        @Mock HenriqueService henriqueService;
        @Mock OwnerService ownerService;
        @Mock PetTypeService petTypeService;
        @InjectMocks PetController petController;
        MockMvc mockMvc;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(petController).build();
        }

         With the configuration above, I HAVE TO use mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
         NOTE: Instead of using @InjectMocks on PetController, is better to use the Constructor Injection
    }

    =============== Example 3 (the best to me) ===============

        //@ExtendWith(MockitoExtension.class) //I can user this annotation or the method MockitoAnnotations.initMocks(this);
        public class IngredientControllerTest {

            @Mock IngredientService ingredientService;
            @Mock UnitOfMeasureService unitOfMeasureService;
            @Mock RecipeService recipeService;
            IngredientController controller;
            MockMvc mockMvc;

            @BeforeEach
            public void setUp() throws Exception {
                MockitoAnnotations.initMocks(this); //I can use this method or the annotation @ExtendWith(MockitoExtension.class)
                controller = new IngredientController(ingredientService, recipeService, unitOfMeasureService); //Using Constructor Injection
                mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            }
        }
*/

    @Mock
    UsuarioService usuarioService;

    @Mock
    LancamentoService lancamentoService;

    UsuarioController usuarioController;

    MockMvc mockMvc; // I am using the MockitoAnnotations.initMocks(this), therefore, I do not need to Autowire this attribute.

    private static final String BASE_URL = "/api/usuarios";
    private static final String EMAIL = "usuario@email.com";
    private static final String SENHA = "1234";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.usuarioController = new UsuarioController(this.usuarioService, this.lancamentoService);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    void autenticar() throws Exception {
        Usuario usuario = Usuario.builder().email(EMAIL).senha(SENHA).build();
        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();

        when(this.usuarioService.autenticar(EMAIL, SENHA)).thenReturn(usuario);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL.concat("/autenticar"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(usuarioDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(usuario.getId()))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("email").value(usuario.getEmail()));

//        //when/then
//        mockMvc.perform(put(CustomerController.BASE_URL + "/1")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(customer)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.firstName", equalTo("Fred")))
//                .andExpect(jsonPath("$.lastName", equalTo("Flintstone")))
//                .andExpect(jsonPath("$.customerUrl", equalTo(CustomerController.BASE_URL + "/1")));
    }

    @Test
    void erroAutenticacao() throws Exception {
        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();

        when(this.usuarioService.autenticar(EMAIL, SENHA)).thenThrow(ErroAutenticacaoException.class);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL.concat("/autenticar"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(usuarioDTO))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void salvar() throws Exception {
        Usuario usuario = Usuario.builder().email(EMAIL).senha(SENHA).build();
        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();

        when(this.usuarioService.salvarUsuario(any())).thenReturn(usuario);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuarioDTO))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(usuario.getId()))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    void salvarUsuarioInvalido() throws Exception {
        Usuario usuario = Usuario.builder().email(EMAIL).senha(SENHA).build();
        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(EMAIL).senha(SENHA).build();

//        public Usuario salvarUsuario(Usuario usuario) {
//            this.validarEmail(usuario.getEmail());
//            return this.usuarioRepository.save(usuario);
//        }

        when(this.usuarioService.salvarUsuario(any())).thenThrow(RegraDeNegocioException.class);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuarioDTO))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void obterSaldo() {
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
