package au.com.xpto.minhasfinancas.services.impl;

import au.com.xpto.minhasfinancas.domain.entities.Lancamento;
import au.com.xpto.minhasfinancas.domain.enums.StatusLancamento;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.repositories.LancamentoRepository;
import au.com.xpto.minhasfinancas.services.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    //In this class, the implemented methods are receiving/returning the Original domain objects instead of DTOs. It may not be a good practice.

    private final LancamentoRepository lancamentoRepository;

    public LancamentoServiceImpl(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    @Override
    @Transactional
    public Lancamento salvarLancamento(Lancamento lancamento) {
        //the method save() has the functionality of creating or updating an entity.

        this.validarLancamento(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return this.lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizarLancamento(Lancamento lancamento) {
        //The difference between salvarLancamento() and atualizarLancamento() is that in the atualizarLancamento() method, the lancamento parameter must have an ID set.
        //In Jonh's course, Long id parameter is part of this method, unlike this given example. Example: public VendorDTO updateVendorDTO(Long id, VendorDTO vendorDTO) { ... }

        Objects.requireNonNull(lancamento.getId());
        this.validarLancamento(lancamento);
        return this.lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletarLancamento(Lancamento lancamento) {
        //I just can delete an element that is already on my database. So, lancamento parameter must have an ID.

        Objects.requireNonNull(lancamento.getId());
        this.lancamentoRepository.delete(lancamento);
    }

    @Override
    public List<Lancamento> listarLancamentos(Lancamento lancamentoFiltro) {
        //Using "Query By Example" that is provided by Spring Data to create filters for this search.

        Example example = Example.of(lancamentoFiltro,
            ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );

        return this.lancamentoRepository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento) {
        lancamento.setStatus(statusLancamento);
        this.atualizarLancamento(lancamento);
    }

    @Override
    public void validarLancamento(Lancamento lancamento) {
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
            throw new RegraDeNegocioException("Informe uma descricao valida.");
        }

        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
            throw new RegraDeNegocioException("Informe um mes valido.");
        }

        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ) {
            throw new RegraDeNegocioException("Informe um ano valido.");
        }

        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
            throw new RegraDeNegocioException("Informe um usuario.");
        }

        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
            throw new RegraDeNegocioException("Informe um valor valido.");
        }

        if(lancamento.getTipo() == null) {
            throw new RegraDeNegocioException("Informe um tipo de lancamento.");
        }
    }

    @Override
    public Optional<Lancamento> lancamentoPorId(Long id) {
        //I should implement NOT FOUND here.
        return this.lancamentoRepository.findById(id);
    }

}
