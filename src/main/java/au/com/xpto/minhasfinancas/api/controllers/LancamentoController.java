package au.com.xpto.minhasfinancas.api.controllers;

import au.com.xpto.minhasfinancas.api.dto.AtualizaStatusDTO;
import au.com.xpto.minhasfinancas.api.dto.LancamentoDTO;
import au.com.xpto.minhasfinancas.domain.entities.Lancamento;
import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.domain.enums.StatusLancamento;
import au.com.xpto.minhasfinancas.domain.enums.TipoLancamento;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.services.LancamentoService;
import au.com.xpto.minhasfinancas.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
    /*
    John's course applies MapStruct to map domain classes into DTO classes and vice versa. A good example of its use is:

    org.mapstruct.@Mapper
    public interface VendorMapper {
        VendorMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(VendorMapper.class);//Using MapStruct
        VendorDTO vendorToVendorDTO(Vendor vendor);
        Vendor vendorDtoToVendor(VendorDTO vendorDTO);
    }
    */

    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;

    public LancamentoController(LancamentoService lancamentoService, UsuarioService usuarioService) {
        this.lancamentoService = lancamentoService;
        this.usuarioService = usuarioService;
    }

    /*
    The methods on this class are executing too much business logic. This business logic should be implemented on the Service class (UsuarioService) not on Controllers.
    For example, there are try/catch being implemented to evaluate.
    Additionally, probably I need to create Custom Exceptions using the annotation @ControllerAdvice. Here follow some example:
        https://mkyong.com/spring-boot/spring-rest-error-handling-example/
        https://howtodoinjava.com/spring-restful/exception-handling-example/
        https://spring.io/guides/tutorials/bookmarks/
    */

    @PostMapping
    public ResponseEntity salvarLancamento(@RequestBody LancamentoDTO lancamentoDTO){
        try{
            Lancamento lancamento = this.converter(lancamentoDTO);
            lancamento = this.lancamentoService.salvarLancamento(lancamento);

            return new ResponseEntity(lancamento, HttpStatus.CREATED);
            //return ResponseEntity.ok(lancamento); //I can use ok() but it is recommended to use CREATED.

        }catch (RegraDeNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable Long id, @RequestBody LancamentoDTO lancamentoDTO){
        return this.lancamentoService.lancamentoPorId(id).map(entity -> {
            try{

                Lancamento lancamento = this.converter(lancamentoDTO);
                lancamento.setId(entity.getId());
                this.lancamentoService.atualizarLancamento(lancamento);
                return ResponseEntity.ok(lancamento);

             }catch (RegraDeNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lancamento nao encontrado na base de dados.", HttpStatus.BAD_REQUEST));//Poderia ser NOT_FOUND
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus( @PathVariable Long id, @RequestBody AtualizaStatusDTO dto ) {
        return lancamentoService.lancamentoPorId(id).map( entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

            if(statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Nao foi possivel atualizar o status do lancamento, envie um status valido.");
            }

            try {
                entity.setStatus(statusSelecionado);
                lancamentoService.atualizarLancamento(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraDeNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }).orElseGet( () ->
                new ResponseEntity("Lancamento nao encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable Long id){
        //According to Jonh's training, delete receives only ID (no DTO is necessary).
        //TODO - Verify what the Repository shows when I try to delete an objetc and it does not exist.

        return this.lancamentoService.lancamentoPorId(id).map(lancamento -> {
            this.lancamentoService.deletarLancamento(lancamento);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lancamento nao encontrado na base de dados.", HttpStatus.BAD_REQUEST));//Poderia ser NOT_FOUND
    }

    @GetMapping
    public ResponseEntity listarLancamento(
            //@RequestParam java.util.Map<String, String> map, I could use this param to substitute the other ones below. NOTE: This way all parameters are optional.

            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario //idUsuario is mandatory
        ){

        //I still do not have filter for tipo, so, my search will bring all kinds of tipo.

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = this.usuarioService.usuarioPorId(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Usuario nao encontrado para o Id informado.");
        }else{
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = this.lancamentoService.listarLancamentos(lancamentoFiltro);

        return ResponseEntity.ok(lancamentos);
    }


    //This method should be placed in a kind of converter class.
    private LancamentoDTO converter(Lancamento lancamento) {
        // TODO: 21/06/2020 - create a converter for this method

        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId())
                .build();

    }

    //This method should be placed in a kind of converter class.
    private Lancamento converter(LancamentoDTO dto) {
        // TODO: 21/06/2020 - create a converter for this method

        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = this.usuarioService
                .usuarioPorId(dto.getUsuario())
                .orElseThrow( () -> new RegraDeNegocioException("Usuario nao encontrado para o Id informado.") );//Exception should not be thrown on a Service class.

        lancamento.setUsuario(usuario);

        if(dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if(dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        return lancamento;
    }


}
