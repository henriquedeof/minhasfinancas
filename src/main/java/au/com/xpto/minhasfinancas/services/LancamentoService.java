package au.com.xpto.minhasfinancas.services;

import au.com.xpto.minhasfinancas.domain.entities.Lancamento;
import au.com.xpto.minhasfinancas.domain.enums.StatusLancamento;

import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Lancamento salvarLancamento(Lancamento lancamento);

    Lancamento atualizarLancamento(Lancamento lancamento);

    void deletarLancamento(Lancamento lancamento);

    List<Lancamento> listarLancamentos(Lancamento lancamentoFiltro);

    void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento);

    void validarLancamento(Lancamento lancamento);

    Optional<Lancamento> lancamentoPorId(Long id);
}
