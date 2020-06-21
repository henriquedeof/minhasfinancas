package au.com.xpto.minhasfinancas.api.controllers;

import au.com.xpto.minhasfinancas.api.dto.UsuarioDTO;
import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import au.com.xpto.minhasfinancas.exceptions.ErroAutenticacaoException;
import au.com.xpto.minhasfinancas.exceptions.RegraDeNegocioException;
import au.com.xpto.minhasfinancas.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    //I created this class as usuarioController but it also would be usuarioResource

    private UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
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

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO usuarioDTO){

        /*
        The commented code below 'apparently' is the best way of using Controller methods. Note that the signature of the method returns a DTO object, not a ResponseEntity.

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

}
