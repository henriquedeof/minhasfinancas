package au.com.xpto.minhasfinancas.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoDTO {
    //In Jonh's course ID was not exposed

    private Long id;
    private String descricao;
    private Integer mes;
    private Integer ano;

    //Referencing Usuario object through its ID
    private Long usuario;

    private BigDecimal valor;

    //Referencing TipoLancamento and StatusLancamento through its description using String
    private String tipo;
    private String status;

}
