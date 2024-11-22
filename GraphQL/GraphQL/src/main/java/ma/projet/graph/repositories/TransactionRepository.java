package ma.projet.graph.repositories;

import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.Transaction;
import ma.projet.graph.entities.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find transactions by the associated account (compte) ID
    List<Transaction> findByCompte(Compte compte);

    // Find transactions by type
    List<Transaction> findByType(String type);


    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.type = :type")
    double sumByType(TypeTransaction type);



}
