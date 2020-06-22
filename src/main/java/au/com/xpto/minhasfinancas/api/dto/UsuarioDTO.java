package au.com.xpto.minhasfinancas.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    //In Jonh's course ID was not exposed

    private String email;
    private String nome;
    private String senha;

}
