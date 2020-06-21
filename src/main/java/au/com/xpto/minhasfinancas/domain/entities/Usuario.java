package au.com.xpto.minhasfinancas.domain.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuario", schema = "financas")//If I remove the schema property, the application may get lost.
public class Usuario {
    //this package 'domain' could also be named as model, entities or even bean

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome") //I could avoid this annotation, but the idea is to exemplify its use.
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

}
