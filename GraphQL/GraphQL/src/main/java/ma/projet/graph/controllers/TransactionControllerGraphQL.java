package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.Transaction;
import ma.projet.graph.entities.TransactionRequest;
import ma.projet.graph.entities.TypeTransaction;
import ma.projet.graph.repositories.TransactionRepository;
import ma.projet.graph.repositories.CompteRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@Controller

@AllArgsConstructor
public class TransactionControllerGraphQL {

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;

    // Query to fetch all transactions
    @QueryMapping
    public List<Transaction> allTransactions() {
        return transactionRepository.findAll();
    }

    // Query to fetch transactions by account ID
    @QueryMapping
    public List<Transaction> transactionsByCompte(@Argument Long id) {
        Compte compte = compteRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Compte  not found: "));
                return transactionRepository.findByCompte(compte);

    }

    @QueryMapping
    public Map<String, Object> transactionStats() {
        long count = transactionRepository.count(); // Total number of transactions
        double sumDepots = transactionRepository.sumByType(TypeTransaction.DEPOT); // Sum of deposit transactions
        double sumRetraits = transactionRepository.sumByType(TypeTransaction.RETRAIT); // Sum of withdrawal transactions

        return Map.of(
                "count", count,
                "sumDepots", sumDepots,
                "sumRetraits", sumRetraits
        );
    }


    // Mutation to create a new transaction
    @MutationMapping
    public Transaction addTransaction(@Argument TransactionRequest transactionRequest) {
        // Find the associated account
        Compte compte = compteRepository.findById(transactionRequest.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found"));

        // Create the new transaction
        Transaction transaction = new Transaction();
        transaction.setMontant(transactionRequest.getMontant());
        transaction.setType(transactionRequest.getType());
        transaction.setDate(transactionRequest.getDate());
        transaction.setCompte(compte);

        // Update the account solde based on transaction type
        if (transactionRequest.getType() == TypeTransaction.DEPOT) {
            compte.setSolde(compte.getSolde() + transactionRequest.getMontant());
        } else if (transactionRequest.getType() == TypeTransaction.RETRAIT) {
            if (compte.getSolde() < transactionRequest.getMontant()) {
                throw new RuntimeException("Insufficient funds for withdrawal");
            }
            compte.setSolde(compte.getSolde() - transactionRequest.getMontant());
        }

        // Save the updated account
        compteRepository.save(compte);

        // Save the transaction
        return transactionRepository.save(transaction);
    }


}
