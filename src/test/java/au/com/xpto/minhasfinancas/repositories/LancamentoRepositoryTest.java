package au.com.xpto.minhasfinancas.repositories;

import au.com.xpto.minhasfinancas.domain.entities.Lancamento;
import au.com.xpto.minhasfinancas.domain.enums.StatusLancamento;
import au.com.xpto.minhasfinancas.domain.enums.TipoLancamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    Lancamento lancamento;

    @BeforeEach
    void setUp() {
        lancamento = Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("descricao 1")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

    @Test
    void salvarLancamento(){
        Lancamento lancamentoSalvo = this.lancamentoRepository.save(lancamento);

        assertNotNull(lancamentoSalvo);
    }

    @Test
    void deletarLancamento(){
        this.lancamentoRepository.save(this.lancamento);

        Lancamento lancamentoSalvo = this.lancamentoRepository.findById(this.lancamento.getId()).get();

        this.lancamentoRepository.delete(lancamentoSalvo);

        Optional<Lancamento> lancamentoOptional = this.lancamentoRepository.findById(lancamentoSalvo.getId());

        assertFalse(lancamentoOptional.isPresent());
    }

    @Test
    void atualizarLancamento(){
        Lancamento lancamentoSalvo = this.lancamentoRepository.save(this.lancamento);

        lancamentoSalvo.setAno(2015);
        lancamentoSalvo.setDescricao("descricao atualizada");
        lancamentoSalvo.setStatus(StatusLancamento.CANCELADO);

        this.lancamentoRepository.save(lancamentoSalvo);
        Lancamento optionalLancamento = this.lancamentoRepository.findById(lancamentoSalvo.getId()).get();

        assertEquals(2015, optionalLancamento.getAno());
        assertEquals("descricao atualizada", optionalLancamento.getDescricao());
        assertEquals(optionalLancamento.getStatus(), StatusLancamento.CANCELADO);
    }

    @Test
    void lancamentoPorId(){
        Lancamento lancamentoSalvo = this.lancamentoRepository.save(this.lancamento);
        Optional<Lancamento> lancamentoOptional = this.lancamentoRepository.findById(lancamentoSalvo.getId());

        assertTrue(lancamentoOptional.isPresent());

    }

    @Test
    void obterSaldoPorTipoLancamentoEUsuarioEStatus() {

    }


}
