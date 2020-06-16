package au.com.xpto.minhasfinancas.domain.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario", schema = "financas")//Test if I can remove the schema property
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
