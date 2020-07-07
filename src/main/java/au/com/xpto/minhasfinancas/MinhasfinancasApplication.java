package au.com.xpto.minhasfinancas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@SpringBootApplication
//@EnableWebMvc //This annotataion enables CORS policy between React and Spring Boot. But it BREAKS MY REPOSITORY TESTS when I use @DataJpaTest
//public class MinhasfinancasApplication implements WebMvcConfigurer {

@SpringBootApplication
public class MinhasfinancasApplication {

//    Approach not recommended as it does not work properly with testing using the @DataJpaTest annotation
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//    }


    //In my opinion, best way to solve the CORS problem without causing any problems with tests or classes loading
    //todo: I need to put this method in a different class that uses the annotation @Configuration
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //Accepting any URL and the methods: GET, POST, PUT, DELETE and OPTIONS.
                registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");


                //Below I am registering two Resources into my mapping. I have autenticar and salvar added with their individual configurations.
//                registry.addMapping("/api/usuarios/autenticar")
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedOrigins("http://localhost:9000", "https://myclientIP.com");
//
//                registry.addMapping("/api/usuarios/salvar")
//                        .allowedMethods("GET", "POST")
//                        .allowedOrigins("http://localhost:9002", "https://myclientIP2.com");

            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(MinhasfinancasApplication.class, args);
    }

}



