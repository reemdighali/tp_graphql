package ma.projet.graph.dto;

import lombok.Data;
import ma.projet.graph.entities.TypeCompte;

@Data
public class CompteRequest {
    private Double solde;
    private String dateCreation; // Keep this as a String for easier parsing
    private TypeCompte type;
}
