package ma.projet.graph.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private Long compteId;       // ID of the associated account
    private Float montant;      // Amount of the transaction
    private TypeTransaction type;         // Type of the transaction (e.g., DEPOSIT, WITHDRAWAL)
    private LocalDateTime date;



    // Date of the transaction
}
