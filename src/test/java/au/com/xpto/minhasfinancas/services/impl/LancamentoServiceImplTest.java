package au.com.xpto.minhasfinancas.services.impl;

import au.com.xpto.minhasfinancas.domain.entities.Lancamento;
import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.domain.enums.StatusLancamento;
import au.com.xpto.minhasfinancas.domain.enums.TipoLancamento;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.repositories.LancamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//https://stackoverflow.com/questions/60308578/extendwithspringextension-class-vs-extendwithmockitoextension-class
//@ExtendWith(MockitoExtension.class) makes available (disponibiliza) Spring test framework features. It replaces the deprecated JUnit4 @RunWith(SpringJUnit4ClassRunner.class)
//@ExtendWith(SpringExtension.class) makes available Mockito and don't have to involve Spring. It replaces the deprecated JUnit4 @RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
class LancamentoServiceImplTest {

//    @SpyBean
    LancamentoServiceImpl lancamentoService;

//    @MockBean
    @Mock
    private LancamentoRepository lancamentoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.lancamentoService = spy(new LancamentoServiceImpl(this.lancamentoRepository)); //Using Constructor injection for Spy
    }

    @Test
    void salvarLancamento() {
        doNothing().when(this.lancamentoService).validarLancamento(any());

        Lancamento lancamentoSalvo = this.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);//According to my logic, all new Lancamento insertion is PENDENTE
        when(lancamentoRepository.save(any())).thenReturn(lancamentoSalvo);

        //execucao
        Lancamento lancamento = lancamentoService.salvarLancamento(this.criarLancamento());
        assertEquals(lancamentoSalvo.getId(), lancamento.getId());
        assertEquals(lancamentoSalvo.getStatus(), StatusLancamento.PENDENTE);
    }

    @Test
    void naoSalvarLancamentoComErroValidacao() {
        //I will execute the step this.validarLancamento(lancamento) and return RegraDeNegocioException. I need to make sure lancamento is not saved.

        Lancamento lancamento = this.criarLancamento();
        doThrow(RegraDeNegocioException.class).when(this.lancamentoService).validarLancamento(lancamento);
        //doThrow(RegraDeNegocioException.class).when(this.lancamentoService.validarLancamento(lancamento)); I cannot use this way as when() does not accept void return.

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.salvarLancamento(lancamento);
        });

        verify(this.lancamentoRepository, never()).save(lancamento);//save() was never invoked

    }

    @Test
    void atualizarLancamento() {
        Lancamento lancamento = this.criarLancamento();
        lancamento.setId(1L);

        doNothing().when(this.lancamentoService).validarLancamento(any());
        when(this.lancamentoRepository.save(any())).thenReturn(lancamento);

        this.lancamentoService.atualizarLancamento(lancamento);

        assertNotNull(lancamento);
        assertEquals(1, lancamento.getId());
        verify(this.lancamentoRepository).save(lancamento);//save() was invoked
    }

    @Test
    void naoAtualizarLancamentoNaoSalvo() {
        Lancamento lancamento = this.criarLancamento();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            this.lancamentoService.atualizarLancamento(lancamento);
        });

        verify(this.lancamentoRepository, never()).save(lancamento);

    }

    @Test
    void deletarLancamento() {
        Lancamento lancamento = this.criarLancamento();
        lancamento.setId(1L);

        this.lancamentoService.deletarLancamento(lancamento);

        verify(this.lancamentoRepository).delete(lancamento);//delete() was invoked
    }

    @Test
    void naoDeletarLancamentoNaoSalvo() {
        Lancamento lancamento = this.criarLancamento();

        assertThrows(NullPointerException.class, () -> {
            this.lancamentoService.deletarLancamento(lancamento);
        });

        verify(this.lancamentoRepository, never()).delete(lancamento);
    }

    @Test
    void listarLancamentos() {
        Lancamento lancamento = this.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);
        when(lancamentoRepository.findAll(any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = this.lancamentoService.listarLancamentos(lancamento);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertIterableEquals(lista, resultado);
    }

    @Test
    void atualizarStatus() {
        Lancamento lancamento = this.criarLancamento();
        lancamento.setId(1L);

        //I cannot use doNothing on atualizarLancamento() because this method is not void. So, I need to use doReturn()
        //doNothing().when(this.lancamentoService).atualizarLancamento(any());
        doReturn(lancamento).when(this.lancamentoService).atualizarLancamento(any());//If I wanted I could use doReturn(new Lancamento())...

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        this.lancamentoService.atualizarStatus(lancamento, novoStatus);

        assertEquals(lancamento.getStatus(), novoStatus);
        verify(this.lancamentoService).atualizarLancamento(lancamento);
    }

    @Test
    void validarLancamento() {
        Lancamento lancamento = new Lancamento();

        RegraDeNegocioException assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe uma descricao valida.", assertThrows.getMessage());

        lancamento.setDescricao("");
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe uma descricao valida.", assertThrows.getMessage());

        lancamento.setDescricao("Salario");
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um mes valido.", assertThrows.getMessage());

        lancamento.setMes(13);
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um mes valido.", assertThrows.getMessage());

        lancamento.setMes(-10);
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um mes valido.", assertThrows.getMessage());

        lancamento.setMes(1);
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um ano valido.", assertThrows.getMessage());

        lancamento.setAno(123);
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um ano valido.", assertThrows.getMessage());

        lancamento.setAno(2020);
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um usuario.", assertThrows.getMessage());

        lancamento.setUsuario(new Usuario());
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um usuario.", assertThrows.getMessage());

        lancamento.getUsuario().setId(1L);
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um valor valido.", assertThrows.getMessage());

        lancamento.setValor(BigDecimal.ZERO);
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um valor valido.", assertThrows.getMessage());

        lancamento.setValor(new BigDecimal(200));
        assertThrows = assertThrows(RegraDeNegocioException.class, () -> {
            this.lancamentoService.validarLancamento(lancamento);
        });
        assertEquals("Informe um tipo de lancamento.", assertThrows.getMessage());
    }

    @Test
    void lancamentoPorId() {
        Lancamento lancamento = this.criarLancamento();
        lancamento.setId(1L);

        when(this.lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamento));
        Optional<Lancamento> lancamentoPorId = this.lancamentoService.lancamentoPorId(1L);

        assertTrue(lancamentoPorId.isPresent());
        assertEquals(1L, lancamentoPorId.get().getId());
    }

    @Test
    void naoRetornarLancamentoPorIdQuandoLancamentoNaoExiste() {
        Lancamento lancamento = this.criarLancamento();
        lancamento.setId(1L);

        when(this.lancamentoRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Lancamento> lancamentoPorId = this.lancamentoService.lancamentoPorId(1L);

        assertFalse(lancamentoPorId.isPresent());
    }

    @Test
    void obterSaldoPorUsuario() {

    }

    private Lancamento criarLancamento() {
        return Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("descricao 1")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

}
