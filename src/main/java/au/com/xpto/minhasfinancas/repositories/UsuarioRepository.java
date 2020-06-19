package au.com.xpto.minhasfinancas.repositories;

import au.com.xpto.minhasfinancas.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //Spring recognizes this method as Query Method, which means that I do not have to create query for this action as Spring will automatically look for email.
    //Using a Query Method that looks for an existing email
    Boolean existsByEmail(String email);

    //When I use the convention 'findByXxxx' Spring knows that it needs to get an Usuario by its email.
    Optional<Usuario> findByEmail(String email);


    //=========================================================================================================
    //===================================== More examples below ===============================================
    //=========================================================================================================

    //Using two fields
    //Optional<Usuario> findByEmailAndNome(String email, String nome);

    //When I use the convention 'findByXxxx' Spring knows that it needs to get an Usuario by its email.
    //Optional<Usuario> findByEmail(String email);

}
