package au.com.xpto.minhasfinancas.domain.entities;

import au.com.xpto.minhasfinancas.domain.enums.StatusLancamento;
import au.com.xpto.minhasfinancas.domain.enums.TipoLancamento;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lancamento", schema = "financas")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private Integer mes;
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private BigDecimal valor;

    //@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalTime dataCadastro;//Java 8

    @Enumerated(value = EnumType.STRING) //Using EnumType.ORDINAL means that it would return the index of the requested value. For example: RECEITA returns 1, DESPESA returns 0.
    private TipoLancamento tipo;

    @Enumerated(value = EnumType.STRING)
    private StatusLancamento status;

}
