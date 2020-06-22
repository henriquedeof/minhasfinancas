package au.com.xpto.minhasfinancas.api.controllers;

import au.com.xpto.minhasfinancas.api.dto.UsuarioDTO;
import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.exceptions.ErroAutenticacaoException;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.services.LancamentoService;
import au.com.xpto.minhasfinancas.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    //I created this class as usuarioController but it also would be usuarioResource

    /*
     John's course applies MapStruct to map domain classes into DTO classes and vice versa. A good example of its use is:

    org.mapstruct.@Mapper
    public interface VendorMapper {
        VendorMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(VendorMapper.class);//Using MapStruct
        VendorDTO vendorToVendorDTO(Vendor vendor);
        Vendor vendorDtoToVendor(VendorDTO vendorDTO);
    }
    */

    private final UsuarioService service;
    private final LancamentoService lancamentoService;

    public UsuarioController(UsuarioService service, LancamentoService lancamentoService) {
        this.service = service;
        this.lancamentoService = lancamentoService;
    }

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO usuarioDTO){

        try{
            Usuario usuario = this.service.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
            return ResponseEntity.ok(usuario);
        }catch (ErroAutenticacaoException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity salvar(@RequestBody UsuarioDTO usuarioDTO){

        /*
        John's example below
        @ResponseStatus(HttpStatus.OK)
        public CategoryDTO getCategoryByName(@PathVariable String name){
        */

        Usuario usuario = Usuario.builder().nome(usuarioDTO.getNome()).email(usuarioDTO.getEmail()).senha(usuarioDTO.getSenha()).build();
        try {
            Usuario usuarioSalvo = this.service.salvarUsuario(usuario);
            return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.CREATED);
        }catch (RegraDeNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable Long id){
        Optional<Usuario> usuario = this.service.usuarioPorId(id);

        if(!usuario.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        BigDecimal saldo = this.lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }

}
