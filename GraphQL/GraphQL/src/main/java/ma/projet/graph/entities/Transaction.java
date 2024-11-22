package ma.projet.graph.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float montant;

    @Enumerated(EnumType.STRING)
    private TypeTransaction type; // DEPOSIT or WITHDRAWAL

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte; // The account associated with this transaction

}
