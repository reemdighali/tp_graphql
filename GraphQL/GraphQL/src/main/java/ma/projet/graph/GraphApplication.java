package ma.projet.graph;

import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.TypeCompte;
import ma.projet.graph.repositories.CompteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class GraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphApplication.class, args);
	}


	@Bean
	CommandLineRunner start(CompteRepository compteRepository){
		return args -> {
			// Initialisation des comptes
			compteRepository.save(new Compte(null, Math.random()*9000, new Date(), TypeCompte.EPARGNE,null));
			compteRepository.save(new Compte(null, Math.random()*9000, new Date(), TypeCompte.COURANT,null));
			compteRepository.save(new Compte(null, Math.random()*9000, new Date(), TypeCompte.EPARGNE,null));
		};
	}
}
